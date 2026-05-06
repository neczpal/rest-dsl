package io.github.neczpal.restdsl.model;

import lombok.Builder;

@Builder
public record Field(String name, Type type) {
}
