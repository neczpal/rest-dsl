package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;
import io.github.neczpal.restdsl.model.Trait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelParser {
    public List<Model> parse(RestDSLParser.ModelsDefinitionContext ctx, List<Trait> allTraits) {
        if (ctx == null) return new ArrayList<>();

        Map<String, RestDSLParser.ModelDefinitionContext> modelCtxMap = new HashMap<>();
        for (RestDSLParser.ModelDefinitionContext modelCtx : ctx.modelDefinition()) {
            modelCtxMap.put(modelCtx.CAPITAL_ID(0).getText(), modelCtx);
        }

        Map<String, Trait> traitMap = new HashMap<>();
        for (Trait trait : allTraits) {
            traitMap.put(trait.name(), trait);
        }

        Map<String, Model> parsedModels = new HashMap<>();
        for (RestDSLParser.ModelDefinitionContext modelCtx : ctx.modelDefinition()) {
            parseModel(modelCtx, modelCtxMap, parsedModels, traitMap);
        }

        return new ArrayList<>(parsedModels.values());
    }

    private Model parseModel(RestDSLParser.ModelDefinitionContext modelCtx, Map<String, RestDSLParser.ModelDefinitionContext> allModels, Map<String, Model> parsedModels, Map<String, Trait> allTraits) {
        String name = modelCtx.CAPITAL_ID(0).getText();
        if (parsedModels.containsKey(name)) {
            return parsedModels.get(name);
        }

        Model parent = null;
        if (modelCtx.CAPITAL_ID().size() > 1) {
            String parentName = modelCtx.CAPITAL_ID(1).getText();
            RestDSLParser.ModelDefinitionContext parentCtx = allModels.get(parentName);
            if (parentCtx != null) {
                parent = parseModel(parentCtx, allModels, parsedModels, allTraits);
            }
        }

        List<Trait> traits = new ArrayList<>();
        List<Field> fields = new ArrayList<>();
        if (modelCtx.modelBlock() != null) {
            for (RestDSLParser.ModelFieldContext modelFieldCtx : modelCtx.modelBlock().modelField()) {
                if (modelFieldCtx.field() != null) {
                    String fieldName = modelFieldCtx.field().anyId().getText();
                    String fieldType = modelFieldCtx.field().type().getText();
                    fields.add(Field.builder().name(fieldName).type(fieldType).build());
                } else {
                    String traitName = modelFieldCtx.CAPITAL_ID().getText();
                    if (allTraits.containsKey(traitName)) {
                        traits.add(allTraits.get(traitName));
                    }
                }
            }
        }

        Model model = Model.builder()
                .name(name)
                .parent(parent)
                .traits(traits)
                .fields(fields)
                .build();
        parsedModels.put(name, model);
        return model;
    }
}
