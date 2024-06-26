package com.example.stompTest.service;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.example.stompTest.Exception.CustomException;
import com.example.stompTest.Exception.ErrorCode;
import com.example.stompTest.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;


    //convertAndSend 로 데이터를 보내면 여기서 잡아서 보낸다.
    // Redis 에서 메시지가 발행(publish)되면 대기하고 있던 Redis Subscriber 가 해당 메시지를 받아 처리한다.
    public void sendMessage(String publishMessage) {
        log.info("publishMessage={}", publishMessage);
        try {
            // ChatMessage 객채로 맵핑
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            // 채팅방을 구독한 클라이언트에게 메시지 발송
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoom().getId(), chatMessage);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.MESSAGE_SEND_FAILED);
        }
    }
    
}