package com.aymen.store.model.entity.api

data class Inventory(

    val id : Long? = null,

    var out_quantity : Double,

    var in_quantity : Double,

    var bestClient : CompanyDto,

    var articleCost : Double,

    var articleSelling : Double,

    var discountOut : Double,

    var discountIn : Double,

    var company : CompanyDto,

    var article : ArticleDto
)
