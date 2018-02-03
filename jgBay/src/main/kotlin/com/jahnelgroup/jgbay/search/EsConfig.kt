package com.jahnelgroup.jgbay.search

import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import java.net.InetAddress

@Configuration
@EnableElasticsearchRepositories(basePackages = arrayOf("com.jahnelgroup.jgbay"))
class EsConfig {

    @Value("\${elasticsearch.host}")
    private val EsHost: String? = null

    @Value("\${elasticsearch.port}")
    private val EsPort: Int = 0

    @Value("\${elasticsearch.clustername}")
    private val EsClusterName: String? = null

    @Bean
    @Throws(Exception::class)
    fun client(): Client {

        val esSettings = Settings.settingsBuilder()
                .put("cluster.name", EsClusterName)
                .build()

        //https://www.elastic.co/guide/en/elasticsearch/guide/current/_transport_client_versus_node_client.html
        return TransportClient.builder()
                .settings(esSettings)
                .build()
                .addTransportAddress(
                        InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort))
    }

    @Bean
    @Throws(Exception::class)
    fun elasticsearchTemplate(): ElasticsearchOperations {
        return ElasticsearchTemplate(client())
    }

    //Embedded Elasticsearch Server
    /*@Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchTemplate(nodeBuilder().local(true).node().client());
    }*/

}