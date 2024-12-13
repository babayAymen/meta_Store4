package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.ClientProviderRelation
import com.aymen.metastore.model.entity.room.remoteKeys.ClientProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ClientRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyOrUser

@Dao
interface ClientProviderRelationDao {

    @Upsert
    suspend fun insertClientProviderRelation(relation : List<ClientProviderRelation>)


    @Upsert
    suspend fun insertClientKeys(keys : List<ClientRemoteKeysEntity>)
    @Upsert
    suspend fun insertProviderKeys(keys : List<ProviderRemoteKeysEntity>)

    @Query("SELECT * FROM client_remote_keys WHERE id = :id")
    suspend fun getClientRemoteKey(id : Long): ClientRemoteKeysEntity
    @Query("DELETE FROM client_remote_keys")
    suspend fun clearClientRemoteKeysTable()
    @Query("DELETE FROM client_provider_relation WHERE providerId = :id")
    suspend fun clearAllClientTable(id : Long)
    @Query("SELECT * FROM provider_remote_keys WHERE id = :id")
    suspend fun getProviderRemoteKey(id : Long): ProviderRemoteKeysEntity
    @Query("DELETE FROM provider_remote_keys")
    suspend fun clearProviderRemoteKeysTable()
    @Query("DELETE FROM client_provider_relation WHERE (clientId = :id OR userId = :id)")
    suspend fun clearAllProviderTable(id : Long)


    @Transaction
    @Query(
        "SELECT c.*, r.*, u.* " +
            "    FROM client_provider_relation AS r " +
            "    LEFT JOIN user AS u ON r.userId = u.id " +
            "    LEFT JOIN company AS c ON r.clientId = c.companyId WHERE r.providerId = :id AND" +
            " ((u.username LIKE '%' || :clientName || '%') OR (c.name LIKE '%' || :clientName || '%') OR (c.code LIKE '%' || :clientName || '%'))"
    )
    fun getAllMyClientsContainig(id : Long, clientName : String ) :PagingSource<Int,CompanyWithCompanyClient>

    @Transaction
    @Query("SELECT * FROM client_provider_relation WHERE providerId  = :myCompanyId")
    fun getAllMyClients(myCompanyId : Long) : PagingSource<Int,CompanyWithCompanyOrUser>
    @Transaction
    @Query("SELECT * FROM client_provider_relation WHERE providerId  = :myCompanyId")
    fun getAllClients(myCompanyId : Long) : List<CompanyWithCompanyOrUser>

    @Transaction
    @Query("SELECT * FROM client_provider_relation WHERE clientId = :myCompanyId")
    fun getAllMyClient(myCompanyId : Long) : List<CompanyWithCompanyClient>


    @Transaction
    @Query("SELECT * FROM client_provider_relation WHERE (clientId = :id)") // without user id
    fun getAllMyProviders(id : Long) : PagingSource<Int,CompanyWithCompanyOrUser>

    @Upsert
    fun insertKeys(keys : List<ClientProviderRemoteKeysEntity>)

    @Query("SELECT * FROM client_provider_remote_keys_table WHERE id = :id")
    suspend fun getRelationRemoteKey(id : Long): ClientProviderRemoteKeysEntity

    @Query("DELETE FROM client_provider_remote_keys_table")
    fun clearAllRemoteKeysTable()

    @Query("DELETE FROM client_provider_relation")
    suspend fun clearAllRelationTable()
   @Transaction
    @Query("SELECT * FROM client_provider_relation WHERE createdDate = :search ")
     fun getAllUserContaining(search: String) : PagingSource<Int,CompanyWithCompanyClient>

     @Query("SELECT MAX(id) FROM client_provider_relation WHERE providerId = :id")
     suspend fun getLatestClientId(id : Long) : Long?

     @Query("SELECT MAX(id) FROM client_provider_relation WHERE clientId = :id")
     suspend fun getLatestProviderId(id : Long) : Long?

     @Query("SELECT COUNT(*) FROM client_provider_relation WHERE providerId = :id")
     suspend fun getClientCount(id : Long) : Int

     @Query("SELECT COUNT(*) FROM client_provider_relation WHERE clientId = :id")
     suspend fun getProviderCount(id : Long) : Int

     @Upsert
     suspend fun insertSingleClientProviderRelation(relation : ClientProviderRelation)

     @Upsert
     suspend fun insertSingleClientRemoteKey(key : ClientRemoteKeysEntity)

     @Query("DELETE FROM client_provider_relation WHERE id = :id")
     suspend fun deleteClientProviderRelationById(id  :Long)

     @Query("DELETE FROM client_remote_keys WHERE id = :id")
     suspend fun deleteClientRelationRemoteKey(id : Long)

     @Query("DELETE FROM provider_remote_keys WHERE id = :id")
     suspend fun deleteProviderRelationRemoteKey(id : Long)

     @Upsert
     suspend fun insertSingleProviderRemoteKey(key : ProviderRemoteKeysEntity)
}