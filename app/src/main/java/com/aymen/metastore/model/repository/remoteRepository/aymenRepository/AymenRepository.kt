package com.aymen.metastore.model.repository.remoteRepository.aymenRepository

interface AymenRepository {

    suspend fun makeAsPointSeller(status : Boolean, id : Long)
    suspend fun makeAsMetaSeller(status : Boolean, id : Long)
}