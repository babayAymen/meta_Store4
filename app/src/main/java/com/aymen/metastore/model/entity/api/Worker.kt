package com.aymen.store.model.entity.api

data class Worker(

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

    var company : CompanyDto
)
