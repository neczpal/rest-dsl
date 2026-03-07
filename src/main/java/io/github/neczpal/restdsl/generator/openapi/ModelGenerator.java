package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;

import java.util.List;

public class ModelGenerator {
    public String generate(List<Model> models) {
        if (models == null || models.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("components:\n");
        sb.append("  schemas:\n");
        for (Model model : models) {
            sb.append("    ").append(model.name()).append(":\n");
            sb.append("      type: object\n");
            sb.append("      properties:\n");
            for (Field field : model.fields()) {
                sb.append("        ").append(field.name()).append(":\n");
                generateSchemaType(sb, field.type(), "          ");
            }
        }
        return sb.toString();
    }

    private void generateSchemaType(StringBuilder sb, String type, String indentation) {
        if (type.startsWith("[") && type.endsWith("]")) {
            sb.append(indentation).append("type: array\n");
            sb.append(indentation).append("items:\n");
            String innerType = type.substring(1, type.length() - 1);
            generateSchemaType(sb, innerType, indentation + "  ");
        } else {
            switch (type) {
                case "Int":
                case "String":
                case "Boolean":
                case "Double":
                    sb.append(indentation).append("type: ").append(mapType(type)).append("\n");
                    break;
                default: // Custom type
                    sb.append(indentation).append("$ref: '#/components/schemas/").append(type).append("'\n");
                    break;
            }
        }
    }

    private String mapType(String type) {
        return switch (type) {
            case "Int" -> "integer";
            case "String" -> "string";
            case "Boolean" -> "boolean";
            case "Double" -> "number";
            default -> type;
        };
    }
}
