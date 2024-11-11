package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Invitation
import com.aymen.metastore.model.entity.roomRelation.InvitationWithClientOrWorkerOrCompany
import com.aymen.store.model.Enum.Status

@Dao
interface InvetationDao {

    @Upsert
    suspend fun insertInvitation(invitation: Invitation)

    @Query("SELECT * FROM invitation")
    suspend fun getAllInvitations():List<InvitationWithClientOrWorkerOrCompany>

    @Query("UPDATE invitation SET status = :status WHERE id = :id")
    suspend fun requestResponse(status: Status , id : Long)
}