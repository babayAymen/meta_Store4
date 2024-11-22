package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.metastore.model.entity.model.Conversation

@Entity(tableName = "conversation",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["user1Id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user2Id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["company1Id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["company2Id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
    ])
data class Conversation (

    @PrimaryKey val id : Long? = null,
    val user1Id : Long? = null,
    val user2Id : Long? = null,
    val company1Id : Long? = null,
    val company2Id : Long? = null,
    val message : String? = "",
    val type : MessageType? = MessageType.USER_SEND_COMPANY,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
    val lastMessage : String = ""
){
    fun toConversation(user1 : com.aymen.metastore.model.entity.model.User?,
                       user2 : com.aymen.metastore.model.entity.model.User?,
                       company1 : com.aymen.metastore.model.entity.model.Company?,
                       company2 : com.aymen.metastore.model.entity.model.Company?) : Conversation{
        return Conversation(
            id = id,
            user1 = user1,
            user2 = user2,
            company1 = company1,
            company2 = company2,
            message = message,
            type = type,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            lastMessage = lastMessage
        )
    }
}