package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;

import java.util.List;

import static io.github.neczpal.restdsl.generator.openapi.TypeMapper.generateSchemaType;

public class ModelGenerator {
    public void generate(OpenAPI openapi, List<Model> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        Components components = openapi.getComponents() == null ? new Components() : openapi.getComponents();
        for (Model model : models) {
            ObjectSchema schema = new ObjectSchema();
            for (Field field : model.fields()) {
                schema.addProperty(field.name(), generateSchemaType(field.type()));
            }
            components.addSchemas(model.name(), schema);
        }
        openapi.components(components);
    }
}
