package io.github.neczpal.restdsl.generator.java.server;

import com.palantir.javapoet.*;
import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.generator.java.JavaFileGenerator;
import io.github.neczpal.restdsl.generator.java.TypeMapper;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Method;
import io.github.neczpal.restdsl.model.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ApiGenerator {
    private final TypeMapper typeMapper;
    private final String packageName;

    public ApiGenerator(String packageName) {
        this.typeMapper = new TypeMapper();
        this.packageName = packageName;
    }

    public List<GeneratedFile> generate(List<Service> services) {
        if (services == null || services.isEmpty()) {
            return List.of();
        }

        List<GeneratedFile> files = new ArrayList<>();
        for (Service service : services) {
            String packagePath = packageName != null ? packageName.replace('.', '/') + "/" : "";
            String fileName = packagePath + service.name() + "Api.java";
            try {
                files.add(new GeneratedFile(fileName, generateController(service)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return files;
    }

    private String generateController(Service service) throws IOException {
        String controllerName = service.name() + "Api";

        AnnotationSpec restControllerAnnotation = AnnotationSpec.builder(RestController.class).build();
        String basePath = service.base() != null ? service.base() : "";
        AnnotationSpec requestMappingAnnotation = AnnotationSpec.builder(RequestMapping.class)
                .addMember("value", "$S", basePath)
                .build();

        TypeSpec.Builder controllerBuilder = TypeSpec.interfaceBuilder(controllerName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(restControllerAnnotation)
                .addAnnotation(requestMappingAnnotation);

        for (Method method : service.methods()) {
            controllerBuilder.addMethod(generateMethod(method));
        }

        TypeSpec controller = controllerBuilder.build();

        return JavaFileGenerator.generate(packageName, controller);
    }

    private MethodSpec generateMethod(Method method) {
        String methodName = method.name();
        TypeName returnType = determineReturnType(method);

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ParameterizedTypeName.get(ClassName.get(ResponseEntity.class), returnType.box()));

        // Add mapping annotation
        methodBuilder.addAnnotation(getMappingAnnotation(method));

        // Path Variables
        for (Field field : method.pathParams()) {
            methodBuilder.addParameter(createParameter(field, PathVariable.class));
        }

        // Query Parameters
        for (Field field : method.queryParams()) {
            methodBuilder.addParameter(createParameter(field, RequestParam.class));
        }

        // Request Body
        if (method.bodyType() != null) {
            TypeName bodyType = typeMapper.toJavaType(method.bodyType());
            methodBuilder.addParameter(ParameterSpec.builder(bodyType, "body")
                    .addAnnotation(RequestBody.class)
                    .build());
        }

        return methodBuilder.build();
    }

    private AnnotationSpec getMappingAnnotation(Method method) {
        String verb = method.verb().toUpperCase();
        String path = method.path() != null ? method.path() : "";

        return switch (verb) {
            case "GET" -> AnnotationSpec.builder(GetMapping.class).addMember("value", "$S", path).build();
            case "POST" -> AnnotationSpec.builder(PostMapping.class).addMember("value", "$S", path).build();
            case "PUT" -> AnnotationSpec.builder(PutMapping.class).addMember("value", "$S", path).build();
            case "DELETE" -> AnnotationSpec.builder(DeleteMapping.class).addMember("value", "$S", path).build();
            case "PATCH" -> AnnotationSpec.builder(PatchMapping.class).addMember("value", "$S", path).build();
            default -> AnnotationSpec.builder(RequestMapping.class)
                    .addMember("method", "RequestMethod.$L", verb)
                    .addMember("path", "$S", path)
                    .build();
        };
    }

    private ParameterSpec createParameter(Field field, Class<? extends Annotation> annotation) {
        TypeName type = typeMapper.toJavaType(field.type());
        return ParameterSpec.builder(type, field.name())
                .addAnnotation(AnnotationSpec.builder(annotation)
                        .addMember("value", "$S", field.name())
                        .build())
                .build();
    }

    private TypeName determineReturnType(Method method) {
        if (method.responses() == null || !method.responses().containsKey(200)) {
            return TypeName.VOID;
        }

        String responseType = method.responses().get(200);
        if (responseType == null || responseType.trim().isEmpty() || responseType.equals("Void")) {
            return TypeName.VOID;
        }

        return typeMapper.toJavaType(responseType);
    }
}
