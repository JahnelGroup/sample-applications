package com.jahnelgroup.jgbay.integration.elasticsearch

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

class Elasticsearch {

    companion object {
        enum class PersistMode {
            SAVE, DELETE
        }

        fun <T> outboundAdapter(documentClass : Class<T>,
                                documentRepository : ElasticsearchRepository<T, *>) : ElasticsearchOutboundAdapterSpec<T> {
            return ElasticsearchOutboundAdapterSpec(documentClass, documentRepository)
                    .producesReply(false)
                    .persistMode(PersistMode.SAVE)
        }
    }
}