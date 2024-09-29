package com.aymen.store.model.entity.api

import com.aymen.store.model.Enum.RoleEnum

data class UserDto(

    var id : Long? = null,

    var phone : String = "",

    var address : String = "",

    var username : String = "",

    var email : String? = "",

    var password : String = "",

    var resettoken : String = "",

  //  var datetoken : Date = Date(),

    var longitude : Double = 0.0,

    var latitude : Double = 0.0,

    var role : RoleEnum = RoleEnum.USER,

    var balance : Double? = 0.0,

    var image : String? = ""
)
