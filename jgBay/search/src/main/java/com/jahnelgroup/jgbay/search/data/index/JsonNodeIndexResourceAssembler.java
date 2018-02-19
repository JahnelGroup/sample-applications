package com.jahnelgroup.jgbay.search.data.index;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class JsonNodeIndexResourceAssembler extends IdentifiableResourceAssemblerSupport<JsonNodeIndex, ResourceSupport> {

    public JsonNodeIndexResourceAssembler() {
        super(IndexController.class, ResourceSupport.class);
    }

    @Override
    public ResourceSupport toResource(JsonNodeIndex entity) {
        return super.createResource(entity);
    }

}
