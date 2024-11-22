package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "worker",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ])
data class Worker(

    @PrimaryKey
    val id : Long ? = null,
    val name: String,
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
){
    fun toWorker(user : com.aymen.metastore.model.entity.model.User,
                 company : com.aymen.metastore.model.entity.model.Company):com.aymen.metastore.model.entity.model.Worker{
        return com.aymen.metastore.model.entity.model.Worker(
            id = id,
            name = name,
            phone = phone,
            email = email,
            address = address,
            salary = salary,
            jobtitle = jobtitle,
            department = department,
            totdayvacation = totdayvacation,
            remainingday = remainingday,
            statusvacation = statusvacation,
            user = user,
            company = company,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )

    }
}
