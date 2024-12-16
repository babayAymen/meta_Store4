package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.CategoryDto
import com.aymen.metastore.model.entity.room.entity.Category

data class Category  (

    var id : Long? = null,

    var libelle : String? = null,

    var code : String ?= null,

    var image : String? = null,

    val company : Company? = null,

    var createdDate : String? = null,

    var lastModifiedDate : String? = null,
    ){
    fun toCategoryDto() : CategoryDto{
        return CategoryDto(
            id = id,
            libelle = libelle,
            code = code,
            image = image,
            company = company?.toCompanyDto(),
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
    fun toCategoryEntity() : Category{
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
}