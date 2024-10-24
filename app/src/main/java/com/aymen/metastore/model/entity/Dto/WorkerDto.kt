package com.aymen.store.model.entity.dto

data class WorkerDto(

    val id : Long? = null,

    var name : String,

    var phone : String,

    var email : String,

    var address : String,

    var salary : Double,

    var jobtitle : String,

    var department : String,

    var totdayvacation : Long,

    var statusvacation : Boolean,

    var user : UserDto,

    var company : CompanyDto,

    var createdDate: String = "",
    var lastModifiedDate: String = "",
    var remainingday : Long? = 0
)
