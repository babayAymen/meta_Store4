package com.aymen.store.model.entity.api

import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type

data class Invetation(

    val id : Long? = null,

    var client : UserDto,

    var worker : UserDto,

    var companySender : CompanyDto,

    var companyReceiver : CompanyDto,

    var salary : Double,

    var jobtitle : String,

    var department : String,

    var totdayvacation : Long,

    var statusvacation : Boolean,

    var status : Status,

    var type : Type
)
