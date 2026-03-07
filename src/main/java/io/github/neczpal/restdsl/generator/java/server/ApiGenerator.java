package io.github.neczpal.restdsl.generator.java.server;

import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Method;
import io.github.neczpal.restdsl.model.Service;

import java.util.ArrayList;
import java.util.List;

public class ApiGenerator {

    public List<GeneratedFile> generate(List<Service> services) {
        if (services == null || services.isEmpty()) {
            return List.of();
        }

        List<GeneratedFile> files = new ArrayList<>();
        for (Service service : services) {
            files.add(new GeneratedFile(service.name() + "Api.java", generateController(service)));
        }
        return files;
    }

    private String generateController(Service service) {
        StringBuilder sb = new StringBuilder();
        String controllerName = service.name() + "Api";

        sb.append("import org.springframework.web.bind.annotation.*;\n");
        sb.append("import org.springframework.http.ResponseEntity;\n");
        sb.append("import java.util.List;\n\n");

        sb.append("@RestController\n");
        String basePath = service.base() != null ? service.base() : "";
        sb.append("@RequestMapping(\"").append(basePath).append("\")\n");
        sb.append("public interface ").append(controllerName).append(" {\n\n");

        for (Method method : service.methods()) {
            sb.append(generateMethod(method));
            sb.append("\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String generateMethod(Method method) {
        StringBuilder sb = new StringBuilder();

        String verb = method.verb().toUpperCase();
        String path = method.path() != null ? method.path() : "";

        String mappingAnnotation;
        switch (verb) {
            case "GET" -> mappingAnnotation = "@GetMapping(\"" + path + "\")";
            case "POST" -> mappingAnnotation = "@PostMapping(\"" + path + "\")";
            case "PUT" -> mappingAnnotation = "@PutMapping(\"" + path + "\")";
            case "DELETE" -> mappingAnnotation = "@DeleteMapping(\"" + path + "\")";
            case "PATCH" -> mappingAnnotation = "@PatchMapping(\"" + path + "\")";
            default -> mappingAnnotation = "@RequestMapping(method = RequestMethod." + verb + ", path = \"" + path + "\")";
        }

        sb.append("    ").append(mappingAnnotation).append("\n");

        String returnType = determineReturnType(method);
        sb.append("    ResponseEntity<").append(returnType).append("> ").append(method.name()).append("(");

        List<String> params = new ArrayList<>();

        // Path Variables
        for (Field field : method.pathParams()) {
            params.add("@PathVariable(\"" + field.name() + "\") " + mapType(field.type()) + " " + field.name());
        }

        // Query Parameters
        for (Field field : method.queryParams()) {
            params.add("@RequestParam(\"" + field.name() + "\") " + mapType(field.type()) + " " + field.name());
        }

        // Request Body
        if (method.bodyType() != null) {
            params.add("@RequestBody " + mapType(method.bodyType()) + " body");
        }

        sb.append(String.join(", ", params));
        sb.append(");\n");

        return sb.toString();
    }

    private String determineReturnType(Method method) {
        if (method.responses() == null || !method.responses().containsKey(200)) {
            return "Void";
        }

        String responseType = method.responses().get(200);
        if (responseType == null || responseType.trim().isEmpty() || responseType.startsWith("\"")) {
            return "Void";
        }

        return mapType(responseType);
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
