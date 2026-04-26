package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelParser {
    public List<Model> parse(RestDSLParser.ModelsDefinitionContext ctx) {
        if (ctx == null) return new ArrayList<>();

        Map<String, RestDSLParser.ModelDefinitionContext> modelCtxMap = new HashMap<>();
        for (RestDSLParser.ModelDefinitionContext modelCtx : ctx.modelDefinition()) {
            modelCtxMap.put(modelCtx.CAPITAL_ID(0).getText(), modelCtx);
        }

        Map<String, Model> parsedModels = new HashMap<>();
        for (RestDSLParser.ModelDefinitionContext modelCtx : ctx.modelDefinition()) {
            parseModel(modelCtx, modelCtxMap, parsedModels);
        }

        return new ArrayList<>(parsedModels.values());
    }

    private Model parseModel(RestDSLParser.ModelDefinitionContext modelCtx, Map<String, RestDSLParser.ModelDefinitionContext> allModels, Map<String, Model> parsedModels) {
        String name = modelCtx.CAPITAL_ID(0).getText();
        if (parsedModels.containsKey(name)) {
            return parsedModels.get(name);
        }

        Model parent = null;
        if (modelCtx.CAPITAL_ID().size() > 1) {
            String parentName = modelCtx.CAPITAL_ID(1).getText();
            RestDSLParser.ModelDefinitionContext parentCtx = allModels.get(parentName);
            if (parentCtx != null) {
                parent = parseModel(parentCtx, allModels, parsedModels);
            }
        }

        List<Field> fields = new ArrayList<>();
        if (modelCtx.modelBlock() != null) {
            for (RestDSLParser.FieldContext fieldCtx : modelCtx.modelBlock().field()) {
                String fieldName = fieldCtx.anyId().getText();
                String fieldType = fieldCtx.type().getText();
                fields.add(Field.builder().name(fieldName).type(fieldType).build());
            }
        }

        Model model = Model.builder()
                .name(name)
                .parent(parent)
                .fields(fields)
                .build();
        parsedModels.put(name, model);
        return model;
    }
}
