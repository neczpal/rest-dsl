package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;
import io.github.neczpal.restdsl.model.Trait;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.neczpal.restdsl.generator.openapi.TypeMapper.generateSchemaType;

public class ModelGenerator {
    public void generate(OpenAPI openapi, List<Trait> allTraits, List<Model> allModels) {
        Components components = openapi.getComponents() == null ? new Components() : openapi.getComponents();
        openapi.components(components);

        // Pass 1: Generate schemas for all traits
        if (allTraits != null) {
            for (Trait trait : allTraits) {
                generateTraitSchema(components, trait);
            }
        }

        // Pass 2: Generate schemas for all models
        if (allModels != null) {
            for (Model model : allModels) {
                ObjectSchema modelSchema = new ObjectSchema();
                for (Field field : model.fields()) {
                    modelSchema.addProperty(field.name(), generateSchemaType(field.type()));
                }

                // Only use ComposedSchema if there's inheritance or traits
                boolean useComposedSchema = (model.parent() != null) || (model.traits() != null && !model.traits().isEmpty());

                if (useComposedSchema) {
                    ComposedSchema finalSchema = new ComposedSchema();
                    if (model.parent() != null) {
                        finalSchema.addAllOfItem(new Schema().$ref("#/components/schemas/" + model.parent().name()));
                    }
                    if (model.traits() != null) {
                        for (Trait trait : model.traits()) {
                            finalSchema.addAllOfItem(new Schema().$ref("#/components/schemas/" + trait.name()));
                        }
                    }
                    finalSchema.addAllOfItem(modelSchema);
                    components.addSchemas(model.name(), finalSchema);
                } else {
                    // It's a simple model
                    components.addSchemas(model.name(), modelSchema);
                }
            }
        }

        // Pass 3: Add discriminators to parent models
        if (allModels != null) {
            for (Model model : allModels) {
                List<Model> children = allModels.stream()
                        .filter(m -> model.equals(m.parent()))
                        .collect(Collectors.toList());

                if (!children.isEmpty()) {
                    Schema parentSchema = components.getSchemas().get(model.name());
                    Discriminator discriminator = new Discriminator().propertyName("_type");
                    children.forEach(child -> discriminator.mapping(child.name(), "#/components/schemas/" + child.name()));
                    parentSchema.setDiscriminator(discriminator);
                }
            }
        }
    }

    private void generateTraitSchema(Components components, Trait trait) {
        if (components.getSchemas() != null && components.getSchemas().containsKey(trait.name())) {
            return;
        }

        ObjectSchema traitSchema = new ObjectSchema();
        for (Field field : trait.fields()) {
            traitSchema.addProperty(field.name(), generateSchemaType(field.type()));
        }

        boolean useComposedSchema = trait.traits() != null && !trait.traits().isEmpty();

        if (useComposedSchema) {
            ComposedSchema finalSchema = new ComposedSchema();
            for (Trait includedTrait : trait.traits()) {
                generateTraitSchema(components, includedTrait);
                finalSchema.addAllOfItem(new Schema().$ref("#/components/schemas/" + includedTrait.name()));
            }
            finalSchema.addAllOfItem(traitSchema);
            components.addSchemas(trait.name(), finalSchema);
        } else {
            components.addSchemas(trait.name(), traitSchema);
        }
    }
}
