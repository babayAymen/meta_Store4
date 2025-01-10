package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.room.entity.ClientProviderRelation

data class ClientProviderRelation(
    val id : Long? = null,
    val person: User? = null,
    val client: Company? = null,
    val provider: Company? = null,
    val mvt: Double? = null,
    val credit: Double? = null,
    val createdDate : String? = "",
    val lastModifiedDate : String? = "",
){
    fun toClientProviderRelationEntity() : ClientProviderRelation{
        return ClientProviderRelation(
            id,
            userId = person?.id,
            clientId = client?.id,
            providerId = provider?.id,
            mvt,
            credit,
            createdDate,
            lastModifiedDate
        )
    }
}