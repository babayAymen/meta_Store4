package com.aymen.metastore.model.entity.model

import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type

data class Invitation (

    val id : Long? = null,
    var client : User? = null,
    var worker : Worker? = null,
    var companySender : Company? = null,
    var companyReceiver : Company? = null,
    var salary : Double? = null,
    var jobtitle : String? = null,
    var department : String? = null,
    var totdayvacation : Long? = null,
    var statusvacation : Boolean? = null,
    var status : Status? = null,
    var type : Type? = null
)