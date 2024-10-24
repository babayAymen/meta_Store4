package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Conversation

@Dao
interface ConversationDao {

    @Upsert
    suspend fun insertConversation(conversation : Conversation)

    @Query("SELECT * FROM conversation")
    suspend fun getAllConversations() : List<Conversation>

    @Query("UPDATE conversation SET lastMessage = :lastMessage WHERE id = :conversationId")
    suspend fun updateLastMessage(conversationId : Long , lastMessage : String)
}