package com.an.chat.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.an.chat.dto.response.ConversationResponse;
import com.an.chat.entity.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationResponse toConversationResponse(Conversation conversation);

    List<ConversationResponse> toConversationResponseList(List<Conversation> conversations);
}
