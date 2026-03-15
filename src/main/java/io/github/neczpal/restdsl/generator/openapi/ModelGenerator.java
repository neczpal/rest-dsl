package io.github.neczpal.restdsl.generator.openapi;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlNode;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;

import java.util.List;

public class ModelGenerator {
    public YamlMappingBuilder generate(YamlMappingBuilder openapi, List<Model> models) {
        if (models == null || models.isEmpty()) {
            return openapi;
        }
        YamlMappingBuilder schemas = Yaml.createYamlMappingBuilder();
        for (Model model : models) {
            YamlMappingBuilder properties = Yaml.createYamlMappingBuilder();
            for (Field field : model.fields()) {
                properties = properties.add(field.name(), generateSchemaType(field.type()));
            }
            schemas = schemas.add(
                    model.name(),
                    Yaml.createYamlMappingBuilder()
                            .add("type", "object")
                            .add("properties", properties.build())
                            .build()
            );
        }
        return openapi.add("components", Yaml.createYamlMappingBuilder()
                .add("schemas", schemas.build())
                .build());
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
