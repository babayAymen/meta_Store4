package com.aymen.metastore.model.entity.Dto

import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto
import com.aymen.store.model.entity.realm.Company

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
)
