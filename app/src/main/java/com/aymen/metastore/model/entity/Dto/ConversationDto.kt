package com.aymen.store.model.entity.dto

import com.aymen.metastore.model.Enum.MessageType

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
    )
