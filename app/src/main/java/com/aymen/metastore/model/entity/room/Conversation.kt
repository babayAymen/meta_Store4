package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.Enum.MessageType
@Entity(tableName = "conversation",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["user1Id"]),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user2Id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["company1Id"]),
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["company2Id"]),
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
)