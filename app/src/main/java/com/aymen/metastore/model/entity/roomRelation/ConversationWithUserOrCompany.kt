package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.Conversation
import com.aymen.metastore.model.entity.room.entity.User

data class ConversationWithUserOrCompany(
    @Embedded val conversation: Conversation,

    @Relation(
        parentColumn = "user1Id",
        entityColumn = "id"
    )
    val user1: User?,
    @Relation(
        parentColumn = "user2Id",
        entityColumn = "id"
    )
    val user2: User?,

    @Relation(
        parentColumn = "company1Id",
        entityColumn = "userId",
        entity = Company::class
    )
    val company1: CompanyWithUser?,
    @Relation(
        parentColumn = "company2Id",
        entityColumn = "userId",
        entity = Company::class
    )
    val company2: CompanyWithUser?,
){
    fun toConversation():com.aymen.metastore.model.entity.model.Conversation{
        return conversation.toConversation(
            user1 = user1?.toUser(),
            user2 = user2?.toUser(),
            company1 = company1?.toCompany(),
            company2 = company2?.toCompany()
        )
    }
}
