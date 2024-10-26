package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.User

data class CompanyWithUser(

@Embedded val company: Company,

@Relation(
    parentColumn = "userId",
    entityColumn = "id"
)
val user: User?

)
