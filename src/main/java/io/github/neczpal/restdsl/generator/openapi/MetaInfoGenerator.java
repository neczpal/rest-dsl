package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Api;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

public class MetaInfoGenerator {
    public void generate(OpenAPI openapi, Api api) {
        Info info = new Info()
                .title(api.title() != null ? api.title() : api.name())
                .version(api.version() != null ? api.version() : "1.0.0");

        openapi.info(info);

        if (api.base() != null) {
            openapi.servers(List.of(new Server().url(api.base())));
        }
    }
}
