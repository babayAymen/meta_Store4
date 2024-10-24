package com.aymen.store.model.entity.dto

import com.aymen.metastore.model.entity.Dto.ArticleCompanyDto

data class InventoryDto(

    val id : Long? = null,

    val out_quantity : Double? = null,

    val in_quantity : Double? = null,

    val bestClient : CompanyDto? = null,

    val articleCost : Double? = null,

    val articleSelling : Double? = null,

    val discountOut : Double? = null,

    val discountIn : Double? = null,

    val company : CompanyDto? = null,

    val article : ArticleCompanyDto? = null
)
