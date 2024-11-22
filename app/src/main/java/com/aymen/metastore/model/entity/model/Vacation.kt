package com.aymen.metastore.model.entity.model

import java.util.Date

data class Vacation (

    val id : Long? = null,
    var year : Int,
    var startdate : String? = null,
    var enddate : String? = null,
    var worker : Worker,
    var company : Company? = null,
    var createdDate : String = "",
    var lastModifiedDate : String = ""

)