package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.entity.api.UserDto

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
        username = userDto.username
        email = userDto.email
    }
}