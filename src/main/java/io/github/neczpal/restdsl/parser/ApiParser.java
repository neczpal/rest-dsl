package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Api;

public class ApiParser {
    public Api parse(RestDSLParser.ApiDefinitionContext ctx) {
        String name = ctx.ID().getText();
        String title = null;
        String version = null;
        String base = null;

        for (RestDSLParser.ApiPropertiesContext prop : ctx.apiProperties()) {
            if (prop instanceof RestDSLParser.TitlePropContext) {
                title = ((RestDSLParser.TitlePropContext) prop).STRING().getText().replace("\"", "");
            } else if (prop instanceof RestDSLParser.VersionPropContext) {
                version = ((RestDSLParser.VersionPropContext) prop).STRING().getText().replace("\"", "");
            } else if (prop instanceof RestDSLParser.BasePropContext) {
                base = ((RestDSLParser.BasePropContext) prop).STRING().getText().replace("\"", "");
            }
        }
        return new Api(name, title, version, base);
    }
}
