package com.jahnelgroup.jgbay.search.integration.elasticsearch

import org.aopalliance.aop.Advice
import org.springframework.aop.framework.ProxyFactory
import org.springframework.beans.factory.config.AbstractFactoryBean
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.integration.jpa.support.OutboundGatewayType
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler
import org.springframework.util.ClassUtils
import org.springframework.util.CollectionUtils

class ElasticsearchGatewayFactoryBean<T>(
        private var documentClass : Class<T>,
        private var documentRepository : ElasticsearchRepository<T, *>
) : AbstractFactoryBean<MessageHandler>() {

    var gatewayType : OutboundGatewayType = OutboundGatewayType.UPDATING
    var txAdviceChain : List<Advice> = ArrayList()
    var adviceChain : List<Advice> = ArrayList()
    var producesReply : Boolean = true
    var persistMode : Elasticsearch.Companion.PersistMode = Elasticsearch.Companion.PersistMode.SAVE
    var outputChannel : MessageChannel? = null
    var order : Int = 0
    var replyTimeout : Long = 0
    var requiresReply : Boolean  = false
    var componentName : String? = null

    override fun getObjectType() : Class<ElasticsearchOutboundGateway<*>> = ElasticsearchOutboundGateway::class.java

    override fun createInstance() : MessageHandler {
        val elasticsearchOutboundGateway = ElasticsearchOutboundGateway(
                this.documentClass, this.documentRepository, this.persistMode)

        elasticsearchOutboundGateway.producesReply = this.producesReply
        elasticsearchOutboundGateway.outputChannel = this.outputChannel
        elasticsearchOutboundGateway.order = this.order
        elasticsearchOutboundGateway.setSendTimeout(this.replyTimeout)
        elasticsearchOutboundGateway.producesReply = this.requiresReply
        elasticsearchOutboundGateway.componentName = this.componentName
        if (!this.adviceChain.isEmpty()) {
            elasticsearchOutboundGateway.setAdviceChain(this.adviceChain)
        }
        elasticsearchOutboundGateway.setBeanFactory(super.getBeanFactory())
        elasticsearchOutboundGateway.afterPropertiesSet()
        if (!CollectionUtils.isEmpty(this.txAdviceChain)) {
            val proxyFactory = ProxyFactory(elasticsearchOutboundGateway)
            txAdviceChain.forEach { proxyFactory.addAdvice(it) }

            return proxyFactory.getProxy(ClassUtils.getDefaultClassLoader()) as MessageHandler
        }

        return elasticsearchOutboundGateway
    }

}