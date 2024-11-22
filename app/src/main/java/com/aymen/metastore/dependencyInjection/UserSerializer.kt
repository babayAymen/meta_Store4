package com.aymen.store.dependencyInjection

import androidx.datastore.core.Serializer
import com.aymen.metastore.model.entity.model.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object UserSerializer : Serializer<User> {
    override val defaultValue: User
        get() = User()

    override suspend fun readFrom(input: InputStream): User {
        return Gson().fromJson(input.readBytes().decodeToString(), User::class.java)
    }

    override suspend fun writeTo(t: User, output: OutputStream) {
        val jsonToken = Gson().toJson(t)
        withContext(Dispatchers.IO){
            output.write(jsonToken.toByteArray())
        }
    }
}