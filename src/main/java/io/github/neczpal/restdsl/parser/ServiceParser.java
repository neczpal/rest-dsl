package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Method;
import io.github.neczpal.restdsl.model.Service;
import io.github.neczpal.restdsl.model.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.neczpal.restdsl.parser.ModelParser.parseType;

public class ServiceParser {
    public List<Service> parse(RestDSLParser.PathsDefinitionContext ctx) {
        List<Service> services = new ArrayList<>();
        if (ctx == null) return services;
        for (RestDSLParser.PathElementContext pathElem : ctx.pathElement()) {
            if (pathElem.pathDefinition() != null) {
                services.add(parseService(pathElem.pathDefinition()));
            }
        }
        return services;
    }

    private Service parseService(RestDSLParser.PathDefinitionContext ctx) {
        String base = ctx.PATH_ID().getText();
        String name = generateServiceName(base);
        List<Method> methods = new ArrayList<>();
        if (ctx.pathBlock() != null) {
            parsePathBlock(ctx.pathBlock(), "", methods);
        }
        
        return Service.builder()
                .name(name)
                .base(base)
                .methods(methods)
                .build();
    }

    private String generateServiceName(String path) {
        String[] parts = path.split("/");
        for (String part : parts) {
            if (!part.isEmpty() && !part.startsWith("{") && !part.startsWith(":")) {
                return part.substring(0, 1).toUpperCase() + part.substring(1);
            }
        }
        return "Default";
    }

    private void parsePathBlock(RestDSLParser.PathBlockContext ctx, String currentPath, List<Method> methods) {
        for (RestDSLParser.PathElementContext elem : ctx.pathElement()) {
            if (elem.pathDefinition() != null) {
                String subPath = elem.pathDefinition().PATH_ID().getText();
                subPath = subPath.replaceAll("/:([a-zA-Z0-9_]+)", "/{$1}");
                parsePathBlock(elem.pathDefinition().pathBlock(), currentPath + subPath, methods);
            } else if (elem.endpointDefinition() != null) {
                methods.add(parseMethod(elem.endpointDefinition(), currentPath));
            }
        }
    }

    private Method parseMethod(RestDSLParser.EndpointDefinitionContext ctx, String currentPath) {
        String summary = null;
        String description = null;
        if (ctx.endpointAnnotations() != null) {
            for (RestDSLParser.EndpointAnnotationContext annotation : ctx.endpointAnnotations().endpointAnnotation()) {
                if (annotation.SUMMARY() != null) {
                    summary = unquote(annotation.STRING().getText());
                } else if (annotation.DESCRIPTION() != null) {
                    description = unquote(annotation.STRING().getText());
                }
            }
        }

        String verb = ctx.httpMethod().getText();
        String name = ctx.anyId() != null ? ctx.anyId().getText() : generateMethodName(verb, currentPath);
        
        Type bodyType = null;
        List<Field> queryParams = new ArrayList<>();
        List<Field> pathParams = new ArrayList<>();
        
        if (ctx.endpointParams() != null) {
            for (RestDSLParser.ParamContext param : ctx.endpointParams().param()) {
                String pName = param.anyId().getText();
                Type pType = parseType(param.type());
                if (pName.equals("body")) {
                    bodyType = pType;
                } else if (currentPath.contains("{" + pName + "}")) {
                    pathParams.add(Field.builder().name(pName).type(pType).build());
                } else {
                    queryParams.add(Field.builder().name(pName).type(pType).build());
                }
            }
        }

        Matcher matcher = Pattern.compile("\\{([^}]+)}").matcher(currentPath);
        while (matcher.find()) {
            String pName = matcher.group(1);
            boolean exists = pathParams.stream().anyMatch(p -> p.name().equals(pName));
            if (!exists) {
                pathParams.add(Field.builder().name(pName).type(Type.builder().name("Int").isPrimitive(true).build()).build()); // default type if missing
            }
        }

        Map<Integer, Type> responses = new HashMap<>();
        if (ctx.endpointSignature() != null) {
            if (ctx.endpointSignature().inlineSignature() != null) {
                RestDSLParser.InlineSignatureContext sig = ctx.endpointSignature().inlineSignature();
                if (sig.responseType() != null) {
                    if (sig.responseType().INT() != null) {
                        responses.put(Integer.parseInt(sig.responseType().getText()), Type.builder().name("Void").build());
                    } else {
                        responses.put(200, parseType(sig.responseType().type()));
                    }
                }
                if (sig.inlineErrorDef() != null) {
                    if (sig.inlineErrorDef().INT() != null) {
                        responses.put(Integer.parseInt(sig.inlineErrorDef().INT().getText()), Type.builder().name("Void").build());
                    } else {
                        for (RestDSLParser.ErrorMappingContext map : sig.inlineErrorDef().errorMapping()) {
                            int code = Integer.parseInt(map.INT().getText());
                            Type t = map.type() != null ? parseType(map.type()) : Type.builder().name("Void").build();
                            responses.put(code, t);
                        }
                    }
                }
            } else if (ctx.endpointSignature().blockSignature() != null) {
                for (RestDSLParser.SignatureBlockElementContext blockElem : ctx.endpointSignature().blockSignature().signatureBlockElement()) {
                    if (blockElem.responseBlock() != null) {
                        for (RestDSLParser.ErrorMappingContext map : blockElem.responseBlock().errorMapping()) {
                            int code = Integer.parseInt(map.INT().getText());
                            Type t = map.type() != null ? parseType(map.type()) : Type.builder().name("Void").build();
                            responses.put(code, t);
                        }
                    } else if (blockElem.errorsBlock() != null) {
                        for (RestDSLParser.ErrorDetailContext detail : blockElem.errorsBlock().errorDetail()) {
                            int code = Integer.parseInt(detail.INT().getText());
                            Type t = Type.builder().name("Void").build();
                            if (detail.type() != null) {
                                t = parseType(detail.type());
                            } else if (detail.errorDetailBlock() != null) {
                                for (RestDSLParser.FieldContext field : detail.errorDetailBlock().field()) {
                                    if (field.anyId().getText().equals("body")) {
                                        t = parseType(field.type());
                                    }
                                }
                            }
                            responses.put(code, t);
                        }
                    }
                }
            }
        }

        return Method.builder()
                .verb(verb)
                .name(name)
                .path(currentPath.isEmpty() ? null : currentPath)
                .summary(summary)
                .description(description)
                .bodyType(bodyType)
                .pathParams(pathParams)
                .queryParams(queryParams)
                .responses(responses)
                .build();
    }

    private String generateMethodName(String verb, String path) {
        if (path == null || path.isEmpty() || path.equals("/")) return verb;
        StringBuilder name = new StringBuilder(verb);
        String[] parts = path.split("/");
        for (String part : parts) {
            if (!part.isEmpty()) {
                if (part.startsWith("{") && part.endsWith("}")) {
                    String paramName = part.substring(1, part.length() - 1);
                    name.append("By").append(paramName.substring(0, 1).toUpperCase()).append(paramName.substring(1));
                } else {
                    name.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
                }
            }
        }
        return name.toString();
    }

    private String unquote(String text) {
        if (text != null && text.startsWith("\"") && text.endsWith("\"")) {
            return text.substring(1, text.length() - 1);
        }
        return text;
    }
}
