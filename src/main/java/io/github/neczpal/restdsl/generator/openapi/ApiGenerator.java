package io.github.neczpal.restdsl.generator.openapi;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import io.github.neczpal.restdsl.model.Api;

public class ApiGenerator {
    public YamlMappingBuilder generate(Api api) {
        YamlMappingBuilder info = Yaml.createYamlMappingBuilder()
                .add("title", api.title() != null ? api.title() : api.name())
                .add("version", api.version() != null ? api.version() : "1.0.0");

        YamlMappingBuilder openapi = Yaml.createYamlMappingBuilder()
                .add("openapi", "3.0.0")
                .add("info", info.build());

        if (api.base() != null) {
            openapi = openapi.add(
                    "servers",
                    Yaml.createYamlSequenceBuilder()
                            .add(Yaml.createYamlMappingBuilder().add("url", api.base()).build())
                            .build()
            );
        }
        return openapi;
    }
}
