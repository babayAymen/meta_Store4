package com.aymen.store.model.entity.dto

data class CategoryDto(

    val id : Long? = null,

    val libelle : String? = null,

    val code : String ?= null,

    val image : String? = null,

    val company : CompanyDto? = null

    )
