package com.jahnelgroup.jgbay.integration.elasticsearch

import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.integration.dsl.core.MessageHandlerSpec
import org.springframework.util.Assert
import java.io.Serializable

open class ElasticsearchOutboundEndpointSpec<T, S : ElasticsearchOutboundEndpointSpec<T, S>>(
        private var documentClass : Class<T>,
        private var documentRepository : ElasticsearchRepository<T, *>
)
    : MessageHandlerSpec<S, ElasticsearchOutboundGateway<T>>() {

    var elasticsearchGatewayFactoryBean : ElasticsearchGatewayFactoryBean<T> =
            ElasticsearchGatewayFactoryBean(documentClass, documentRepository)

    override fun doGet() : ElasticsearchOutboundGateway<T> {
        Assert.notNull(documentClass, "documentClass must not be null.")
        Assert.notNull(documentRepository, "documentRepository must not be null.")
        this.elasticsearchGatewayFactoryBean.setBeanFactory(DefaultListableBeanFactory())

        try {
            this.elasticsearchGatewayFactoryBean.afterPropertiesSet()
            return this.elasticsearchGatewayFactoryBean.`object` as ElasticsearchOutboundGateway<T>
        }
        catch (e : Exception) {
            throw BeanCreationException("Cannot create the ElasticsearchOutboundGateway", e)
        }
    }

}