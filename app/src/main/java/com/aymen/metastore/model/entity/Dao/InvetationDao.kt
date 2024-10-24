package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Invitation

@Dao
interface InvetationDao {

    @Upsert
    suspend fun insertInvitation(invitation: Invitation)

    @Query("SELECT * FROM invitation")
    suspend fun getAllInvitations():List<Invitation>
}