package com.aymen.metastore.model.entity.converterRealmToApi

import android.util.Log
import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.entity.dto.UserDto

fun mapUserToUserDto(user: User): UserDto{
    return UserDto(
        id = user.id,
        username = user.username,
        email = user.email,
    )
}

fun mapUserDtoToUserRealm(userDto : UserDto):User{
    return User().apply {
        id = userDto.id
        username = userDto.username!!
        email = userDto.email
    }
}

fun mapUserToRoomUser(user : UserDto?): com.aymen.metastore.model.entity.room.User{
        return com.aymen.metastore.model.entity.room.User(
            id = user?.id,
            phone = user?.phone,
            address = user?.address,
            username = user?.username,
            email = user?.email,
            longitude = user?.longitude,
            latitude = user?.latitude,
            role = user?.role,
            balance = user?.balance,
            image = user?.image
        )

}