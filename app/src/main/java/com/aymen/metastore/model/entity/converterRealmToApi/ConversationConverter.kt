package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.ConversationDto
import com.aymen.store.model.entity.dto.UserDto


fun mapRoomConversationToConversationDto(conversation: com.aymen.metastore.model.entity.room.Conversation): ConversationDto {
    return ConversationDto(
        id = conversation.id,
        user1 = conversation.user1Id?.let { UserDto(id = it) },
        user2 = conversation.user2Id?.let { UserDto(id = it) },
        company1 = conversation.company1Id?.let { CompanyDto(id = it) },
        company2 = conversation.company2Id?.let { CompanyDto(id = it) },
        message = conversation.message,
        type = conversation.type
    )
}

fun mapConversationToRoomConversation(conversation : ConversationDto) : com.aymen.metastore.model.entity.room.Conversation{
    return com.aymen.metastore.model.entity.room.Conversation(
        id = conversation.id,
        user1Id = conversation.user1?.id,
        user2Id = conversation.user2?.id,
        company1Id = conversation.company1?.id,
        company2Id = conversation.company2?.id,
        message = conversation.message,
        type = conversation.type,
        lastMessage = conversation.lastMessage
    )
}