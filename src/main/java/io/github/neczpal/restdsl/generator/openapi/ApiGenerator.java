package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Api;

public class ApiGenerator {
    public String generate(Api api) {
        StringBuilder sb = new StringBuilder();
        sb.append("openapi: 3.0.0\n");
        sb.append("info:\n");
        sb.append("  title: ").append(api.title() != null ? api.title() : api.name()).append("\n");
        sb.append("  version: ").append(api.version() != null ? api.version() : "1.0.0").append("\n");
        if (api.base() != null) {
            sb.append("servers:\n");
            sb.append("  - url: ").append(api.base()).append("\n");
        }
        return sb.toString();
    }
}
