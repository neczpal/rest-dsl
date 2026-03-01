package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Api;

public class ApiGenerator {
    public String generate(Api api) {
        StringBuilder sb = new StringBuilder();
        sb.append("openapi: 3.0.0\n");
        sb.append("info:\n");
        sb.append("  title: ").append(api.getTitle() != null ? api.getTitle() : api.getName()).append("\n");
        sb.append("  version: ").append(api.getVersion() != null ? api.getVersion() : "1.0.0").append("\n");
        if (api.getBase() != null) {
            sb.append("servers:\n");
            sb.append("  - url: ").append(api.getBase()).append("\n");
        }
        return sb.toString();
    }
}
