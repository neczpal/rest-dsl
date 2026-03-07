package io.github.neczpal.restdsl.generator.java.server;

import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;

import java.util.ArrayList;
import java.util.List;

public class ModelGenerator {

    public List<GeneratedFile> generate(List<Model> models) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }

        List<GeneratedFile> files = new ArrayList<>();
        for (Model model : models) {
            files.add(new GeneratedFile(model.name() + ".java", generateModel(model)));
        }
        return files;
    }

    private String generateModel(Model model) {
        StringBuilder sb = new StringBuilder();
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Objects;\n\n");

        sb.append("public class ").append(model.name()).append(" {\n");

        // Fields
        for (Field field : model.fields()) {
            sb.append("    private ").append(mapType(field.type())).append(" ").append(field.name()).append(";\n");
        }
        sb.append("\n");

        // No-args Constructor
        sb.append("    public ").append(model.name()).append("() {\n");
        sb.append("    }\n\n");

        // All-args Constructor
        sb.append("    public ").append(model.name()).append("(");
        for (int i = 0; i < model.fields().size(); i++) {
            Field field = model.fields().get(i);
            sb.append(mapType(field.type())).append(" ").append(field.name());
            if (i < model.fields().size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") {\n");
        for (Field field : model.fields()) {
            sb.append("        this.").append(field.name()).append(" = ").append(field.name()).append(";\n");
        }
        sb.append("    }\n\n");

        // Getters and Setters
        for (Field field : model.fields()) {
            String type = mapType(field.type());
            String name = field.name();
            String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);

            // Getter
            sb.append("    public ").append(type).append(" get").append(capitalizedName).append("() {\n");
            sb.append("        return ").append(name).append(";\n");
            sb.append("    }\n\n");

            // Setter
            sb.append("    public void set").append(capitalizedName).append("(").append(type).append(" ").append(name).append(") {\n");
            sb.append("        this.").append(name).append(" = ").append(name).append(";\n");
            sb.append("    }\n\n");
        }

        // equals()
        sb.append("    @Override\n");
        sb.append("    public boolean equals(Object o) {\n");
        sb.append("        if (this == o) return true;\n");
        sb.append("        if (o == null || getClass() != o.getClass()) return false;\n");
        sb.append("        ").append(model.name()).append(" that = (").append(model.name()).append(") o;\n");
        sb.append("        return ");
        if (model.fields().isEmpty()) {
            sb.append("true");
        } else {
            for (int i = 0; i < model.fields().size(); i++) {
                Field field = model.fields().get(i);
                sb.append("Objects.equals(").append(field.name()).append(", that.").append(field.name()).append(")");
                if (i < model.fields().size() - 1) {
                    sb.append(" && ");
                }
            }
        }
        sb.append(";\n");
        sb.append("    }\n\n");

        // hashCode()
        sb.append("    @Override\n");
        sb.append("    public int hashCode() {\n");
        sb.append("        return Objects.hash(");
        for (int i = 0; i < model.fields().size(); i++) {
            Field field = model.fields().get(i);
            sb.append(field.name());
            if (i < model.fields().size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(");\n");
        sb.append("    }\n\n");

        // toString()
        sb.append("    @Override\n");
        sb.append("    public String toString() {\n");
        sb.append("        return \"").append(model.name()).append("{\" +\n");
        for (int i = 0; i < model.fields().size(); i++) {
            Field field = model.fields().get(i);
            sb.append("                \"");
            if (i > 0) sb.append(", ");
            sb.append(field.name()).append("=\" + ").append(field.name()).append(" +\n");
        }
        sb.append("                '}';\n");
        sb.append("    }\n");

        sb.append("}\n");
        return sb.toString();
    }

    private String mapType(String type) {
        if (type.startsWith("[") && type.endsWith("]")) {
            return "List<" + mapType(type.substring(1, type.length() - 1)) + ">";
        }
        return switch (type) {
            case "Int" -> "Integer";
            case "String" -> "String";
            case "Boolean" -> "Boolean";
            case "Double" -> "Double";
            default -> type;
        };
    }
}
