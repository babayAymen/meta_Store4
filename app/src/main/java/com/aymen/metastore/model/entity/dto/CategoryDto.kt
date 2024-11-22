package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.Category


data class CategoryDto(

    var id : Long? = null,

    val libelle : String? = null,

    val code : String ?= null,

    val image : String? = null,

    val company : CompanyDto? = null,

    var createdDate : String = "",

    var lastModifiedDate : String = "",

    ){
    fun toCategory() : Category {
    return Category(
        id = id,
        libelle = libelle,
        code = code,
        image = image,
        companyId = company?.id,
        createdDate = createdDate,
        lastModifiedDate = lastModifiedDate
    )
    }
    fun toCategoryModel() : com.aymen.metastore.model.entity.model.Category {
    return com.aymen.metastore.model.entity.model.Category(
        id = id,
        libelle = libelle,
        code = code,
        image = image,
        company = company?.toCompanyModel(),
        createdDate = createdDate,
        lastModifiedDate = lastModifiedDate
    )
    }
}
