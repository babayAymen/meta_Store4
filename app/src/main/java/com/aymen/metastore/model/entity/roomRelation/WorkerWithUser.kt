package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.User
import com.aymen.metastore.model.entity.room.Worker

data class WorkerWithUser(
    @Embedded val worker : Worker,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user : User? = null
)
