package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "message",
    foreignKeys = [
        ForeignKey(entity = Conversation::class, parentColumns = ["id"], childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)
    ])
data class Message(

    @PrimaryKey
    val id : Long? = null,
    val createdBy : Long? = null,
    val createdDate : String? = null,
    val conversationId : Long? = null,
    val content : String? = ""
){
    fun toMessage(conversation : com.aymen.metastore.model.entity.model.Conversation): com.aymen.metastore.model.entity.model.Message {
        return com.aymen.metastore.model.entity.model.Message(
            id = id,
            createdBy = createdBy,
            createdDate = createdDate,
            conversation = conversation,
            content = content
        )
    }
}
