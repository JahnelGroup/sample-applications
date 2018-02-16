package com.jahnelgroup.jgbay.common.search.integration

import com.jahnelgroup.jgbay.common.search.Searchable
import com.jahnelgroup.jgbay.common.search.SearchableTransformer
import com.jahnelgroup.jgbay.common.search.isSearchable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.dsl.HeaderEnricherSpec
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.channel.MessageChannels
import org.springframework.integration.dsl.http.Http
import org.springframework.integration.dsl.support.Consumer
import org.springframework.integration.dsl.support.GenericHandler
import org.springframework.integration.dsl.support.Transformers
import org.springframework.integration.transformer.GenericTransformer
import org.springframework.messaging.Message
import javax.naming.OperationNotSupportedException

/**
 * Inbound gateway integration flow to the search service.
 */
@Configuration
class SearchGatewayIntegrationConfig {

    @Value("\${service.search.uri}") lateinit var URI: String
    @Autowired lateinit var appContext: ApplicationContext

    @Bean fun searchInboundGatewayChannel(): DirectChannel =
            MessageChannels.direct("searchInboundGatewayChannel").get()

    /**
     * Search service gateway destined for Elasticsearch
     */
    @Bean
    fun searchInboundGatewayChannelFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchInboundGatewayChannel())
                .filter(ApplicationEvent::isSearchable)
                .log()
                .transform(ApplicationEvent::getSource)
                .enrichHeaders(enrichPayloadId())
                .enrichHeaders(enrichPayloadIndex())
                .transform(transformPayload())
                .transform(Transformers.toJson())
                .handle(Http.outboundGateway(this::uri).httpMethodFunction(this::method))
                .handle(GenericHandler<Any> { resp, _ -> println(resp) })
                .get()
    }

    /**
     * Enriches headers with payload id
     */
    fun enrichPayloadId() = Consumer<HeaderEnricherSpec> {
        it.headerExpression("payloadId", "payload.id")
    }

    /**
     * Enriches headers with payload index as defined by the @Searchable annotation on the event source
     */
    fun enrichPayloadIndex() = Consumer<HeaderEnricherSpec> {
        it.headerFunction<Any>("payloadIndex"){
            it.payload.javaClass.getAnnotation(Searchable::class.java).index
        }
    }

    /**
     * Invokes the respective transform as defined by the @Searchable annotation on the event source
     */
    fun transformPayload() = GenericTransformer<Message<Any>, Any> {
        var from = it.payload
        var transformer = appContext.getBean(from.javaClass.getAnnotation(Searchable::class.java).transformRef)
                as SearchableTransformer<Any>
        transformer.from(from)
    }

    /**
     * Maps searchAction(create, update, delete) to their respective URI
     */
    fun uri(it: Message<Any>) : String {
        var index = it.headers["payloadIndex"]
        var id = it.headers["payloadId"]
        return when(it.headers["searchAction"]){
            "create"            -> "$URI/$index/$id"
            "update", "delete"  -> "$URI/$index/$id"
            else                -> throw OperationNotSupportedException()
        }
    }

    /**
     * Maps searchAction(create, update, delete) to HttpMethod (POST, PUT, DELETE)
     */
    fun method(it: Message<Any>): HttpMethod {
        return when(it.headers["searchAction"]){
            "create" -> HttpMethod.POST
            "update" -> HttpMethod.PUT
            "delete" -> HttpMethod.DELETE
            else     -> throw OperationNotSupportedException()
        }
    }

}