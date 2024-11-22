package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.Worker

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
){
    fun toWorker() : Worker {
        return Worker(
        id = id,
        name = name,
        phone = phone,
        email = email,
        address = address,
        salary = salary,
        jobtitle = jobtitle,
        department = department,
        totdayvacation = totdayvacation,
        statusvacation = statusvacation,
        userId = user.id,
        companyId = company.id,
        createdDate = createdDate,
        lastModifiedDate = lastModifiedDate,
        remainingday = remainingday
        )
    }
}
