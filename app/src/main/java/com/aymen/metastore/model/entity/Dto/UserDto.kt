package com.aymen.store.model.entity.dto

import com.aymen.store.model.Enum.RoleEnum

data class UserDto(

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
    val image : String? = ""
)
