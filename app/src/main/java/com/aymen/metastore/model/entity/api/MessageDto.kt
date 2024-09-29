package com.aymen.store.model.entity.api

data class MessageDto(

    var id : Long? = null,

//    var senderUser : UserDto? = null,
//
//    var receiverUser : UserDto? = null,
//
//    var senderCompany : CompanyDto? = null,
//
//    var receiverCompany: CompanyDto? = null,
    var createdBy : Long? = null,

    var conversation : ConversationDto? = null,

    var content : String? = ""
)
