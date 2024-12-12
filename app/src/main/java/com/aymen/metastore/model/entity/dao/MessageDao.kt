package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Message
import com.aymen.metastore.model.entity.room.remoteKeys.MessageRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.MessageWithCompanyAndUserAndConversation

@Dao
interface MessageDao {

    @Upsert
    suspend fun insertMessage(message : List<Message>)

    @Upsert
    fun insertRemoteKeys(remoteKeys : List<MessageRemoteKeysEntity>)

    @Query("SELECT * FROM message_remote_keys_table WHERE id = :id")
    suspend fun getMessageRemoteKey(id : Long) : MessageRemoteKeysEntity

    @Query("DELETE FROM message_remote_keys_table")
    fun clearAllMessageRemoteKeys()

    @Query("DELETE FROM message")
    suspend fun clearAllMessages()

    @Query("SELECT * FROM message WHERE conversationId = :id")
     fun getAllMessagesByConversationId(id : Long) : PagingSource<Int,MessageWithCompanyAndUserAndConversation>
}