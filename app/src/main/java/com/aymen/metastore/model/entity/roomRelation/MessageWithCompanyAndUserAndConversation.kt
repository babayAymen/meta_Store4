package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Conversation
import com.aymen.metastore.model.entity.room.entity.Message

data class MessageWithCompanyAndUserAndConversation (
    @Embedded val message : Message,

    @Relation(
        entity = Conversation::class,
        parentColumn = "conversationId",
        entityColumn = "id"
    )
    val conversation : ConversationWithUserOrCompany,
){
    fun toMessage(): com.aymen.metastore.model.entity.model.Message{
        return message.toMessage(
            conversation = conversation.toConversation()
        )
    }
}