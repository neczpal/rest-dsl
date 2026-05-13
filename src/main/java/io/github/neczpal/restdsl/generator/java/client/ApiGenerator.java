package io.github.neczpal.restdsl.generator.java.client;

import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.generator.java.JavaFileGenerator;
import io.github.neczpal.restdsl.generator.java.TypeMapper;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Method;
import io.github.neczpal.restdsl.model.Service;
import io.github.neczpal.restdsl.model.Type;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiGenerator {
    private final TypeMapper typeMapper;
    private final String packageName;

    public ApiGenerator(String packageName) {
        this.typeMapper = new TypeMapper(packageName);
        this.packageName = packageName;
    }

    public List<GeneratedFile> generate(List<Service> services) {
        return services.stream()
                .map(this::generateService)
                .collect(Collectors.toList());
    }

    private GeneratedFile generateService(Service service) {
        String packagePath = packageName != null ? packageName.replace('.', '/') + "/" : "";
        String fileName = packagePath + service.name() + "Client.java";
        try {
            return new GeneratedFile(fileName, generateClient(service));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateClient(Service service) throws IOException {
        String clientName = service.name() + "Client";

        FieldSpec restClientField = FieldSpec.builder(RestClient.class, "restClient", Modifier.PRIVATE, Modifier.FINAL).build();

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "serverUrl");

        if (service.base() != null && !service.base().isEmpty()) {
            constructorBuilder.addStatement("this.restClient = RestClient.builder().baseUrl(serverUrl + $S).build()", service.base());
        } else {
            constructorBuilder.addStatement("this.restClient = RestClient.builder().baseUrl(serverUrl).build()");
        }
        MethodSpec constructor = constructorBuilder.build();

        TypeSpec.Builder clientBuilder = TypeSpec.classBuilder(clientName)
                .addModifiers(Modifier.PUBLIC)
                .addField(restClientField)
                .addMethod(constructor);

        for (Method method : service.methods()) {
            clientBuilder.addMethod(generateEndpoint(method));
        }

        TypeSpec client = clientBuilder.build();
        return JavaFileGenerator.generate(packageName, client);
    }

    private MethodSpec generateEndpoint(Method endpoint) {
        String methodName = endpoint.name();
        String verb = endpoint.verb().toUpperCase();
        Type dslReturnType = endpoint.responses().get(200);
        TypeName returnType = typeMapper.toJavaType(dslReturnType);

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(returnType);

        // Javadoc
        if (endpoint.summary() != null) {
            methodBuilder.addJavadoc("$L\n", endpoint.summary());
        }
        if (endpoint.description() != null) {
            methodBuilder.addJavadoc("\n$L\n", endpoint.description());
        }

        // Javadoc for possible errors
        if (!endpoint.responses().isEmpty()) {
            boolean hasErrors = endpoint.responses().entrySet().stream().anyMatch(e -> e.getKey() >= 400);
            if (hasErrors) {
                methodBuilder.addJavadoc("\nPossible error codes:\n");
                for (Map.Entry<Integer, Type> response : endpoint.responses().entrySet()) {
                    if (response.getKey() >= 400) {
                        Type type = response.getValue();
                        if (type != null && !"Void".equals(type.name())) {
                            methodBuilder.addJavadoc("- $L - $L\n", response.getKey(), type.name());
                        } else {
                            methodBuilder.addJavadoc("- $L\n", response.getKey());
                        }
                    }
                }
            }
        }

        // Parameters
        endpoint.pathParams().forEach(p -> methodBuilder.addParameter(typeMapper.toJavaType(p.type()), p.name()));
        endpoint.queryParams().forEach(p -> methodBuilder.addParameter(typeMapper.toJavaType(p.type()), p.name()));
        if (endpoint.bodyType() != null) {
            methodBuilder.addParameter(typeMapper.toJavaType(endpoint.bodyType()), "body");
        }

        // Method invocation
        String methodInvocation = switch (verb) {
            case "GET" -> "get()";
            case "POST" -> "post()";
            case "PUT" -> "put()";
            case "DELETE" -> "delete()";
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + verb);
        };

        CodeBlock.Builder statement = CodeBlock.builder();
        if (returnType.equals(TypeName.VOID)) {
            statement.add("");
        } else {
            statement.add("return ");
        }
        statement.add("restClient.$L\n", methodInvocation);

        // URI
        String uri = endpoint.path();
        if (uri != null) {
            for (Field pathParam : endpoint.pathParams()) {
                uri = uri.replace(":" + pathParam.name(), "{" + pathParam.name() + "}");
            }
            String query = endpoint.queryParams().stream()
                    .map(Field::name)
                    .map(name -> name + "={" + name + "}")
                    .collect(Collectors.joining("&"));
            if (!query.isEmpty()) {
                uri += "?" + query;
            }

            statement.add("                .uri($S", uri);
            endpoint.pathParams().forEach(p -> statement.add(", $L", p.name()));
            endpoint.queryParams().forEach(p -> statement.add(", $L", p.name()));
            statement.add(")\n");
        }

        if (endpoint.bodyType() != null) {
            statement.add("                .body(body)\n");
        }

        statement.add("                .retrieve()\n");

        if (returnType.equals(TypeName.VOID)) {
            statement.add("                .toBodilessEntity()");
        } else {
            if (dslReturnType != null && dslReturnType.isArray()) {
                statement.add("                .body(new $T<$T>() {})", ParameterizedTypeReference.class, returnType);
            } else {
                statement.add("                .body($T.class)", returnType);
            }
        }

        methodBuilder.addStatement(statement.build());

        return methodBuilder.build();
    }
}
