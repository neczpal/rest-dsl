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
    Type bodyType,
    List<Field> pathParams,
    List<Field> queryParams,
    Map<Integer, Type> responses
) {
    public Method {
        if (pathParams == null) pathParams = new ArrayList<>();
        if (queryParams == null) queryParams = new ArrayList<>();
        if (responses == null) responses = new HashMap<>();
    }
}
