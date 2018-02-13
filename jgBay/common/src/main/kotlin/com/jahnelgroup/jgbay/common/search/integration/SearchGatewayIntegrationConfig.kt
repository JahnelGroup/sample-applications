package com.jahnelgroup.jgbay.common.search.integration

import com.jahnelgroup.jgbay.common.search.Searchable
import com.jahnelgroup.jgbay.common.search.SearchableTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.expression.spel.standard.SpelExpressionParser
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
import org.springframework.messaging.MessageChannel
import javax.naming.OperationNotSupportedException

@Configuration
class SearchGatewayIntegrationConfig {

    @Value("\${service.search.uri}")
    lateinit var URI: String

    @Bean fun searchInboundGatewayChannel(): DirectChannel = MessageChannels.direct("searchInboundGatewayChannel").get()

    @Autowired
    lateinit var appContext: ApplicationContext

    /**
     * Search service gateway destined for Elasticsearch
     */
    @Bean
    fun searchInboundGatewayChannelFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchInboundGatewayChannel())
                .filter(this::searchablePayload)
                .log()
                .transform(ApplicationEvent::getSource)
                .enrichHeaders(enrichPayloadId())
                .enrichHeaders(enrichPayloadIndex())
                .transform(transformPayload())
                .transform(Transformers.toJson())
                .handle(Http.outboundGateway {it: Message<String> ->
                    var index = it.headers["payloadIndex"]
                    var id = it.headers["payloadId"]
                    when(it.headers["searchAction"]){
                        "create" ->"$URI/$index"
                        "update", "delete" ->"$URI/$index/$id"
                        else -> throw OperationNotSupportedException()
                    }
                }.httpMethodFunction {it: Message<String> ->
                            when(it.headers["searchAction"]){
                                "create" ->HttpMethod.POST
                                "update" ->HttpMethod.PUT
                                "delete" ->HttpMethod.DELETE
                                else -> throw OperationNotSupportedException()
                            }

                        })



                .handle(GenericHandler<Any> { resp, _ -> println(resp) })
                .get()
    }

    fun transformPayload() = GenericTransformer<Message<Any>, Any> {
        var from = it.payload
        var transformer = appContext.getBean(from.javaClass.getAnnotation(Searchable::class.java).transformRef)
                as SearchableTransformer<*, *>
        transformer.from(from)
    }

    fun searchablePayload(any: ApplicationEvent): Boolean =
        any.source.javaClass.isAnnotationPresent(Searchable::class.java)

    fun enrichPayloadId() = Consumer<HeaderEnricherSpec> {
        it.headerExpression("payloadId", "payload.id")
    }

    fun enrichPayloadIndex() = Consumer<HeaderEnricherSpec> {
        it.headerFunction<Any>("payloadIndex"){
            it.payload.javaClass.getAnnotation(Searchable::class.java).index
        }
    }

}