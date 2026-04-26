package io.github.neczpal.restdsl.generator.java;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

public class ModelGenerator {

    private final TypeMapper typeMapper;
    private final String packageName;
    private final List<Model> allModels;

    public ModelGenerator(String packageName, List<Model> allModels) {
        this.typeMapper = new TypeMapper(packageName);
        this.packageName = packageName;
        this.allModels = allModels;
    }

    public List<GeneratedFile> generate() {
        return allModels.stream()
                .map(this::generateModel)
                .collect(Collectors.toList());
    }

    private GeneratedFile generateModel(Model model) {
        String packagePath = this.packageName != null ? this.packageName.replace('.', '/') + "/" : "";
        String fileName = packagePath + model.name() + ".java";

        try {
            String content = this.generateModelClass(model);
            return new GeneratedFile(fileName, content);
        } catch (IOException e) {
            throw new RuntimeException("Error generating model: " + model.name(), e);
        }
    }

    private String generateModelClass(Model model) throws IOException {
        boolean isParent = allModels.stream().anyMatch(m -> model.equals(m.parent()));

        if (isParent) {
            return generateSealedInterface(model);
        } else if (model.parent() != null) {
            return generateImplementingRecord(model);
        } else {
            return generateSimpleRecord(model);
        }
    }

    private String generateSealedInterface(Model model) {
        TypeSpec.Builder interfaceBuilder = TypeSpec.interfaceBuilder(model.name())
                .addModifiers(Modifier.PUBLIC, Modifier.SEALED);

        if (model.parent() != null) {
            interfaceBuilder.addSuperinterface(ClassName.get(packageName, model.parent().name()));
        }

        allModels.stream()
                .filter(m -> model.equals(m.parent()))
                .forEach(child -> interfaceBuilder.addPermittedSubclass(ClassName.get(packageName, child.name())));

        for (Field field : model.fields()) {
            interfaceBuilder.addMethod(MethodSpec.methodBuilder(field.name())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(typeMapper.toJavaType(field.type()))
                    .build());
        }

        TypeSpec typeSpec = interfaceBuilder.build();
        return JavaFile.builder(this.packageName, typeSpec).build().toString();
    }

    private String generateImplementingRecord(Model model) {
        TypeSpec.Builder recordBuilder = TypeSpec.recordBuilder(model.name())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(packageName, model.parent().name()));

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
        List<Field> allFields = new ArrayList<>();
        Model current = model;
        while (current != null) {
            allFields.addAll(0, current.fields());
            current = current.parent();
        }

        for (Field field : allFields) {
            TypeName type = this.typeMapper.toJavaType(field.type());
            constructorBuilder.addParameter(ParameterSpec.builder(type, field.name()).build());
        }
        recordBuilder.recordConstructor(constructorBuilder.build());

        TypeSpec typeSpec = recordBuilder.build();
        return JavaFile.builder(this.packageName, typeSpec).build().toString();
    }

    private String generateSimpleRecord(Model model) {
        TypeSpec.Builder recordBuilder = TypeSpec.recordBuilder(model.name())
                .addModifiers(Modifier.PUBLIC);
        
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
        for (Field field : model.fields()) {
            TypeName type = this.typeMapper.toJavaType(field.type());
            constructorBuilder.addParameter(ParameterSpec.builder(type, field.name()).build());
        }
        recordBuilder.recordConstructor(constructorBuilder.build());

        TypeSpec typeSpec = recordBuilder.build();
        return JavaFile.builder(this.packageName, typeSpec).build().toString();
    }
}
