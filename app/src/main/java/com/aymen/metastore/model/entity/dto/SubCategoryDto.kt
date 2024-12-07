package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.SubCategory


data class SubCategoryDto(
    var id : Long? = null,
    var libelle : String? = null,
    var code : String? = null,
    val image : String? = null,
    var category : CategoryDto? = null,
    val company : CompanyDto? = null
){
    fun toSubCategory() : SubCategory {
        return SubCategory(
            id = id,
            libelle = libelle,
            code = code,
            image = image,
            categoryId = category?.id,
            companyId = company?.id
        )
    }
    fun toSubCategoryModel() : com.aymen.metastore.model.entity.model.SubCategory {
        return com.aymen.metastore.model.entity.model.SubCategory(
            id = id,
            libelle = libelle,
            code = code,
            image = image,
            category = category?.toCategoryModel(),
            company = company?.toCompanyModel()
        )
    }
}
