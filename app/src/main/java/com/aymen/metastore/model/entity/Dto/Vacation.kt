package com.aymen.store.model.entity.dto

import java.util.Date

data class Vacation(

    val id : Long? = null,

    var year : Int,

    var startdate : Date,

    var enddate : Date,

    var worker : WorkerDto,

    var company : CompanyDto
)
