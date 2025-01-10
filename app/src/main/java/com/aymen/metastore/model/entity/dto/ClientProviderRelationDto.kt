package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.ClientProviderRelation

data class ClientProviderRelationDto(
    val id : Long? = null,

    val person: UserDto? = null,

    val client: CompanyDto? = null,

    val provider: CompanyDto? = null,

    val mvt: Double? = null,

    val credit: Double? = null,

    val createdDate : String? = "",

    val lastModifiedDate : String? = "",
){
    fun toClientProviderRelation() : ClientProviderRelation {
        return ClientProviderRelation(
            id = id,
            userId = person?.id,
            clientId = client?.id,
            providerId = provider?.id,
            mvt = mvt,
            credit = credit,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
    fun toClientProviderRelationModel() : com.aymen.metastore.model.entity.model.ClientProviderRelation {
        return com.aymen.metastore.model.entity.model.ClientProviderRelation(
            id = id,
            person = person?.toUserModel(),
            client = client?.toCompanyModel(),
            provider = provider?.toCompanyModel(),
            mvt = mvt,
            credit = credit,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
