package ru.itmo.rain.maksimov.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;
import ru.itmo.rain.maksimov.Helper;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class Implementor implements JarImpler {
    private static final String DEFAULT_OBJECT = " null";
    private final static String DEFAULT_PRIMITIVE = " 0";
    private final static String DEFAULT_VOID = "";
    private final static String DEFAULT_BOOLEAN = " false";
    private final static String END_OF_LINE = System.lineSeparator();
    private final static String TAB = "    ";
    protected final static Cleaner DELETER = new Cleaner();

    private static class Cleaner extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }

    private static class MethodWrapper {

        private final Method method;

        private MethodWrapper(Method method) {
            this.method = method;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Method that = ((MethodWrapper) o).getMethod();
            return Arrays.equals(method.getParameterTypes(), that.getParameterTypes()) &&
                    method.getReturnType().equals(that.getReturnType()) &&
                    method.getName().equals(that.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(Arrays.hashCode(method.getParameterTypes()),
                    method.getReturnType().hashCode(),
                    method.getName().hashCode());
        }

        public Method getMethod() {
            return method;
        }

    }

    private static void cleanDir(Path dir) throws IOException {
        Files.walkFileTree(dir, DELETER);
    }

    private String getClassName(Class<?> token) {
        return token.getSimpleName() + "Impl";
    }

    private String getTabs(int count) {
        return TAB.repeat(Math.max(0, count));
    }

    private String getPackage(Class<?> token) {
        StringBuilder res = new StringBuilder();
        if (!"".equals(token.getPackageName())) {
            res.append("package ").append(token.getPackageName()).append(';').append(END_OF_LINE);
        }
        res.append(END_OF_LINE);
        return res.toString();
    }

    private String getClassHead(Class<?> token) {
        return getPackage(token) + "public class " + getClassName(token) + ' ' +
                (token.isInterface() ? "implements " : "extends ") +
                token.getCanonicalName() + " {" + END_OF_LINE;
    }

    private String getReturnTypeAndName(Class<?> token, Executable executable) {
        if (executable instanceof Method) {
            Method tmp = (Method) executable;
            return tmp.getReturnType().getCanonicalName() + ' ' + tmp.getName();
        } else {
            return getClassName(token);
        }
    }

    private String getParam(Parameter parameter, boolean typeNeeded) {
        return (typeNeeded ? parameter.getType().getCanonicalName() + ' ' : "") + parameter.getName();
    }

    private String getParams(Executable executable, boolean typeNeeded) {
        return Arrays.stream(executable.getParameters())
                .map(parameter -> getParam(parameter, typeNeeded))
                .collect(Collectors.joining(", ", "(", ")"));
    }

    private String getExceptions(Executable executable) {
        StringBuilder res = new StringBuilder();
        Class<?>[] exceptions = executable.getExceptionTypes();
        if (exceptions.length > 0) {
            res.append(" throws ");
        }
        res.append(Arrays.stream(exceptions)
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(", ")));
        return res.toString();
    }

    private String getDefaultValue(Class<?> token) {
        if (boolean.class.equals(token)) {
            return DEFAULT_BOOLEAN;
        } else if (void.class.equals(token)) {
            return DEFAULT_VOID;
        } else if (token.isPrimitive()) {
            return DEFAULT_PRIMITIVE;
        }
        return DEFAULT_OBJECT;
    }

    private String getBody(Executable executable) {
        if (executable instanceof Method) {
            return "return" + getDefaultValue(((Method) executable).getReturnType()) + ';';
        } else {
            return "super" + getParams(executable, false) + ';';
        }
    }

    private String getExecutable(Class<?> token, Executable executable) {
        StringBuilder res = new StringBuilder(getTabs(1));
        final int mods = executable.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.NATIVE & ~Modifier.TRANSIENT;
        res.append(Modifier.toString(mods))
                .append(mods > 0 ? " " : "")
                .append(getReturnTypeAndName(token, executable))
                .append(getParams(executable, true))
                .append(getExceptions(executable))
                .append(" {")
                .append(END_OF_LINE)
                .append(getTabs(2))
                .append(getBody(executable))
                .append(END_OF_LINE)
                .append(getTabs(1))
                .append("}")
                .append(END_OF_LINE);
        return res.toString();
    }

    private void addMethodsToSet(Method[] methods, Set<MethodWrapper> storage) {
        Arrays.stream(methods)
                .map(MethodWrapper::new)
                .forEach(storage::add);
    }

    private Set<MethodWrapper> getOnlyAbstractMethods(Set<MethodWrapper> storage) {
        return storage.stream()
                .filter(methodWrapper -> Modifier.isAbstract(methodWrapper.getMethod().getModifiers()))
                .collect(Collectors.toSet());
    }

    private void implementAbstractMethods(Class<?> token, Writer writer) throws IOException {
        Set<MethodWrapper> methods = new HashSet<>();
        addMethodsToSet(token.getMethods(), methods);
        while (token != null) {
            addMethodsToSet(token.getDeclaredMethods(), methods);
            token = token.getSuperclass();
        }
        for (MethodWrapper methodWrapper : getOnlyAbstractMethods(methods)) {
            writer.write(toUnicode(getExecutable(null, methodWrapper.getMethod())));
        }
    }

    private void implementConstructors(Class<?> token, Writer writer) throws ImplerException, IOException {
        Constructor<?>[] constructors = Arrays.stream(token.getDeclaredConstructors())
                .filter(constructor -> !Modifier.isPrivate(constructor.getModifiers()))
                .toArray(Constructor[]::new);
        if (constructors.length == 0) {
            throw new ImplerException("There is no non-private constructors in class");
        }
        for (Constructor<?> constructor : constructors) {
            writer.write(toUnicode(getExecutable(token, constructor)));
        }
    }

    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        checkNotNull(token, root);
        if (token.isPrimitive() || token.isArray() || token == Enum.class || Modifier.isFinal(token.getModifiers()) || Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Incorrect class token");
        }
        root = getPath(token, root, ".java");
        createDirectories(root);

        try (BufferedWriter writer = Files.newBufferedWriter(root)) {
            try {
                writer.write(toUnicode(getClassHead(token)));
                if (!token.isInterface()) {
                    implementConstructors(token, writer);
                }
                implementAbstractMethods(token, writer);
                writer.write('}' + END_OF_LINE);
            } catch (IOException e) {
                throw new ImplerException("Error while writing to output file", e);
            }
        } catch (IOException e) {
            throw new ImplerException("Unable to write to output file", e);
        }
    }

    private void createDirectories(Path root) throws ImplerException {
        if (root.getParent() != null) {
            try {
                Files.createDirectories(root.getParent());
            } catch (IOException e) {
                throw new ImplerException("Error while creating directories for output file", e);
            }
        }
    }

    private Path getPath(Class<?> token, Path root, String end) {
        return root.resolve(token.getPackageName().replace('.', File.separatorChar)).resolve(getClassName(token) + end);
    }

    private String toUnicode(String str) {
        StringBuilder b = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (c >= 128)
                b.append("\\u").append(String.format("%04X", (int) c));
            else
                b.append(c);
        }

        return b.toString();
    }

    @Override
    public void implementJar(Class<?> token, Path root) throws ImplerException {
        checkNotNull(token, root);
        createDirectories(root);
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory(root.toAbsolutePath().getParent(), "temp");
        } catch (IOException e) {
            throw new ImplerException("Can not create temp directory", e);
        }

        try {
            implement(token, tempDir);
            final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
            if (javaCompiler == null || javaCompiler.run(null, null, null, "-cp",
                    tempDir.toString() + File.pathSeparator + System.getProperty("java.class.path"),
                    getPath(token, tempDir, ".java").toString()) != 0) {
                throw new ImplerException("Error while compiling generated files");
            }
            Manifest manifest = new Manifest();
            Attributes attributes = manifest.getMainAttributes();
            attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
            attributes.put(Attributes.Name.IMPLEMENTATION_VENDOR, "Nikita Maksimov");
            try (JarOutputStream jarOutputStream = new JarOutputStream(Files.newOutputStream(root), manifest)) {
                jarOutputStream.putNextEntry(new ZipEntry(token.getPackageName().replace('.', File.separatorChar) + File.separatorChar + getClassName(token) + ".class"));
                Files.copy(getPath(token, tempDir, ".class"), jarOutputStream);
            } catch (IOException e) {
                throw new ImplerException("Error while writing to JAR file", e);
            }
        } finally {
            try {
                cleanDir(tempDir);
            } catch (IOException e) {
                Helper.log("Can not delete temp directory", e);
            }
        }
    }

    private void checkNotNull(Class<?> token, Path root) throws ImplerException {
        if (token == null) {
            throw new ImplerException("Class token can not be null");
        }
        if (root == null) {
            throw new ImplerException("Path root can not be null");
        }
    }

    public static void main(String[] args) {
        if (args == null || (args.length != 2 && args.length != 3)) {
            Helper.log("Two or three arguments expected");
            return;
        }
        for (String arg : args) {
            if (arg == null) {
                Helper.log("Argument can not be null");
                return;
            }
        }
        JarImpler implementor = new Implementor();
        try {
            if (args.length == 2) {
                implementor.implement(Class.forName(args[0]), Paths.get(args[1]));
            } else {
                if ("-jar".equals(args[0])) {
                    implementor.implementJar(Class.forName(args[1]), Paths.get(args[2]));
                } else {
                    Helper.log("Invalid first argument for a program");
                }
            }
        } catch (ClassNotFoundException e) {
            Helper.log("Incorrect class name", e);
        } catch (ImplerException e) {
            Helper.log("An error occurred while implementing", e);
        } catch (InvalidPathException e) {
            Helper.log("Invalid output path", e);
        }
    }
}
