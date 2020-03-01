package ru.itmo.rain.maksimov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Implementor implements Impler {
    private static final String DEFAULT_OBJECT = " null";
    private final static String DEFAULT_PRIMITIVE = " 0";
    private final static String DEFAULT_VOID = "";
    private final static String DEFAULT_BOOLEAN = " false";
    private final static String END_OF_LINE = System.lineSeparator();
    private final static String TAB = "    ";

    private static class MethodWrapper {
        private final Method method;
        private static final int BASE = 59;
        private static final int MOD = (int) (1e9 + 7);

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
                token.getSimpleName() + " {" + END_OF_LINE;
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

    private void getAbstractMethods(Method[] methods, Set<MethodWrapper> storage) {
        Arrays.stream(methods)
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .map(MethodWrapper::new)
                .forEach(storage::add);
    }

    private void implementAbstractMethods(Class<?> token, Writer writer) throws IOException {
        Set<MethodWrapper> methods = new HashSet<>();
        getAbstractMethods(token.getMethods(), methods);
        while (token != null) {
            getAbstractMethods(token.getDeclaredMethods(), methods);
            token = token.getSuperclass();
        }
        for (MethodWrapper methodWrapper : methods) {
            writer.write(getExecutable(null, methodWrapper.getMethod()));
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
            writer.write(getExecutable(token, constructor));
        }
    }

    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (token == null || root == null) {
            throw new ImplerException("Arguments can not be null");
        }
        if (token.isPrimitive() || token.isArray() || token == Enum.class || Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Incorrect class token");
        }
        root = root.resolve(token.getPackageName().replace('.', File.separatorChar)).resolve(getClassName(token) + ".java");
        if (root.getParent() != null) {
            try {
                Files.createDirectories(root.getParent());
            } catch (IOException e) {
                throw new ImplerException("Error while creating directories for output file", e);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(root)) {
            try {
                writer.write(getClassHead(token));
                if (!token.isInterface()) {
                    implementConstructors(token, writer);
                }
                implementAbstractMethods(token, writer);
                writer.write('}' + END_OF_LINE);
            } catch (IOException e) {
                throw new ImplerException("Error while writing to output file", e);
            }
        } catch (IOException e) {
            throw new ImplerException("Error while creating output file", e);
        }
    }
}
