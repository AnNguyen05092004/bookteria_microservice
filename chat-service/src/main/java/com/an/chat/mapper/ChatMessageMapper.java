package com.an.chat.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.an.chat.dto.request.ChatMessageRequest;
import com.an.chat.dto.response.ChatMessageResponse;
import com.an.chat.entity.ChatMessage;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage);

    ChatMessage toChatMessage(ChatMessageRequest request);

    List<ChatMessageResponse> toChatMessageResponses(List<ChatMessage> chatMessages);
}
