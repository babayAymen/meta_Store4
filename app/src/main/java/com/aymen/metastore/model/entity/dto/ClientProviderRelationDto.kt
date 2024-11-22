package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.ClientProviderRelation

data class ClientProviderRelationDto(
    val id : Long? = null,

    val person: UserDto? = null,

    val client: CompanyDto? = null,

    val provider: CompanyDto? = null,

    val mvt: Double? = null,

    val credit: Double? = null,

    val advance: Double? = null,

    val createdDate : String = "",

    val lastModifiedDate : String = "",
){
    fun toClientProviderRelation() : ClientProviderRelation {
        return ClientProviderRelation(
            id = id,
            userId = person?.id,
            clientId = client?.id,
            providerId = provider?.id,
            mvt = mvt,
            credit = credit,
            advance = advance,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
