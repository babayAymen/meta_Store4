package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Conversation
import com.aymen.metastore.model.entity.room.remoteKeys.ConversationRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.ConversationWithUserOrCompany
import retrofit2.http.GET

@Dao
interface ConversationDao {

    @Upsert
    suspend fun insertConversation(conversation : List<Conversation>)

    @Upsert
    fun insertKeys(conversation : List<ConversationRemoteKeysEntity>)

    @Query("SELECT * FROM conversation_remote_keys_table WHERE id = :id")
    suspend fun getConversationRemoteKey(id : Long) : ConversationRemoteKeysEntity

    @Query("DELETE FROM conversation_remote_keys_table")
    suspend fun clearAllConversationRemoteKeysTable()

    @Query("DELETE FROM conversation")
    suspend fun clearAllConversationTable()

    @Query("SELECT * FROM conversation")
    fun getAllConversation() : PagingSource<Int, ConversationWithUserOrCompany>

    @Query("SELECT * FROM conversation")
    suspend fun getAllConversations() : List<Conversation>

    @Query("UPDATE conversation SET lastMessage = :lastMessage WHERE id = :conversationId")
    suspend fun updateLastMessage(conversationId : Long , lastMessage : String)
}