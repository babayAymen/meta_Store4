package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.SubCategoryDto

data class SubCategory (
    var id : Long? = null,
    val libelle : String? = null,
    val code : String? = null,
    val image : String? = null,
    val category : Category? = null,
    val company : Company? = null
){
    fun toSubCategoryDto() : SubCategoryDto{
        return SubCategoryDto(
            id = id,
            libelle = libelle,
            code = code,
            image = image,
            category =  category?.toCategoryDto(),
            company = company?.toCompanyDto()
        )
    }
}