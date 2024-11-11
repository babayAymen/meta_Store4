package com.aymen.store.model.entity.dto

data class SubCategoryDto(
    var id : Long? = null,

    var libelle : String? = null,

    var code : String? = null,

    var image : String? = null,

    var category : CategoryDto? = null,

    var company : CompanyDto? = null
)
