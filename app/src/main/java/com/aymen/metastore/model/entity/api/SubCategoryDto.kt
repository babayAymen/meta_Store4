package com.aymen.store.model.entity.api

data class SubCategoryDto(
    var id : Long? = null,

    var libelle : String = "",

    var code : String = "",

    val image : String? = "",

    var category : CategoryDto = CategoryDto(),

    var company : CompanyDto = CompanyDto()
)
