package io.github.neczpal.restdsl.generator;

import io.github.neczpal.restdsl.model.Api;
import io.github.neczpal.restdsl.model.Model;
import io.github.neczpal.restdsl.model.Service;

import java.util.List;

public interface Generator {
    String generate(Api api, List<Model> models, List<Service> services);
}
