package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;

import java.util.ArrayList;
import java.util.List;

public class ModelParser {
    public List<Model> parse(RestDSLParser.ModelsDefinitionContext ctx) {
        List<Model> models = new ArrayList<>();
        if (ctx == null) return models;
        for (RestDSLParser.ModelDefinitionContext modelCtx : ctx.modelDefinition()) {
            String name = modelCtx.CAPITAL_ID().getText();
            List<Field> fields = new ArrayList<>();
            if (modelCtx.modelBlock() != null) {
                for (RestDSLParser.FieldContext fieldCtx : modelCtx.modelBlock().field()) {
                    String fieldName = fieldCtx.anyId().getText();
                    String fieldType = fieldCtx.type().getText();
                    fields.add(Field.builder().name(fieldName).type(fieldType).build());
                }
            }
            models.add(Model.builder().name(name).fields(fields).build());
        }
        return models;
    }
}