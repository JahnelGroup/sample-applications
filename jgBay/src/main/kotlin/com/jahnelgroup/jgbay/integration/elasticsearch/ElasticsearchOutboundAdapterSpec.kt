package com.jahnelgroup.jgbay.integration.elasticsearch

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

class ElasticsearchOutboundAdapterSpec<T>(documentClass : Class<T>, documentRepository : ElasticsearchRepository<T, *>)
    : ElasticsearchOutboundEndpointSpec<T, ElasticsearchOutboundAdapterSpec<T>>(documentClass, documentRepository) {

    fun producesReply(producesReply : Boolean) : ElasticsearchOutboundAdapterSpec<T> {
        super.elasticsearchGatewayFactoryBean.producesReply = producesReply
        return this
    }

    fun persistMode(persistMode: Elasticsearch.Companion.PersistMode) : ElasticsearchOutboundAdapterSpec<T> {
        super.elasticsearchGatewayFactoryBean.persistMode = persistMode
        return this
    }

}