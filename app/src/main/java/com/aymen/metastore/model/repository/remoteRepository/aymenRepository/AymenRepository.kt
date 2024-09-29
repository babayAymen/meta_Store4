package com.aymen.metastore.model.repository.remoteRepository.aymenRepository

interface AymenRepository {

    suspend fun makeAsPointSeller(status : Boolean, id : Long)
}