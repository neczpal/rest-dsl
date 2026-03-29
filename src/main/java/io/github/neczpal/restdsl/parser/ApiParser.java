package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Api;

public class ApiParser {
    public Api parse(RestDSLParser.ApiDefinitionContext ctx) {
        String name = ctx.CAPITAL_ID().getText();
        String title = name;
        String version = null;
        String base = null;

        for (RestDSLParser.ApiElementContext element : ctx.apiElement()) {
            if (element.metaDefinition() != null) {
                for (RestDSLParser.MetaFieldContext field : element.metaDefinition().metaField()) {
                    String fieldName = field.anyId().getText();
                    String fieldValue = field.metaValue().getText().replace("\"", "");
                    if ("title".equals(fieldName)) {
                        title = fieldValue;
                    } else if ("version".equals(fieldName)) {
                        version = fieldValue;
                    } else if ("basePath".equals(fieldName) || "base".equals(fieldName)) {
                        base = fieldValue;
                    }
                }
            }
        }
        return Api.builder()
                .name(name)
                .title(title)
                .version(version)
                .base(base)
                .build();
    }
}