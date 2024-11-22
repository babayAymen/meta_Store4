package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.Message


data class MessageDto(

    var id : Long? = null,
    var createdBy : Long? = null,
    var createdDate : String? = null,
    var conversation : ConversationDto? = null,
    var content : String? = ""
){
    fun toMessage() : Message {

        return Message(
            id = id,
            createdBy = createdBy,
            createdDate = createdDate,
            conversationId = conversation?.id,
            content = content
        )
    }
}
