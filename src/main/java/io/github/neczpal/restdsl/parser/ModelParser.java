package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;

import java.util.ArrayList;
import java.util.List;

public class ModelParser {
    public Model parse(RestDSLParser.ModelDefinitionContext ctx) {
        String name = ctx.ID().getText();
        List<Field> fields = new ArrayList<>();

        for (RestDSLParser.FieldContext fieldCtx : ctx.field()) {
            String fieldName = fieldCtx.ID().getText();
            String fieldType = fieldCtx.type().getText();
            fields.add(new Field(fieldName, fieldType));
        }
        return new Model(name, fields);
    }
}
