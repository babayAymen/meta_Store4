package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type

@Entity(tableName = "invitation",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["clientId"]),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["workerId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["companySenderId"]),
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["companyReceiverId"]),
    ])
data class Invitation(


    @PrimaryKey
    val id : Long? = null,

    val clientId : Long? = null, //user

    val workerId : Long? = null,

    val companySenderId : Long? = null,

    val companyReceiverId : Long? = null,

    val salary : Double? = null,

    val jobtitle : String? = null,

    val department : String? = null,

    val totdayvacation : Long? = null,

    val statusvacation : Boolean? = null,

    val status : Status? = null,

    val type : Type? = null
)
