package com.jahnelgroup.jgbay.search.integration.elasticsearch

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler
import org.springframework.messaging.Message

class ElasticsearchOutboundGateway<T>(
        private var documentClass : Class<T>,
        private var documentRepository : ElasticsearchRepository<T, *>,
        private var persistMode : Elasticsearch.Companion.PersistMode) : AbstractReplyProducingMessageHandler() {

    var producesReply : Boolean = false

    override fun handleRequestMessage(requestMessage: Message<*>): Any? {
        val payload = requestMessage.payload
        val entity : T = documentClass.cast(payload)

        if( persistMode == Elasticsearch.Companion.PersistMode.SAVE){
            val toReturn = documentRepository.save(entity)
            if(producesReply) {
                return toReturn as Any
            }
        }else{
            documentRepository.delete(entity)
        }

        return null
    }

}