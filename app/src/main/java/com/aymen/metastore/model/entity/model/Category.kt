package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.CategoryDto

data class Category  (

    var id : Long? = null,

    var libelle : String? = null,

    var code : String ?= null,

    val image : String? = null,

    val company : Company? = null,

    var createdDate : String = "",

    var lastModifiedDate : String = "",
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
}