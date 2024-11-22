package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.Invitation
import com.aymen.metastore.model.entity.room.entity.User
import com.aymen.metastore.model.entity.room.entity.Worker

data class InvitationWithClientOrWorkerOrCompany(
    @Embedded val invitation: Invitation,

    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client : User? = null,

    @Relation(
        parentColumn = "workerId",
        entityColumn = "id",
        entity = Worker::class
    )
    val workerWithUser: WorkerWithUser? = null,

    @Relation(
        parentColumn = "companySenderId",
        entityColumn = "userId",
        entity = Company::class
    )
    val companySender : CompanyWithUser? = null,

    @Relation(
        parentColumn = "companyReceiverId",
        entityColumn = "userId",
        entity = Company::class
    )
    val companyReceiver : CompanyWithUser? = null,

    ){
    fun toInvitationWithClientOrWorkerOrCompany():com.aymen.metastore.model.entity.model.Invitation{
        return invitation.toInvitation(
            client = client?.toUser(),
            worker = workerWithUser?.toWorkerWithUser(),
            companySender = companySender?.toCompany(),
            companyReceiver = companyReceiver?.toCompany()
        )
    }
}
