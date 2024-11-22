package com.aymen.metastore.model.entity.model

data class Message(

    var id : Long? = null,
    var createdBy : Long? = null,
    var createdDate : String? = null,
    var conversation : Conversation? = null,
    var content : String? = ""
)