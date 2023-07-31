package com.nttdata.bootcamp.ewalletservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.nttdata.bootcamp.ewalletservice.documents.EWallet;

@Configuration
public class RedisConfig {

	@Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

	@Bean
    public ReactiveRedisTemplate<String, EWallet> reactiveRedisTemplate(
            @Qualifier("reactiveRedisConnectionFactory") ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<EWallet> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(EWallet.class);

        RedisSerializationContext<String, EWallet> serializationContext = RedisSerializationContext
                .<String, EWallet>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(jsonRedisSerializer)
                .hashKey(new StringRedisSerializer())
                .hashValue(jsonRedisSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

}
