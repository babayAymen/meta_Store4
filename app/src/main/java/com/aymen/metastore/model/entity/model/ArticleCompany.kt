package com.aymen.metastore.model.entity.model

import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.UnitArticle

data class ArticleCompany (
    val id : Long? = null,
    var unit: UnitArticle? = UnitArticle.U,
    var cost: Double? = 0.0,
    var quantity: Double? = 0.0,
    var minQuantity: Double? = 0.0,
    var sharedPoint: Long? = null,
    var margin: Double? = 0.0,
    var category: Category? = null,
    var subCategory: SubCategory? = null,
    var provider: Company? = null,
    var company: Company? = null,
//    var companyId: Long? = null,
    var isVisible: PrivacySetting? = PrivacySetting.PUBLIC,
    var sellingPrice: Double? = 0.0,
    var isFav : Boolean? = false,
    var article : Article? = null,
    var isEnabledToComment : Boolean? = false,
    val likeNumber : Long? = null,
)