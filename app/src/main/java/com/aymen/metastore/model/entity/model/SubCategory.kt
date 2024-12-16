package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.SubCategoryDto
import com.aymen.metastore.model.entity.room.entity.SubCategory

data class SubCategory (
    var id : Long? = null,
    var libelle : String? = null,
    var code : String? = null,
    val image : String? = null,
    var category : Category? = null,
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
    fun toSubCategoryentity() : SubCategory{
        return SubCategory(
            id = id,
            libelle = libelle,
            code = code,
            image = image,
            categoryId =  category?.id,
            companyId = company?.id
        )
    }
}