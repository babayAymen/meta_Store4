package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.User
import com.aymen.metastore.model.entity.room.entity.Worker

data class WorkerWithUser(
    @Embedded val worker : Worker,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user : User? = null,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val company : CompanyWithUser
){
    fun toWorkerWithUser(): com.aymen.metastore.model.entity.model.Worker{
        return worker.toWorker(
            user = user?.toUser()!!,
            company = company.toCompany()
        )
    }
}
