package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.UserDto
import com.aymen.metastore.model.entity.room.entity.User
import com.aymen.store.model.Enum.AccountType
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
    val image : String? = null,
    val rate: Double? = 0.0,
    val rater: Int? = 0,
    val accountType: AccountType? = AccountType.NULL
){
    fun toUserDto(): UserDto{
        return UserDto(
            id,
            phone,
            address,
            username,
            email,
            resettoken,
            longitude,
            latitude,
            role,
            balance,
            image,
            rate,
            rater,
            accountType
        )
    }
    fun toUserEntity(): User{
        return User(
            id,
            phone,
            address,
            username,
            email,
            resettoken,
            longitude,
            latitude,
            role,
            balance,
            image,
            rate,
            rater,
            accountType
        )
    }
}