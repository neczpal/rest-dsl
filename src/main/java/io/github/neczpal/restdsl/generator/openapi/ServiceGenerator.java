package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Method;
import io.github.neczpal.restdsl.model.Service;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.github.neczpal.restdsl.generator.openapi.TypeMapper.generateSchemaType;

public class ServiceGenerator {

    public void generate(OpenAPI openapi, List<Service> services) {
        if (services == null || services.isEmpty()) {
            return;
        }

        Map<String, List<Method>> methodsByPath = groupMethodsByPath(services);

        Paths paths = openapi.getPaths() == null ? new Paths() : openapi.getPaths();

        for (Map.Entry<String, List<Method>> entry : methodsByPath.entrySet()) {
            String path = entry.getKey();
            List<Method> methods = entry.getValue();

            PathItem pathItem = new PathItem();
            for (Method method : methods) {
                Operation operation = generateMethod(method);
                switch (method.verb().toLowerCase()) {
                    case "get":
                        pathItem.get(operation);
                        break;
                    case "post":
                        pathItem.post(operation);
                        break;
                    case "put":
                        pathItem.put(operation);
                        break;
                    case "delete":
                        pathItem.delete(operation);
                        break;
                    case "patch":
                        pathItem.patch(operation);
                        break;
                    case "options":
                        pathItem.options(operation);
                        break;
                    case "head":
                        pathItem.head(operation);
                        break;
                    case "trace":
                        pathItem.trace(operation);
                        break;
                }
            }
            paths.addPathItem(path, pathItem);
        }
        openapi.paths(paths);
    }

    private Operation generateMethod(Method method) {
        Operation operation = new Operation()
                .operationId(method.name())
                .summary(method.summary())
                .description(method.description());

        List<Parameter> parameters = generateParameters(method);
        if (parameters != null && !parameters.isEmpty()) {
            operation.parameters(parameters);
        }

        RequestBody requestBody = generateRequestBody(method);
        if (requestBody != null) {
            operation.requestBody(requestBody);
        }

        operation.responses(generateResponses(method));
        return operation;
    }

    private Map<String, List<Method>> groupMethodsByPath(List<Service> services) {
        Map<String, List<Method>> methodsByPath = new LinkedHashMap<>();
        for (Service service : services) {
            String base = service.base() != null ? service.base() : "";
            for (Method method : service.methods()) {
                String methodPath = method.path() != null ? method.path() : "";
                String fullPath = base + methodPath;
                if (!fullPath.startsWith("/")) {
                    fullPath = "/" + fullPath;
                }
                methodsByPath.computeIfAbsent(fullPath, _ -> new ArrayList<>()).add(method);
            }
        }
        return methodsByPath;
    }

    private List<Parameter> generateParameters(Method method) {
        boolean hasPathParams = method.pathParams() != null && !method.pathParams().isEmpty();
        boolean hasQueryParams = method.queryParams() != null && !method.queryParams().isEmpty();

        if (!hasPathParams && !hasQueryParams) {
            return null;
        }

        List<Parameter> parameters = new ArrayList<>();
        if (hasPathParams) {
            for (Field param : method.pathParams()) {
                parameters.add(new PathParameter()
                        .name(param.name())
                        .required(true)
                        .schema(generateSchemaType(param.type()))
                );
            }
        }
        if (hasQueryParams) {
            for (Field param : method.queryParams()) {
                parameters.add(new QueryParameter()
                        .name(param.name())
                        .schema(generateSchemaType(param.type()))
                );
            }
        }
        return parameters;
    }

    private RequestBody generateRequestBody(Method method) {
        if (method.bodyType() == null) {
            return null;
        }
        Schema<?> schema = generateSchemaType(method.bodyType());
        if (schema == null) {
            return null;
        }
        return new RequestBody()
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(schema)));
    }

    private ApiResponses generateResponses(Method method) {
        ApiResponses responses = new ApiResponses();
        if (method.responses() == null || method.responses().isEmpty()) {
            responses.addApiResponse("200", new ApiResponse().description("Ok"));
            return responses;
        }

        for (Map.Entry<Integer, String> entry : method.responses().entrySet()) {
            String responseValue = entry.getValue();
            ApiResponse response = new ApiResponse();

            Schema<?> schema = generateSchemaType(responseValue);

            if (entry.getKey() < 400) {
                response.description("Ok");
            } else {
                response.description("Error");
            }

            if (schema != null) {
                response.content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(schema)));
            }

            responses.addApiResponse(entry.getKey().toString(), response);
        }
        return responses;
    }
}