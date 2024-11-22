package com.aymen.store.dependencyInjection

import androidx.datastore.core.Serializer
import com.aymen.metastore.model.entity.model.Company
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object CompanySerializer : Serializer<Company> {
    override val defaultValue: Company
        get() = Company()

    override suspend fun readFrom(input: InputStream): Company {
        return Gson().fromJson(input.readBytes().decodeToString(), Company::class.java)
    }

    override suspend fun writeTo(t: Company, output: OutputStream) {
        val jsonToken = Gson().toJson(t)
        withContext(Dispatchers.IO){
            output.write(jsonToken.toByteArray())
        }
    }
}