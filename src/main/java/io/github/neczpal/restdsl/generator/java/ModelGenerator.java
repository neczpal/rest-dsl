package io.github.neczpal.restdsl.generator.java;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;
import io.github.neczpal.restdsl.model.Trait;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;

public class ModelGenerator {

    private final TypeMapper typeMapper;
    private final String packageName;
    private final List<Trait> allTraits;
    private final List<Model> allModels;

    public ModelGenerator(String packageName, List<Trait> allTraits, List<Model> allModels) {
        this.typeMapper = new TypeMapper(packageName);
        this.packageName = packageName;
        this.allTraits = allTraits;
        this.allModels = allModels;
    }

    public List<GeneratedFile> generate() {
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        allTraits.stream()
                .map(this::generateTraitInterface)
                .forEach(generatedFiles::add);
        allModels.stream()
                .map(this::generateModel)
                .forEach(generatedFiles::add);
        return generatedFiles;
    }

    private GeneratedFile generateTraitInterface(Trait trait) {
        String packagePath = this.packageName != null ? this.packageName.replace('.', '/') + "/" : "";
        String fileName = packagePath + trait.name() + ".java";

        try {
            String content = this.generateTraitInterfaceFile(trait);
            return new GeneratedFile(fileName, content);
        } catch (IOException e) {
            throw new RuntimeException("Error generating trait: " + trait.name(), e);
        }
    }

    private String generateTraitInterfaceFile(Trait trait) throws IOException {
        TypeSpec.Builder interfaceBuilder = TypeSpec.interfaceBuilder(trait.name())
                .addModifiers(Modifier.PUBLIC);

        for (Trait includedTrait : trait.traits()) {
            interfaceBuilder.addSuperinterface(ClassName.get(packageName, includedTrait.name()));
        }

        for (Field field : trait.fields()) {
            interfaceBuilder.addMethod(MethodSpec.methodBuilder(field.name())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(typeMapper.toJavaType(field.type()))
                    .build());
        }

        TypeSpec typeSpec = interfaceBuilder.build();
        return JavaFile.builder(this.packageName, typeSpec).build().toString();
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

        // Add Jackson annotations for polymorphism
        interfaceBuilder.addAnnotation(AnnotationSpec.builder(JsonTypeInfo.class)
                .addMember("use", "JsonTypeInfo.Id.NAME")
                .addMember("include", "JsonTypeInfo.As.PROPERTY")
                .addMember("property", "\"_type\"")
                .build());

        List<Model> children = allModels.stream()
                .filter(m -> model.equals(m.parent()))
                .toList();

        CodeBlock.Builder subTypesBuilder = CodeBlock.builder().add("{");
        for (int i = 0; i < children.size(); i++) {
            subTypesBuilder.add(i > 0 ? ", " : "");
            subTypesBuilder.add("\n    @JsonSubTypes.Type(value = $T.class, name = \"$L\")",
                    ClassName.get(packageName, children.get(i).name()), children.get(i).name());
        }
        subTypesBuilder.add("\n}");

        interfaceBuilder.addAnnotation(AnnotationSpec.builder(JsonSubTypes.class)
                .addMember("value", subTypesBuilder.build())
                .build());

        if (model.parent() != null) {
            interfaceBuilder.addSuperinterface(ClassName.get(packageName, model.parent().name()));
        }

        for (Trait trait : model.traits()) {
            interfaceBuilder.addSuperinterface(ClassName.get(packageName, trait.name()));
        }

        children.forEach(child -> interfaceBuilder.addPermittedSubclass(ClassName.get(packageName, child.name())));

        for (Field field : getFields(model)) {
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

        return generateSuperTraits(model, recordBuilder);
    }

    @NonNull
    private String generateSuperTraits(Model model, TypeSpec.Builder recordBuilder) {
        for (Trait trait : model.traits()) {
            recordBuilder.addSuperinterface(ClassName.get(packageName, trait.name()));
        }

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
        for (Field field : getFields(model)) {
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

        return generateSuperTraits(model, recordBuilder);
    }

    private List<Field> getFields(Model model) {
        List<Field> fields = new ArrayList<>();
        Model current = model;
        while (current != null) {
            fields.addAll(0, current.fields());
            for (Trait trait : current.traits()) {
                fields.addAll(0, getFields(trait));
            }
            current = current.parent();
        }
        return fields;
    }

    private List<Field> getFields(Trait trait) {
        List<Field> fields = new ArrayList<>(trait.fields());
        for (Trait nestedTrait : trait.traits()) {
            fields.addAll(getFields(nestedTrait));
        }
        return fields;
    }
}
