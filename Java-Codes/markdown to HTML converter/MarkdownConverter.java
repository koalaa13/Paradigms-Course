package md2html;

import base.Triple;

import java.util.*;

class MarkdownConverter {
    private final MarkdownSource source;
    private static Map<String, String> identifiers = new HashMap<>();
    private static Map<Character, String> specialHtmlSymbols = new HashMap<>();
    private static Set<Character> specialMarkdownSymbols;
    private static Map<String, Integer> identifierLen = new HashMap<>();
    private static final String begPar = "<p>", endPar = "</p>";

    static {
        specialHtmlSymbols.put('<', "&lt;");
        specialHtmlSymbols.put('>', "&gt;");
        specialHtmlSymbols.put('&', "&amp;");

        specialMarkdownSymbols = Set.of('*', '-', '_', '`', '[', ']', '(', ')', '+', '~');

        identifiers.put("++", "u");
        identifiers.put("~", "mark");
        identifiers.put("_", "em");
        identifiers.put("*", "em");
        identifiers.put("__", "strong");
        identifiers.put("**", "strong");
        identifiers.put("--", "s");
        identifiers.put("`", "code");

        for (Map.Entry<String, String> i : identifiers.entrySet()) {
            identifierLen.put("<" + i.getValue() + ">", i.getKey().length());
            identifierLen.put("</" + i.getValue() + ">", i.getKey().length());
        }
        identifierLen.put("", 1);
    }

    MarkdownConverter(MarkdownSource source) {
        this.source = source;
    }

    private void add(Map<String, Integer> opened, Map<Integer, String> toReplace, String pat, int pos) {
        if (opened.containsKey(pat)) {
            final String tag = identifiers.get(pat);
            toReplace.put(opened.get(pat), "<" + tag + ">");
            toReplace.put(pos, "</" + tag + ">");
            opened.remove(pat);
        } else {
            opened.put(pat, pos);
        }
    }

    private boolean check(String s, char c, int pos) {
        return pos < s.length() && c == s.charAt(pos);
    }

    private boolean discharge(char c, Map<String, Integer> opened, Map<Integer, String> toReplace, String string, int i) {
        boolean flag = false;
        if (c == '\\') {
            if (i + 1 < string.length() && specialMarkdownSymbols.contains(string.charAt(i + 1))) {
                toReplace.put(i, "");
                flag = true;
            }
        }
        if (c == '*' || c == '_') {
            String pat = String.valueOf(c);
            if (check(string, c, i + 1)) {
                pat += c;
                flag = true;
            }
            add(opened, toReplace, pat, i);
        }
        if (c == '+' || c == '-') {
            if (check(string, c, i + 1)) {
                String pat = String.valueOf(c);
                pat += c;
                add(opened, toReplace, pat, i);
                flag = true;
            }
        }
        if (c == '~' || c == '`') {
            add(opened, toReplace, String.valueOf(c), i);
        }
        return flag;
    }

