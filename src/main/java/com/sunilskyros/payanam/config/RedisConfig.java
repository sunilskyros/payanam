package com.sunilskyros.payanam.config;

import com.sunilskyros.payanam.features.realtime.service.RedisMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Safe default LettuceConnectionFactory pointing to localhost/standard environment variables
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public ChannelTopic locationTopic() {
        return new ChannelTopic("payanam:bus:locations");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter,
            ChannelTopic locationTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        
        // Dynamically probe port 6379 to check if local/cloud Redis is active
        boolean redisAvailable = false;
        try (java.net.Socket socket = new java.net.Socket("localhost", 6379)) {
            redisAvailable = true;
        } catch (Exception ignored) {}
        
        if (redisAvailable) {
            container.addMessageListener(listenerAdapter, locationTopic);
            System.out.println("Redis is ONLINE. Clustered live synchronization is ACTIVE.");
        } else {
            System.out.println("Warning: Redis is OFFLINE. Clustered synchronization is disabled. Local WebSocket sync is active.");
        }
        
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisMessageSubscriber redisMessageSubscriber) {
        // Delegates messages to a Subscriber bean's 'onMessage' method
        return new MessageListenerAdapter(redisMessageSubscriber, "onMessage");
    }
}
