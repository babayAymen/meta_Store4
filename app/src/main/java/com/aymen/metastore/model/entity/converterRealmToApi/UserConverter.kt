package com.aymen.metastore.model.entity.converterRealmToApi

import android.util.Log
import com.aymen.store.model.entity.dto.UserDto


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
fun mapRoomUserToUserDto(user : com.aymen.metastore.model.entity.room.User): UserDto{
        return UserDto(
            id = user.id,
            phone = user.phone,
            address = user.address,
            username = user.username,
            email = user.email,
            longitude = user.longitude,
            latitude = user.latitude,
            balance = user.balance,
            image = user.image
        )

}