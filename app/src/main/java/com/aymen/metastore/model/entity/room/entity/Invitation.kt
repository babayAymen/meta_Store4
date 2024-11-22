package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.Invitation
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type

@Entity(tableName = "invitation",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(
            entity = Worker::class,
            parentColumns = ["id"],
            childColumns = ["workerId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["companySenderId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["companyReceiverId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
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
){
    fun toInvitation(client : com.aymen.metastore.model.entity.model.User?,
                     worker : com.aymen.metastore.model.entity.model.Worker?,
                     companySender : com.aymen.metastore.model.entity.model.Company?,
                     companyReceiver : com.aymen.metastore.model.entity.model.Company?):Invitation{
        return Invitation(
            id,
            client = client,
            worker = worker,
            companySender = companySender,
            companyReceiver = companyReceiver,
            salary,
            jobtitle,
            department,
            totdayvacation,
            statusvacation,
            status,
            type
        )

    }
}
