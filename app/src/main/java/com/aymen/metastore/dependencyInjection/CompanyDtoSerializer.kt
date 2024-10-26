package com.aymen.metastore.dependencyInjection

import androidx.datastore.core.Serializer
import com.aymen.store.model.entity.dto.CompanyDto
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object CompanyDtoSerializer : Serializer<CompanyDto> {
    override val defaultValue: CompanyDto
        get() = CompanyDto()

    override suspend fun readFrom(input: InputStream): CompanyDto {
        return Gson().fromJson(input.readBytes().decodeToString(), CompanyDto::class.java)
    }

    override suspend fun writeTo(t: CompanyDto, output: OutputStream) {
        val jsonToken = Gson().toJson(t)
        withContext(Dispatchers.IO){
            output.write(jsonToken.toByteArray())
        }
    }
}