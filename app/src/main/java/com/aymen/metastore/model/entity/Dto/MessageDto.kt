package com.aymen.store.model.entity.dto

data class MessageDto(


//    var senderUser : UserDto? = null,
//
//    var receiverUser : UserDto? = null,
//
//    var senderCompany : CompanyDto? = null,
//
//    var receiverCompany: CompanyDto? = null,
    var id : Long? = null,
    var createdBy : Long? = null,

    var conversation : ConversationDto? = null,

    var content : String? = ""
)
