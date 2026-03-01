package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Method;
import io.github.neczpal.restdsl.model.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceParser {
    public Service parse(RestDSLParser.ServiceDefinitionContext ctx) {
        String name = ctx.ID().getText();
        String base = null;
        List<Method> methods = new ArrayList<>();

        for (RestDSLParser.ServiceElementContext elem : ctx.serviceElement()) {
            if (elem instanceof RestDSLParser.ServiceBasePropContext) {
                base = ((RestDSLParser.ServiceBasePropContext) elem).STRING().getText().replace("\"", "");
            } else if (elem instanceof RestDSLParser.ServiceMethodPropContext) {
                methods.add(parseMethod(((RestDSLParser.ServiceMethodPropContext) elem).methodDefinition()));
            }
        }
        return new Service(name, base, methods);
    }

    private Method parseMethod(RestDSLParser.MethodDefinitionContext ctx) {
        String verb = ctx.verb().getText();
        String name = ctx.ID().getText();
        String path = null;
        String bodyType = null;
        List<Field> pathParams = new ArrayList<>();
        List<Field> queryParams = new ArrayList<>();
        Map<Integer, String> responses = new HashMap<>();

        for (RestDSLParser.MethodElementContext elem : ctx.methodElement()) {
            if (elem instanceof RestDSLParser.MethodPathPropContext) {
                path = ((RestDSLParser.MethodPathPropContext) elem).STRING().getText().replace("\"", "");
            } else if (elem instanceof RestDSLParser.MethodBodyPropContext) {
                bodyType = ((RestDSLParser.MethodBodyPropContext) elem).type().getText();
            } else if (elem instanceof RestDSLParser.MethodPathParamsPropContext) {
                for (RestDSLParser.ParamFieldContext param : ((RestDSLParser.MethodPathParamsPropContext) elem).paramField()) {
                    pathParams.add(new Field(param.ID().getText(), param.type().getText()));
                }
            } else if (elem instanceof RestDSLParser.MethodQueryParamsPropContext) {
                for (RestDSLParser.ParamFieldContext param : ((RestDSLParser.MethodQueryParamsPropContext) elem).paramField()) {
                    queryParams.add(new Field(param.ID().getText(), param.type().getText()));
                }
            } else if (elem instanceof RestDSLParser.MethodResponsesPropContext) {
                for (RestDSLParser.ResponseFieldContext response : ((RestDSLParser.MethodResponsesPropContext) elem).responseField()) {
                    int code = Integer.parseInt(response.INT().getText());
                    String value;
                    if (response.type() != null) {
                        value = response.type().getText();
                    } else { // It must be a STRING
                        value = response.STRING().getText(); // This includes the quotes
                    }
                    responses.put(code, value);
                }
            }
        }
        return new Method(verb, name, path, bodyType, pathParams, queryParams, responses);
    }
}
