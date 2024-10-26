package com.aymen.store.model.entity.dto

data class SubCategoryDto(
    val id : Long? = null,

    val libelle : String? = null,

    val code : String? = null,

    val image : String? = null,

    val category : CategoryDto? = null,

    val company : CompanyDto? = null
)
