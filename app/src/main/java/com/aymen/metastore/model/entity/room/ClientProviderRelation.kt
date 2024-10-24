package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "client_provider_relation")
data class ClientProviderRelation(

    @PrimaryKey val id : Long? = null,
    
    val personId: Long? = null,

    val clientId: Long? = null,

    val providerId: Long? = null,

    val mvt: Double? = null,

    val credit: Double? = null,

    val advance: Double? = null,

    val createdDate : String = "",

    val lastModifiedDate : String = "",
)
