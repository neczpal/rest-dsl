package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Type;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

public class TypeMapper {

    public static Schema generateSchemaType(Type type) {
        if (type.isArray()) {
            return new ArraySchema().items(generateSchemaType(type.elementType()));
        } else if (type.isPrimitive()) {
            return switch (type.name()) {
                case "String" -> new Schema<String>().type("string");
                case "Int" -> new Schema<Integer>().type("integer").format("int32");
                case "Float" -> new Schema<Number>().type("number").format("float");
                case "BigInt" -> new Schema<Long>().type("integer").format("int64");
                case "Boolean" -> new Schema<Boolean>().type("boolean");
                case "DateTime" -> new Schema<String>().type("string").format("date-time");
                case "Date" -> new Schema<String>().type("string").format("date");
                case "Time" -> new Schema<String>().type("string").format("time");
                case "Binary" -> new Schema().type("string").format("binary");
                default -> throw new IllegalArgumentException("Unsupported primitive type: " + type.name());
            };
        } else {
            return new Schema().$ref("#/components/schemas/" + type.name());
        }
    }
}
