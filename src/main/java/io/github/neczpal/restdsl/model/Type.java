package io.github.neczpal.restdsl.model;

import lombok.Builder;

@Builder
public record Type(String name, boolean isArray, boolean isPrimitive, Type elementType) {
}
