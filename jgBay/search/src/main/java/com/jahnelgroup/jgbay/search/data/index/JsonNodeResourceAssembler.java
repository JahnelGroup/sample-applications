package com.jahnelgroup.jgbay.search.data.index;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;

public class JsonNodeResourceAssembler extends IdentifiableResourceAssemblerSupport<JsonNodeDocument, ResourceSupport> {

    public JsonNodeResourceAssembler() {
        super(IndexController.class, ResourceSupport.class);
    }

    @Override
    public ResourceSupport toResource(JsonNodeDocument entity) {
        return super.createResource(entity);
    }

}
