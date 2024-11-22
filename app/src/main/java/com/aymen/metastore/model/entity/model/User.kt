package com.aymen.metastore.model.entity.model

import com.aymen.store.model.Enum.RoleEnum

data class User (

    var id : Long? = null,

    val phone : String? = null,
    val address : String? = null,
    val username : String? = null,
    val email : String? = "",
    val resettoken : String? = "",
    val longitude : Double? = 0.0,
    val latitude : Double? = 0.0,
    val role : RoleEnum? = RoleEnum.USER,
    val balance : Double? = 0.0,
    val image : String? = "",
    val rate: Double? = 0.0,
    val rater: Int? = 0
)