package com.aymen.store.model.entity.dto

import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type

data class InvitationDto(

    val id : Long? = null,

    var client : UserDto? = null,

    var worker : UserDto? = null,

    var companySender : CompanyDto? = null,

    var companyReceiver : CompanyDto? = null,

    var salary : Double? = null,

    var jobtitle : String? = null,

    var department : String? = null,

    var totdayvacation : Long? = null,

    var statusvacation : Boolean? = null,

    var status : Status? = null,

    var type : Type? = null
)
