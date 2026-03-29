package io.github.neczpal.restdsl.generator.java;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;

import java.util.List;

public class TypeMapper {
    public TypeName toJavaType(String dslType) {
        if (dslType == null || dslType.trim().isEmpty()) {
            return TypeName.VOID;
        }
        if (dslType.startsWith("[") && dslType.endsWith("]")) {
            String innerType = dslType.substring(1, dslType.length() - 1);
            return ParameterizedTypeName.get(ClassName.get(List.class), toJavaType(innerType));
        }
        return switch (dslType) {
            case "Int" -> ClassName.get(Integer.class);
            case "String" -> ClassName.get(String.class);
            case "Boolean" -> ClassName.get(Boolean.class);
            case "Double" -> ClassName.get(Double.class);
            case "Void" -> TypeName.VOID;
            default -> ClassName.bestGuess(dslType);
        };
    }
}
