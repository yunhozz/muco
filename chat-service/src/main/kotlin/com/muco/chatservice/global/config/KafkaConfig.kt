package com.muco.chatservice.global.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
@EnableKafka
class KafkaConfig {

    companion object {
        const val CONSUMER_FACTORY = "consumerFactory"
        const val PRODUCER_FACTORY = "producerFactory"
        const val LISTENER_CONTAINER_FACTORY = "listenerContainerFactory"
    }

    @Bean(CONSUMER_FACTORY)
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val configs = hashMapOf<String, Any>()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092, localhost:9093, localhost:9094"
        configs[ConsumerConfig.GROUP_ID_CONFIG] = "servicesConsumerGroupId"
        configs[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configs[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        return DefaultKafkaConsumerFactory(configs)
    }

    @Bean(PRODUCER_FACTORY)
    fun producerFactory(): ProducerFactory<String, Any> {
        val configs = hashMapOf<String, Any>()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092, localhost:9093, localhost:9094"
        configs[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configs[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return DefaultKafkaProducerFactory(configs)
    }

    @Bean(LISTENER_CONTAINER_FACTORY)
    fun concurrentKafkaListenerContainerFactory(
        @Qualifier(CONSUMER_FACTORY) factory: ConsumerFactory<String, Any>
    ): ConcurrentKafkaListenerContainerFactory<String, Any> =
        object : ConcurrentKafkaListenerContainerFactory<String, Any>() {
            init {
                consumerFactory = factory
            }
        };

    @Bean
    fun kafkaTemplate(
        @Qualifier(PRODUCER_FACTORY) factory: ProducerFactory<String, Any>
    ): KafkaTemplate<String, Any> = KafkaTemplate(factory)
}