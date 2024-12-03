package com.aymen.metastore.dependencyInjection

import androidx.datastore.core.Serializer
import com.aymen.store.model.Enum.AccountType
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object AccountTypeDtoSerializer : Serializer<AccountType>{
    override val defaultValue: AccountType
        get() = AccountType.NULL

    override suspend fun readFrom(input: InputStream): AccountType {
        return Gson().fromJson(input.readBytes().decodeToString(), AccountType::class.java)
    }

    override suspend fun writeTo(t: AccountType, output: OutputStream) {
        val jsonToken = Gson().toJson(t)
        withContext(Dispatchers.IO){
            output.write(jsonToken.toByteArray())
        }
    }
}