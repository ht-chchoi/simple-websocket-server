package com.example.simplewebsocketserver

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import javax.annotation.PostConstruct

@Component
class KafkaConsumerManager {
    lateinit var receiverOptions:ReceiverOptions<String, String>
    lateinit var kafkaReceiver: KafkaReceiver<String, String>

    @PostConstruct
    fun init() {
        val consumerConfig = HashMap<String, Any>();
        consumerConfig[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "192.168.2.89:29091"
        consumerConfig[org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG] = "test-1"
        consumerConfig[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        consumerConfig[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        consumerConfig[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] =
            OffsetResetStrategy.EARLIEST.name.lowercase(Locale.getDefault())
        consumerConfig[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 10

        this.receiverOptions = ReceiverOptions.create(consumerConfig)
        this.kafkaReceiver = KafkaReceiver
            .create(this.receiverOptions
                .commitInterval(Duration.of(10, ChronoUnit.MILLIS))
                .commitBatchSize(1)
                .subscription(listOf("chchoi-test-1")))
    }
}