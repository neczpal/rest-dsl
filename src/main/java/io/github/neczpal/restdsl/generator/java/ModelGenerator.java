package io.github.neczpal.restdsl.generator.java;

import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

public class ModelGenerator {
    private final TypeMapper typeMapper;
    private final String packageName;

    public ModelGenerator(String packageName) {
        this.typeMapper = new TypeMapper();
        this.packageName = packageName;
    }

    public List<GeneratedFile> generate(List<Model> models) {
        return models.stream()
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
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(model.name())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        for (Field field : model.fields()) {
            TypeName type = this.typeMapper.toJavaType(field.type());
            String name = field.name();

            classBuilder.addField(FieldSpec.builder(type, name, Modifier.PRIVATE, Modifier.FINAL).build());
            
            constructorBuilder.addParameter(type, name);
            constructorBuilder.addStatement("this.$N = $N", name, name);

            // generate getter
            classBuilder.addMethod(MethodSpec.methodBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(type)
                    .addStatement("return this.$N", name)
                    .build());
        }

        classBuilder.addMethod(constructorBuilder.build());

        TypeSpec classSpec = classBuilder.build();

        JavaFile javaFile = JavaFile.builder(this.packageName, classSpec).build();
        StringBuilder sb = new StringBuilder();
        javaFile.writeTo(sb);
        return sb.toString();
    }
}
