package com.muco.chatservice.global.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeFunctions
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Configuration
class WebClientConfig {

    companion object {
        val OM: ObjectMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(JavaTimeModule())

        const val COMMON_WEB_CLIENT: String = "commonWebClient"
        const val WEB_CLIENT_HTTP_CLIENT: String = "defaultHttpClient"
        const val WEB_CLIENT_CONNECTION_PROVIDER: String = "connectionProvider"
        const val WEB_CLIENT_EXCHANGE_STRATEGIES: String = "defaultExchangeStrategies"
    }

    @Bean(name = [COMMON_WEB_CLIENT])
    fun commonWebClient(
            @Qualifier(value = WEB_CLIENT_EXCHANGE_STRATEGIES) exchangeStrategies: ExchangeStrategies,
            @Qualifier(value = WEB_CLIENT_HTTP_CLIENT) httpClient: HttpClient
    ): WebClient {

        return WebClient
                .builder()
                .clientConnector(ReactorClientHttpConnector(HttpClient.create()))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .exchangeFunction(ExchangeFunctions.create(ReactorClientHttpConnector(httpClient), exchangeStrategies))
                .exchangeStrategies(exchangeStrategies)
                .build()
    }

    @Bean(name = [WEB_CLIENT_HTTP_CLIENT])
    fun defaultHttpClient(@Qualifier(value = WEB_CLIENT_CONNECTION_PROVIDER) provider: ConnectionProvider): HttpClient {

        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected { conn: Connection ->
                    conn.addHandlerLast(ReadTimeoutHandler(5)) // 읽기시간초과 타임아웃
                            .addHandlerLast(WriteTimeoutHandler(5))
                }
    }

    @Bean(name = [WEB_CLIENT_CONNECTION_PROVIDER])
    fun connectionProvider(): ConnectionProvider {

        return ConnectionProvider.builder("http-pool")
                .maxConnections(100) // connection pool의 갯수
                .pendingAcquireTimeout(Duration.ofMillis(0)) // 커넥션 풀에서 커넥션을 얻기 위해 기다리는 최대 시간
                .pendingAcquireMaxCount(-1) // 커넥션 풀에서 커넥션을 가져오는 시도 횟수 (-1: no limit)
                .maxIdleTime(Duration.ofMillis(2000L)) // 커넥션 풀에서 idle 상태의 커넥션을 유지하는 시간
                .build()
    }

    @Bean(name = [WEB_CLIENT_EXCHANGE_STRATEGIES])
    fun defaultExchangeStrategies(): ExchangeStrategies {

        return ExchangeStrategies.builder().codecs { config: ClientCodecConfigurer ->
            config.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(OM, MediaType.APPLICATION_JSON))
            config.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(OM, MediaType.APPLICATION_JSON))
            config.defaultCodecs().maxInMemorySize(1024 * 1024) // max buffer 1MB 고정. default: 256 * 1024
        }.build()
    }
}
