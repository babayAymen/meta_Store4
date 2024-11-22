package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.Enum.MessageType
import com.aymen.metastore.model.entity.room.entity.Conversation

data class ConversationDto(

    var id : Long? = null,
    var user1 : UserDto? = null,
    var user2 : UserDto? = null,
    var company1 : CompanyDto? = null,
    var company2 : CompanyDto? = null,
    var message : String? = "",
    var type : MessageType? = null,
    var createdDate : String = "",
    var lastModifiedDate : String = "",
    val lastMessage : String = ""
    ){
    fun toConversation() : Conversation {

        return Conversation(
            id = id,
            user1Id = user1?.id,
            user2Id = user2?.id,
            company1Id = company1?.id,
            company2Id = company2?.id,
            message = message,
            type = type,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            lastMessage = lastMessage

        )
    }
}
