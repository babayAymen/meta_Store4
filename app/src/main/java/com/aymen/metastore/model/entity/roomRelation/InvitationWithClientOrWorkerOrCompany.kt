package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.Invitation
import com.aymen.metastore.model.entity.room.User
import com.aymen.metastore.model.entity.room.Worker

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
        entityColumn = "id"
    )
    val companySender : Company? = null,

    @Relation(
        parentColumn = "companyReceiverId",
        entityColumn = "id"
    )
    val companyReceiver : Company? = null,

)
