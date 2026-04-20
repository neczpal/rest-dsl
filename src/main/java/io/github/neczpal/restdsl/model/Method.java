package io.github.neczpal.restdsl.model;

import lombok.Builder;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Builder
public record Method(
    String verb,
    String name,
    String path,
    String summary,
    String description,
    String bodyType,
    List<Field> pathParams,
    List<Field> queryParams,
    Map<Integer, String> responses
) {
    public Method {
        if (pathParams == null) pathParams = new ArrayList<>();
        if (queryParams == null) queryParams = new ArrayList<>();
        if (responses == null) responses = new HashMap<>();
    }
}
