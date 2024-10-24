package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.Enum.MessageType
import com.aymen.store.model.entity.dto.ConversationDto
import com.aymen.store.model.entity.realm.Conversation


fun mapConversationToConversationDto(conversation: Conversation): ConversationDto {
    return ConversationDto(
        id = conversation.id,
        user1 = conversation.user1?.let { mapUserToUserDto(it) },
        user2 = conversation.user2?.let { mapUserToUserDto(it) },
        company1 = conversation.company1?.let { mapCompanyToCompanyDto(it) },
        company2 = conversation.company2?.let { mapCompanyToCompanyDto(it) },
        message = conversation.message,
        type = conversation.type?.let { MessageType.valueOf(it) }
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