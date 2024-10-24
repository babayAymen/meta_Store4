package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.Dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.room.ClientProviderRelation

fun mapRelationToRoomRelation(relation : ClientProviderRelationDto): ClientProviderRelation{
    return ClientProviderRelation(
        id = relation.id,
        personId = relation.person?.id,
        clientId = relation.client?.id,
        providerId = relation.provider?.id,
        mvt = relation.mvt,
        credit = relation.credit,
        advance = relation.advance,
        createdDate = relation.createdDate,
        lastModifiedDate = relation.lastModifiedDate

    )
}