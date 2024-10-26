package com.aymen.metastore.dependencyInjection

import androidx.datastore.core.Serializer
import com.aymen.store.model.entity.dto.UserDto
import com.aymen.store.model.entity.realm.Company
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object UserDtoSerializer : Serializer<UserDto> {
    override val defaultValue: UserDto
        get() = UserDto()

    override suspend fun readFrom(input: InputStream): UserDto {
        return Gson().fromJson(input.readBytes().decodeToString(), UserDto::class.java)
    }

    override suspend fun writeTo(t: UserDto, output: OutputStream) {
        val jsonToken = Gson().toJson(t)
        withContext(Dispatchers.IO){
            output.write(jsonToken.toByteArray())
        }
    }
}