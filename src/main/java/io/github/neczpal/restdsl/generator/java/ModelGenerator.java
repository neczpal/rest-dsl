package io.github.neczpal.restdsl.generator.java;

import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import com.palantir.javapoet.ParameterSpec;
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
        TypeSpec.Builder classBuilder = TypeSpec.recordBuilder(model.name())
                .addModifiers(Modifier.PUBLIC);

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

        for (Field field : model.fields()) {
            TypeName type = this.typeMapper.toJavaType(field.type());
            String name = field.name();

            constructorBuilder.addParameter(ParameterSpec.builder(type, name).build());
        }

        classBuilder.recordConstructor(constructorBuilder.build());

        TypeSpec classSpec = classBuilder.build();

        return JavaFileGenerator.generate(this.packageName, classSpec);
    }
}
