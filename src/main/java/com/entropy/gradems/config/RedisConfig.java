package com.entropy.gradems.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplateWithGenericJackson(ReactiveRedisConnectionFactory connectionFactory) {
        // 创建一个定制化的ObjectMapper实例
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // 注册JavaTimeModule
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 确保Java 8日期/时间类型以ISO格式序列化
        mapper.findAndRegisterModules(); // 自动注册所有可用的模块（包括JavaTimeModule）

        // 使用定制化的ObjectMapper实例创建GenericJackson2JsonRedisSerializer
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(mapper);
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, Object> context = builder.value(valueSerializer).build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
