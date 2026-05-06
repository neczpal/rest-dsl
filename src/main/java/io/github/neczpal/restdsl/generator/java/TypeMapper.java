package io.github.neczpal.restdsl.generator.java;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import io.github.neczpal.restdsl.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TypeMapper {
    private final String packageName;

    public TypeMapper(String packageName) {
        this.packageName = packageName;
    }

    public TypeName toJavaType(Type dslType) {
        if (dslType == null || "Void".equals(dslType.name())) {
            return TypeName.VOID;
        }
        if (dslType.isArray()) {
            return ParameterizedTypeName.get(ClassName.get(List.class), toJavaType(dslType.elementType()));
        }
        if (dslType.isPrimitive()) {
            return switch (dslType.name()) {
                case "Int" -> ClassName.get(Integer.class);
                case "String" -> ClassName.get(String.class);
                case "Boolean" -> ClassName.get(Boolean.class);
                case "Float" -> ClassName.get(Float.class);
                case "BigInt" -> ClassName.get(BigDecimal.class);
                case "DateTime" -> ClassName.get(LocalDateTime.class);
                case "Date" -> ClassName.get(LocalDate.class);
                case "Time" -> ClassName.get(LocalTime.class);
                case "Binary" -> ClassName.get(byte[].class);
                default -> throw new IllegalArgumentException("Unsupported primitive type: " + dslType.name());
            };
        }
        return ClassName.get(packageName, dslType.name());
    }
}
