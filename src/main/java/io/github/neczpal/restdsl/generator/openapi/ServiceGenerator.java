package io.github.neczpal.restdsl.generator.openapi;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequenceBuilder;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Method;
import io.github.neczpal.restdsl.model.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServiceGenerator {

    public YamlMappingBuilder generate(YamlMappingBuilder openapi, List<Service> services) {
        if (services == null || services.isEmpty()) {
            return openapi;
        }

        Map<String, List<Method>> methodsByPath = groupMethodsByPath(services);

        YamlMappingBuilder paths = Yaml.createYamlMappingBuilder();

        for (Map.Entry<String, List<Method>> entry : methodsByPath.entrySet()) {
            String path = entry.getKey();
            List<Method> methods = entry.getValue();

            YamlMappingBuilder pathMethods = Yaml.createYamlMappingBuilder();
            for (Method method : methods) {
                pathMethods = pathMethods.add(
                        method.verb().toLowerCase(),
                        generateMethod(method)
                );
            }
            paths = paths.add(path, pathMethods.build());
        }
        return openapi.add("paths", paths.build());
    }

    private YamlMapping generateMethod(Method method) {
        YamlMappingBuilder methodBuilder = Yaml.createYamlMappingBuilder()
                .add("summary", method.name());

        YamlNode parameters = generateParameters(method);
        if (parameters != null) {
            methodBuilder = methodBuilder.add("parameters", parameters);
        }

        YamlNode requestBody = generateRequestBody(method);
        if (requestBody != null) {
            methodBuilder = methodBuilder.add("requestBody", requestBody);
        }

        methodBuilder = methodBuilder.add("responses", generateResponses(method));
        return methodBuilder.build();
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

    private YamlNode generateParameters(Method method) {
        boolean hasPathParams = method.pathParams() != null && !method.pathParams().isEmpty();
        boolean hasQueryParams = method.queryParams() != null && !method.queryParams().isEmpty();

        if (!hasPathParams && !hasQueryParams) {
            return null;
        }

        YamlSequenceBuilder parameters = Yaml.createYamlSequenceBuilder();
        if (hasPathParams) {
            for (Field param : method.pathParams()) {
                parameters = parameters.add(
                        Yaml.createYamlMappingBuilder()
                                .add("name", param.name())
                                .add("in", "path")
                                .add("required", "true")
                                .add("schema", Yaml.createYamlMappingBuilder().add("type", mapType(param.type())).build())
                                .build()
                );
            }
        }
        if (hasQueryParams) {
            for (Field param : method.queryParams()) {
                parameters = parameters.add(
                        Yaml.createYamlMappingBuilder()
                                .add("name", param.name())
                                .add("in", "query")
                                .add("schema", Yaml.createYamlMappingBuilder().add("type", mapType(param.type())).build())
                                .build()
                );
            }
        }
        return parameters.build();
    }

    private YamlNode generateRequestBody(Method method) {
        if (method.bodyType() == null) {
            return null;
        }
        return Yaml.createYamlMappingBuilder()
                .add("content", Yaml.createYamlMappingBuilder()
                        .add("application/json", Yaml.createYamlMappingBuilder()
                                .add("schema", generateSchemaType(method.bodyType()))
                                .build())
                        .build())
                .build();
    }

    private YamlNode generateResponses(Method method) {
        YamlMappingBuilder responses = Yaml.createYamlMappingBuilder();
        if (method.responses() == null || method.responses().isEmpty()) {
            responses = responses.add(
                    "200",
                    Yaml.createYamlMappingBuilder().add("description", "OK").build()
            );
            return responses.build();
        }

        for (Map.Entry<Integer, String> entry : method.responses().entrySet()) {
            String responseValue = entry.getValue();
            YamlMappingBuilder response = Yaml.createYamlMappingBuilder();

            boolean isDescription = responseValue.startsWith("\"");

            if (isDescription) {
                response = response.add("description", responseValue.substring(1, responseValue.length() - 1));
            } else {
                response = response.add("description", "OK")
                        .add("content", Yaml.createYamlMappingBuilder()
                                .add("application/json", Yaml.createYamlMappingBuilder()
                                        .add("schema", generateSchemaType(responseValue))
                                        .build())
                                .build());
            }
            responses = responses.add(entry.getKey().toString(), response.build());
        }
        return responses.build();
    }

    private YamlNode generateSchemaType(String type) {
        if (type.startsWith("[") && type.endsWith("]")) {
            String innerType = type.substring(1, type.length() - 1);
            return Yaml.createYamlMappingBuilder()
                    .add("type", "array")
                    .add("items", generateSchemaType(innerType))
                    .build();
        } else {
            return switch (type) {
                case "Int", "String", "Boolean", "Double" ->
                        Yaml.createYamlMappingBuilder().add("type", mapType(type)).build();
                default -> // Custom type
                        Yaml.createYamlMappingBuilder().add("$ref", "#/components/schemas/" + type).build();
            };
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
