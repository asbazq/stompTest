package com.example.stompTest.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.stompTest.service.RedisSubscriber;
import com.example.stompTest.service.SseRedisSubscriber;

@Configuration
public class RedisConfig {
    
    // 단일 Topic 사용을 위한 Bean 설정
    //채팅
    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("chat");
    }

    //알림 (sse)
    @Bean
    public ChannelTopic sseTopic() {
        return new ChannelTopic("sse");
    }

    // 실제 메시지를 처리하는 subscriber 설정 추가
    // 실제 pub가 실행되면 이곳을 통해 데이터가 나가게 된다.
    // pub 데이터가 sendMessage로 던져짐
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "sendMessage");
    }

    @Bean
    public MessageListenerAdapter sseListenerAdapter(SseRedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "sendMessage");
    }

     // 어플리케이션에서 사용할 redisTemplate 설정
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory,
                                                              MessageListenerAdapter listenerAdapter,
                                                              MessageListenerAdapter sseListenerAdapter,
                                                              ChannelTopic channelTopic,
                                                              ChannelTopic sseTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //채팅 리스너
        container.addMessageListener(listenerAdapter, channelTopic);
        //알림 리스너 (sse)
        container.addMessageListener(sseListenerAdapter, sseTopic);
        return container;
    }

}
