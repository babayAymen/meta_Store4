package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "worker",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.SET_NULL
        )
    ])
data class Worker(

    @PrimaryKey
    val id : Long ? = null,
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val salary: Double? = null,
    val jobtitle: String? = null,
    val department: String? = null,
    val totdayvacation: Long? = 0,
    val remainingday: Long? = 0,
    val statusvacation : Boolean? = null,
    val userId: Long? = null,
    val companyId: Long? = null,
    val createdDate : String = "",
    val lastModifiedDate : String = ""
)
