package com.nttdata.bootcamp.ewalletservice.config;

import java.util.Arrays;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;


@Configuration
public class KafkaConfig {


	@Bean
    public KafkaReceiver<String, String> kafkaReceiver() {
        ReceiverOptions<String, String> receiverOptions = ReceiverOptions.<String, String>create()
                .consumerProperty("bootstrap.servers", "localhost:29092")
                .consumerProperty("key.deserializer", StringDeserializer.class)
                .consumerProperty("value.deserializer", StringDeserializer.class)
                .consumerProperty("group.id", "myConsumerGroup")
                .subscription(Arrays.asList("topicUpdateWallet2", "updateBalanceWallet"));  
        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public KafkaSender<String, String> kafkaSender() {
        SenderOptions<String, String> senderOptions = SenderOptions.<String, String>create()
                .producerProperty("bootstrap.servers", "localhost:29092")
                .producerProperty("key.serializer", StringSerializer.class)
                .producerProperty("value.serializer", StringSerializer.class);
        return KafkaSender.create(senderOptions);
    }

}