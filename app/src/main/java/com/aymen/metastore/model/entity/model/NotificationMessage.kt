package com.aymen.metastore.model.entity.model

data class NotificationMessage(

    val token : String? = null,
    var title : String? = null,
    var body : String? = null,
    val image : String? = null,
    val data : Map<String , String>? = null,
    var balnce : Double? = null
)
