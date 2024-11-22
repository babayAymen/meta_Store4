package com.aymen.metastore.model.entity.model

data class Inventory (

    val id : Long? = null,
    val out_quantity : Double? = null,
    val in_quantity : Double? = null,
    val bestClient : Company? = null,
    val articleCost : Double? = null,
    val articleSelling : Double? = null,
    val discountOut : Double? = null,
    val discountIn : Double? = null,
    val company : Company? = null,
    val article : ArticleCompany? = null
)