package com.aymen.metastore.model.entity.roomRelation

import android.util.Log
import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.User

data class CompanyWithUser(

    @Embedded val company : Company,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user : User?

    ){
    fun toCompany(): com.aymen.metastore.model.entity.model.Company {
        return company.toCompany(user = user?.toUser(),parent = null)
    }
}