    private String convertString(String string) {
        StringBuilder res = new StringBuilder();
        Map<String, Integer> opened = new HashMap<>();
        Map<Integer, String> toReplace = new HashMap<>();
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (discharge(c, opened, toReplace, string, i)) {
                i++;
            }
        }
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (toReplace.containsKey(i)) {
                final String s = toReplace.get(i);
                res.append(s);
                i += identifierLen.get(s) - 1;
            } else {
                if (specialHtmlSymbols.containsKey(c)) {
                    res.append(specialHtmlSymbols.get(c));
                } else {
                    res.append(c);
                }
            }
        }
        return res.toString();
    }

    private String convertPar(String par) throws MarkdownException {
        StringBuilder res = new StringBuilder();
        int countHashtags = 0, indInPar = 0;
        while (indInPar < par.length() && par.charAt(indInPar) == '#') {
            indInPar++;
            countHashtags++;
        }
        int countSkipped = skipWhiteSpacesParagraph(par, indInPar);
        indInPar += countSkipped;
        boolean isHeadline = par.length() > 0 && par.charAt(0) == '#' && countSkipped > 0;
        if (isHeadline) {
            res.append("<h").append(countHashtags).append(">");
        } else {
            res.append(begPar);
            for (int i = 0; i < countHashtags; ++i) {
                res.append('#');
            }
            for (int i = 0; i < countSkipped; ++i) {
                res.append(' ');
            }
        }
        Map<String, Integer> opened = new HashMap<>();
        Map<Integer, String> toReplace = new HashMap<>();
        Map<Integer, Triple<String, String, Integer>> images = new HashMap<>();
        Map<Integer, Triple<String, String, Integer>> links = new HashMap<>();
        for (int i = indInPar; i < par.length(); ++i) {
            char c = par.charAt(i);
            int j, count;
            switch (c) {
                case '!':
                    if (check(par, '[', i + 1)) {
                        StringBuilder picture = new StringBuilder(), pictureLink = new StringBuilder();
                        count = 0;
                        j = i + 2;
                        while (j < par.length() && par.charAt(j) != ']') {
                            picture.append(par.charAt(j++));
                            ++count;
                        }
                        expected(par, j, ']');
                        expected(par, j + 1, '(');
                        j += 2;
                        while (j < par.length() && par.charAt(j) != ')') {
                            pictureLink.append(par.charAt(j++));
                            ++count;
                        }
                        expected(par, j, ')');
                        images.put(i, Triple.of(picture.toString(), pictureLink.toString(), count + 4));
                        i = j;
                    }
                    break;
                case '[':
                    StringBuilder link = new StringBuilder(), writing = new StringBuilder();
                    count = 0;
                    j = i + 1;
                    while (j < par.length() && par.charAt(j) != ']') {
                        writing.append(par.charAt(j++));
                        ++count;
                    }
                    expected(par, j, ']');
                    expected(par, j + 1, '(');
                    j += 2;
                    while (j < par.length() && par.charAt(j) != ')') {
                        link.append(par.charAt(j++));
                        ++count;
                    }
                    expected(par, j, ')');
                    links.put(i, Triple.of(writing.toString(), link.toString(), count + 3));
                    i = j;
                    break;
                default:
                    if (discharge(c, opened, toReplace, par, i)) {
                        i++;
                    }
                    break;
            }
        }
        for (int i = indInPar; i < par.length(); ++i) {
            char c = par.charAt(i);
            if (toReplace.containsKey(i)) {
                final String s = toReplace.get(i);
                res.append(s);
                i += identifierLen.get(s) - 1;
            } else {
                if (images.containsKey(i)) {
                    final String s1 = images.get(i).first(), s2 = images.get(i).second();
                    res.append("<img alt='").append(s1).append("\' src=\'").append(s2).append("\'>");
                    i += images.get(i).third();
                } else {
                    if (links.containsKey(i)) {
                        final String s1 = links.get(i).first(), s2 = links.get(i).second();
                        res.append("<a href=\'").append(s2).append("\'>").append(convertString(s1)).append("</a>");
                        i += links.get(i).third();
                    } else {
                        if (specialHtmlSymbols.containsKey(c)) {
                            res.append(specialHtmlSymbols.get(c));
                        } else {
                            res.append(c);
                        }
                    }
                }
            }
        }
        if (isHeadline) {
            res.append("</h").append(countHashtags).append(">");
        } else {
            res.append(endPar);
        }
        res.append('\n');
        return res.toString();
    }

    String convert() throws MarkdownException {
        StringBuilder res = new StringBuilder();
        for (; ; ) {
            String paragraph = source.getParagraph();
            if (paragraph.length() == 0) {
                continue;
            }
            res.append(convertPar(paragraph));
            if (source.getChar() == MarkdownSource.END) {
                break;
            }
        }
        return res.toString();
    }

    private void expected(String s, int pos, char c) throws MarkdownException {
        if (!check(s, c, pos)) {
            throw source.error("Expected '%c'", c);
        }
    }

    private int skipWhiteSpacesParagraph(String paragraph, int ind) {
        int countSkipped = 0;
        while (Character.isWhitespace(paragraph.charAt(ind))) {
            ind++;
            countSkipped++;
        }
        return countSkipped;
    }
}
