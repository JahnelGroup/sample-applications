package com.jahnelgroup.jgbay.search.data.document;

import com.jahnelgroup.jgbay.search.data.index.IndexController;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class JsonNodeDocumentResourceAssembler extends IdentifiableResourceAssemblerSupport<JsonNodeDocument, ResourceSupport> {

    public JsonNodeDocumentResourceAssembler() {
        super(IndexController.class, ResourceSupport.class);
    }

    @Override
    public ResourceSupport toResource(JsonNodeDocument entity) {
        return super.createResource(entity);
    }

}
