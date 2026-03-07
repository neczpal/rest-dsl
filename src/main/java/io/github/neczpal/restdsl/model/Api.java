package io.github.neczpal.restdsl.model;

import lombok.Builder;

@Builder
public record Api(String name, String title, String version, String base) {
}
