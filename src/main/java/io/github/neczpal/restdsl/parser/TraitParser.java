package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Trait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraitParser {
    public List<Trait> parse(RestDSLParser.TraitsDefinitionContext ctx) {
        if (ctx == null) return new ArrayList<>();

        Map<String, RestDSLParser.TraitDefinitionContext> traitCtxMap = new HashMap<>();
        for (RestDSLParser.TraitDefinitionContext traitCtx : ctx.traitDefinition()) {
            traitCtxMap.put(traitCtx.CAPITAL_ID().getText(), traitCtx);
        }

        Map<String, Trait> parsedTraits = new HashMap<>();
        for (RestDSLParser.TraitDefinitionContext traitCtx : ctx.traitDefinition()) {
            parseTrait(traitCtx, traitCtxMap, parsedTraits);
        }

        return new ArrayList<>(parsedTraits.values());
    }

    private Trait parseTrait(RestDSLParser.TraitDefinitionContext traitCtx, Map<String, RestDSLParser.TraitDefinitionContext> allTraits, Map<String, Trait> parsedTraits) {
        String name = traitCtx.CAPITAL_ID().getText();
        if (parsedTraits.containsKey(name)) {
            return parsedTraits.get(name);
        }

        List<Trait> traits = new ArrayList<>();
        List<Field> fields = new ArrayList<>();
        if (traitCtx.traitBlock() != null) {
            for (RestDSLParser.TraitFieldContext traitFieldCtx : traitCtx.traitBlock().traitField()) {
                if (traitFieldCtx.field() != null) {
                    String fieldName = traitFieldCtx.field().anyId().getText();
                    String fieldType = traitFieldCtx.field().type().getText();
                    fields.add(Field.builder().name(fieldName).type(fieldType).build());
                } else {
                    String traitName = traitFieldCtx.CAPITAL_ID().getText();
                    RestDSLParser.TraitDefinitionContext includedTraitCtx = allTraits.get(traitName);
                    if (includedTraitCtx != null) {
                        traits.add(parseTrait(includedTraitCtx, allTraits, parsedTraits));
                    }
                }
            }
        }

        Trait trait = Trait.builder()
                .name(name)
                .traits(traits)
                .fields(fields)
                .build();
        parsedTraits.put(name, trait);
        return trait;
    }
}
