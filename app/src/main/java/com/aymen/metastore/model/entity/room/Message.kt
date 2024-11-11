package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
@Entity(tableName = "message",
    foreignKeys = [
        ForeignKey(entity = Conversation::class, parentColumns = ["id"], childColumns = ["conversationId"])
    ])
data class Message(

    @PrimaryKey
    val id : Long? = null,

    val createdBy : Long? = null,

    val createdDate : String? = null,

    val conversationId : Long? = null,

    val content : String? = ""
)
