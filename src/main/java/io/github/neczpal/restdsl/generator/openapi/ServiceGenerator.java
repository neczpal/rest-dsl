package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Method;
import io.github.neczpal.restdsl.model.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServiceGenerator {

    public String generate(List<Service> services) {
        if (services == null || services.isEmpty()) {
            return "";
        }

        Map<String, List<Method>> methodsByPath = groupMethodsByPath(services);

        StringBuilder sb = new StringBuilder();
        sb.append("paths:\n");

        for (Map.Entry<String, List<Method>> entry : methodsByPath.entrySet()) {
            String path = entry.getKey();
            List<Method> methods = entry.getValue();

            sb.append("  ").append(path).append(":\n");

            for (Method method : methods) {
                sb.append("    ").append(method.verb().toLowerCase()).append(":\n");
                sb.append("      summary: ").append(method.name()).append("\n");

                generateParameters(sb, method);
                generateRequestBody(sb, method);
                generateResponses(sb, method);
            }
        }
        return sb.toString();
    }

    private Map<String, List<Method>> groupMethodsByPath(List<Service> services) {
        Map<String, List<Method>> methodsByPath = new LinkedHashMap<>();
        for (Service service : services) {
            String base = service.base() != null ? service.base() : "";
            for (Method method : service.methods()) {
                String methodPath = method.path() != null ? method.path() : "";
                String fullPath = base + methodPath;
                methodsByPath.computeIfAbsent(fullPath, _ -> new ArrayList<>()).add(method);
            }
        }
        return methodsByPath;
    }

    private void generateParameters(StringBuilder sb, Method method) {
        boolean hasPathParams = method.pathParams() != null && !method.pathParams().isEmpty();
        boolean hasQueryParams = method.queryParams() != null && !method.queryParams().isEmpty();

        if (!hasPathParams && !hasQueryParams) {
            return;
        }

        sb.append("      parameters:\n");
        if (hasPathParams) {
            for (Field param : method.pathParams()) {
                sb.append("        - name: ").append(param.name()).append("\n");
                sb.append("          in: path\n");
                sb.append("          required: true\n");
                sb.append("          schema:\n");
                sb.append("            type: ").append(mapType(param.type())).append("\n");
            }
        }
        if (hasQueryParams) {
            for (Field param : method.queryParams()) {
                sb.append("        - name: ").append(param.name()).append("\n");
                sb.append("          in: query\n");
                sb.append("          schema:\n");
                sb.append("            type: ").append(mapType(param.type())).append("\n");
            }
        }
    }

    private void generateRequestBody(StringBuilder sb, Method method) {
        if (method.bodyType() == null) {
            return;
        }
        sb.append("      requestBody:\n");
        sb.append("        content:\n");
        sb.append("          application/json:\n");
        sb.append("            schema:\n");
        generateSchemaType(sb, method.bodyType(), "              ");
    }

    private void generateResponses(StringBuilder sb, Method method) {
        sb.append("      responses:\n");
        if (method.responses() == null || method.responses().isEmpty()) {
            sb.append("        '200':\n");
            sb.append("          description: OK\n");
            return;
        }

        for (Map.Entry<Integer, String> entry : method.responses().entrySet()) {
            String responseValue = entry.getValue();
            sb.append("        '").append(entry.getKey()).append("':\n");

            boolean isDescription = responseValue.startsWith("\"");

            if (isDescription) {
                sb.append("          description: ").append(responseValue, 1, responseValue.length() - 1).append("\n");
            } else {
                sb.append("          description: OK\n"); // Default description
                sb.append("          content:\n");
                sb.append("            application/json:\n");
                sb.append("              schema:\n");
                generateSchemaType(sb, responseValue, "                ");
            }
        }
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
