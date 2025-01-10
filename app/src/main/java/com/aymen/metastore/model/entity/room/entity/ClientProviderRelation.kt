package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User

@Entity(tableName = "client_provider_relation",
    foreignKeys = [
        ForeignKey(entity = com.aymen.metastore.model.entity.room.entity.User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = com.aymen.metastore.model.entity.room.entity.Company::class,
            parentColumns = ["companyId"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = com.aymen.metastore.model.entity.room.entity.Company::class,
            parentColumns = ["companyId"],
            childColumns = ["providerId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
    ],
            indices = [Index(value = ["userId"]),
                Index(value = ["clientId"]),
                Index(value = ["providerId"])])
data class ClientProviderRelation(

    @PrimaryKey val id : Long? = null,
    
    val userId: Long? = null,

    val clientId: Long? = null,

     val providerId: Long? = null,

    val mvt: Double? = null,

    val credit: Double? = null,

    val createdDate : String? = "",

    val lastModifiedDate : String? = "",
){
    fun toClientProviderRelation(person: User?,
                                 client : Company?,
                                 provider : Company?): com.aymen.metastore.model.entity.model.ClientProviderRelation{
        return com.aymen.metastore.model.entity.model.ClientProviderRelation(
            id = id,
            person = person,
            client = client,
            provider = provider,
            mvt = mvt,
            credit = credit,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
