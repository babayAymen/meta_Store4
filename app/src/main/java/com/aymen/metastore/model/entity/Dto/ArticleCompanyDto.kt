package com.aymen.metastore.model.entity.Dto

import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.UnitArticle
import com.aymen.store.model.entity.dto.ArticleDto
import com.aymen.store.model.entity.dto.CategoryDto
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.SubCategoryDto

data class ArticleCompanyDto(
    val id : Long? = null,
    var unit: UnitArticle? = UnitArticle.U,
    var cost: Double? = 0.0,
    var quantity: Double? = 0.0,
    var minQuantity: Double? = 0.0,
    var sharedPoint: Long? = null,
    var margin: Double? = 0.0,
    var category: CategoryDto? = null,
    var subCategory: SubCategoryDto? = null,
    var provider: CompanyDto? = null,
    var company: CompanyDto? = null,
    var companyId: Long? = null,
    var isVisible: PrivacySetting? = PrivacySetting.PUBLIC,
    var sellingPrice: Double? = 0.0,
    var isFav : Boolean? = false,
    var article : ArticleDto? = null,
    var isEnabledToComment : Boolean? = false
)
