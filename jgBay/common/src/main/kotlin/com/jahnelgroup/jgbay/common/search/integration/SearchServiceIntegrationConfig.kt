package com.jahnelgroup.jgbay.common.search.integration

import com.jahnelgroup.jgbay.common.search.Searchable
import com.jahnelgroup.jgbay.common.search.SearchableTransformer
import com.jahnelgroup.jgbay.common.search.integration.event.SearchUpdateEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.http.HttpMethod
import org.springframework.integration.dsl.HeaderEnricherSpec
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.channel.MessageChannels
import org.springframework.integration.dsl.http.Http
import org.springframework.integration.dsl.support.Consumer
import org.springframework.integration.dsl.support.GenericHandler
import org.springframework.integration.dsl.support.Transformers
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer
import org.springframework.integration.transformer.GenericTransformer
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel

@Configuration
class SearchServiceIntegrationConfig {

    @Value("\${service.search.uri}")
    lateinit var SEARCH_SERVICE_URI: String

    val PARSER = SpelExpressionParser()
    val SEARCH_URI = PARSER.parseExpression("headers.searchUri")

    @Autowired
    lateinit var appContext: ApplicationContext

    @Bean fun searchCreateChannel(): MessageChannel = MessageChannels.direct("searchCreateChannel").get()
    @Bean fun searchUpdateChannel(): MessageChannel = MessageChannels.direct("searchUpdateChannel").get()
    @Bean fun searchDeleteChannel(): MessageChannel = MessageChannels.direct("searchDeleteChannel").get()

    /**
     * Listen for SearchUpdateEvents and publish them on the searchUpdateChannel to update
     * the source Entity associated with the event.
     */
    @Bean
    fun domainEvents(): ApplicationEventListeningMessageProducer {
        val producer = ApplicationEventListeningMessageProducer()
        producer.setEventTypes(SearchUpdateEvent::class.java)
        producer.setOutputChannelName("searchCreateChannel")
        return producer
    }

    fun transformPayload() = GenericTransformer<Message<Any>, Any> {
        var from = it.payload
        var transformer = appContext.getBean(from.javaClass.getAnnotation(Searchable::class.java).transformRef)
                as SearchableTransformer<*, *>
        transformer.from(from)
    }

    fun searchableEvent(event: ApplicationEvent): Boolean =
        event.source.javaClass.isAnnotationPresent(Searchable::class.java)

    fun enrichPayloadId() = Consumer<HeaderEnricherSpec> {
        it.headerExpression("payloadId", "payload.id")
    }

    fun enrichPayloadIndex() = Consumer<HeaderEnricherSpec> {
        it.headerFunction<Any>("payloadIndex"){
            it.payload.javaClass.getAnnotation(Searchable::class.java).index
        }
    }

    fun enrichSearchUri() = Consumer<HeaderEnricherSpec> {
        it.headerFunction<Any>("searchUri"){
            """${SEARCH_SERVICE_URI}/${it.headers["payloadIndex"]}"""
        }
    }

    fun enrichSearchIdUri() = Consumer<HeaderEnricherSpec> {
        it.headerFunction<Any>("searchUri"){
            """${SEARCH_SERVICE_URI}/${it.headers["payloadIndex"]}/${it.headers["payloadId"]}"""
        }
    }

    /**
     * POST
     */
    @Bean
    fun createESFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchCreateChannel())
                .filter(this::searchableEvent)
                .log()
                .transform(ApplicationEvent::getSource)
                .enrichHeaders(enrichPayloadId())
                .enrichHeaders(enrichPayloadIndex())
                .enrichHeaders(enrichSearchUri())
                .transform(transformPayload())
                .transform(Transformers.toJson())
                .handle(Http.outboundGateway(SEARCH_URI).httpMethod(HttpMethod.POST))
                .handle(GenericHandler<Any> { resp, _ -> println(resp) })
                .get()
    }

    /**
     * PUT
     */
    @Bean
    fun updateESFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchUpdateChannel())
                .filter(this::searchableEvent)
                .log()
                .transform(ApplicationEvent::getSource)
                .enrichHeaders(enrichPayloadId())
                .enrichHeaders(enrichPayloadIndex())
                .enrichHeaders(enrichSearchIdUri())
                .transform(transformPayload())
                .transform(Transformers.toJson())
                .handle(Http.outboundGateway(SEARCH_URI).httpMethod(HttpMethod.PUT))
                .handle(GenericHandler<Any> { resp, _ -> println(resp) })
                .get()
    }

    /**
     * DELETE
     */
    @Bean
    fun deleteAuctionESFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchDeleteChannel())
                .filter(this::searchableEvent)
                .log()
                .transform(RepositoryEvent::getSource)
                .enrichHeaders(enrichPayloadId())
                .enrichHeaders(enrichPayloadIndex())
                .enrichHeaders(enrichSearchIdUri())
                .transform(transformPayload())
                .transform(Transformers.toJson())
                .handle(Http.outboundGateway(SEARCH_URI).httpMethod(HttpMethod.DELETE))
                .handle(GenericHandler<Any> { resp, _ -> println(resp) })
                .get()
    }

}