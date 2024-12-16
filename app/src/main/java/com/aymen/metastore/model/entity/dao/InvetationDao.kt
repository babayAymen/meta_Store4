package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Invitation
import com.aymen.metastore.model.entity.room.remoteKeys.InvitationRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InvitationWithClientOrWorkerOrCompany
import com.aymen.store.model.Enum.Status

@Dao
interface InvetationDao {

    @Upsert
    suspend fun insertInvitation(invitation: List<Invitation>)

    @Upsert
    suspend fun insertInvitationKeys(keys : List<InvitationRemoteKeysEntity>)

    @Query("SELECT * FROM inventory_remote_keys_entity WHERE id = :id")
    suspend fun getInvitationRemoteKey(id : Long) : InvitationRemoteKeysEntity

    @Query("DELETE FROM inventory_remote_keys_entity")
    suspend fun clearAllRemoteKeysTables()

    @Query("DELETE FROM inventory")
    suspend fun clearAllInvitationTables()

    @Query("SELECT * FROM invitation")
     fun getAllInvitations():PagingSource<Int,InvitationWithClientOrWorkerOrCompany>

    @Query("UPDATE invitation SET status = :status WHERE id = :id")
    suspend fun requestResponse(status: Status , id : Long)
}