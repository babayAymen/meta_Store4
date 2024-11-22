package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "conversation_remote_keys_table")
data class ConversationRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val prevPage: Int?,
    val nextPage: Int?
)
