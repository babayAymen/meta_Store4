package com.aymen.store.model.entity.api

import java.util.Date

data class Vacation(

    val id : Long? = null,

    var year : Int,

    var startdate : Date,

    var enddate : Date,

    var worker : Worker,

    var company : CompanyDto
)
