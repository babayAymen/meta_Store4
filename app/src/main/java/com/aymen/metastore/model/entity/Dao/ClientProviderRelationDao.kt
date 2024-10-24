package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.ClientProviderRelation
@Dao
interface ClientProviderRelationDao {

    @Upsert
    suspend fun insertClientProviderRelation(relation : ClientProviderRelation)

    @Query("select * from client_provider_relation")
    suspend fun getAllRelations(): List<ClientProviderRelation>


}