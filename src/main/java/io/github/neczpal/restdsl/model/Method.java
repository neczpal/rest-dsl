package io.github.neczpal.restdsl.model;

import java.util.List;
import java.util.Map;

public class Method {
    private String verb;
    private String name;
    private String path;
    private String bodyType;
    private List<Field> pathParams;
    private List<Field> queryParams;
    private Map<Integer, String> responses;

    public Method(String verb, String name, String path, String bodyType, List<Field> pathParams, List<Field> queryParams, Map<Integer, String> responses) {
        this.verb = verb;
        this.name = name;
        this.path = path;
        this.bodyType = bodyType;
        this.pathParams = pathParams;
        this.queryParams = queryParams;
        this.responses = responses;
    }

    public String getVerb() {
        return verb;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getBodyType() {
        return bodyType;
    }

    public List<Field> getPathParams() {
        return pathParams;
    }

    public List<Field> getQueryParams() {
        return queryParams;
    }

    public Map<Integer, String> getResponses() {
        return responses;
    }
}
