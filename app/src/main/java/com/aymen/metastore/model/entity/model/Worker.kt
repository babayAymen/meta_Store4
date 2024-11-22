package com.aymen.metastore.model.entity.model

data class Worker (

    val id : Long? = null,
    var name : String,
    var phone : String? = null,
    var email : String? = null,
    var address : String? = null,
    var salary : Double? = null,
    var jobtitle : String? = null,
    var department : String? = null,
    var totdayvacation : Long? = null,
    var statusvacation : Boolean? = false,
    var user : User,
    var company : Company,
    var createdDate: String = "",
    var lastModifiedDate: String = "",
    var remainingday : Long? = 0
)