package com.aymen.metastore.model.entity.model

data class ClientProviderRelation(
    val id : Long? = null,
    val person: User? = null,
    val client: Company? = null,
    val provider: Company? = null,
    val mvt: Double? = null,
    val credit: Double? = null,
    val advance: Double? = null,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
)