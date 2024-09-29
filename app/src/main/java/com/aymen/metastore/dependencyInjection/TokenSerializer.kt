package com.aymen.store.dependencyInjection

import androidx.datastore.core.Serializer
import com.aymen.store.model.entity.api.AuthenticationResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream


object TokenSerializer : Serializer<AuthenticationResponse> {
    override val defaultValue: AuthenticationResponse
        get() = AuthenticationResponse()


    override suspend fun readFrom(input: InputStream): AuthenticationResponse {
        return Gson().fromJson(input.readBytes().decodeToString(), AuthenticationResponse::class.java)
    }

    override suspend fun writeTo(t: AuthenticationResponse, output: OutputStream) {
        val jsonToken = Gson().toJson(t)
        withContext(Dispatchers.IO){
            output.write(jsonToken.toByteArray())
        }
    }
}