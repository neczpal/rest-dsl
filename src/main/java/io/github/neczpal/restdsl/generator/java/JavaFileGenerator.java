package io.github.neczpal.restdsl.generator.java;

import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.TypeSpec;

import java.io.IOException;

public class JavaFileGenerator {
    public static String generate(String packageName, TypeSpec typeSpec) throws IOException {
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .indent("    ")
                .build();
        StringBuilder sb = new StringBuilder();
        javaFile.writeTo(sb);
        return sb.toString();
    }
}
