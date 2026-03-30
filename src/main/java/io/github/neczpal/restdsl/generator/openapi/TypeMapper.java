package io.github.neczpal.restdsl.generator.openapi;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

public class TypeMapper {

    public static Schema<?> generateSchemaType(String type) {
        if (type == null || type.equals("Void")) {
            return null;
        }
        if (type.startsWith("[") && type.endsWith("]")) {
            String innerType = type.substring(1, type.length() - 1);
            return new ArraySchema().items(generateSchemaType(innerType));
        } else {
            return switch (type) {
                case "Int", "String", "Boolean", "Double" ->
                        new Schema<>().type(mapType(type));
                default -> // Custom type
                        new Schema<>().$ref("#/components/schemas/" + type);
            };
        }
    }

    public static String mapType(String type) {
        return switch (type) {
            case "Int" -> "integer";
            case "String" -> "string";
            case "Boolean" -> "boolean";
            case "Double" -> "number";
            default -> type;
        };
    }
}
