package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Message

@Dao
interface MessageDao {

    @Upsert
    suspend fun insertMessage(message : Message)

    @Query("SELECT * FROM message WHERE conversationId = :id")
    suspend fun getAllMessagesById(id : Long) : List<Message>
}