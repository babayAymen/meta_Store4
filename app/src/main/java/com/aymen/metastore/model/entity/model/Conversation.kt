package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.Enum.MessageType
data class Conversation(

    var id : Long? = null,
    var user1 : User? = null,
    var user2 : User? = null,
    var company1 : Company? = null,
    var company2 : Company? = null,
    var message : String? = "",
    var type : MessageType? = null,
    var createdDate : String = "",
    var lastModifiedDate : String = "",
    val lastMessage : String = ""
)