package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.room.Message
import com.aymen.store.model.entity.dto.MessageDto

fun mapMessageToRoomMessage(message : MessageDto) : Message{
    return Message(
        id = message.id,

     createdBy = message.createdBy,

     conversationId = message.conversation?.id,

     content = message.content
    )
}