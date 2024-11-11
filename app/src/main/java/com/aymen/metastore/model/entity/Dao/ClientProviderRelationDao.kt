package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.ClientProviderRelation
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.model.entity.roomRelation.CompanyWithUserClient

@Dao
interface ClientProviderRelationDao {

    @Upsert
    suspend fun insertClientProviderRelation(relation : ClientProviderRelation)

    @Query("select * from client_provider_relation")
    suspend fun getAllRelations(): List<ClientProviderRelation>

    @Transaction
    @Query("SELECT * FROM client_provider_relation AS r JOIN company c ON clientId = c.id WHERE providerId = :myCompanyId AND (c.name LIKE '%' || :search || '%' OR c.code LIKE '%' || :search || '%')")
    suspend fun getAllClientsCompanyContaining(search : String, myCompanyId : Long): List<CompanyWithCompanyClient>

    @Transaction
    @Query("SELECT * FROM client_provider_relation AS r JOIN user u ON personId = u.id WHERE providerId = :myCompanyId AND u.username LIKE '%' || :search || '%'")
    suspend fun getAllClientsUserContaining(search : String, myCompanyId : Long): List<CompanyWithCompanyClient>

    @Transaction
    @Query("SELECT * FROM client_provider_relation WHERE providerId = :myCompanyId ")
    suspend fun getAllClientsByProviderId(myCompanyId: Long): List<CompanyWithCompanyClient>

    @Transaction
    @Query("SELECT * FROM client_provider_relation WHERE clientId = :myCompanyId")
    suspend fun getAllProvidersByClientId(myCompanyId: Long): List<CompanyWithCompanyClient>
}