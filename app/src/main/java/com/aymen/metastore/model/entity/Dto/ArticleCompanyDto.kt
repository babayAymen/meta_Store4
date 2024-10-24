package com.aymen.metastore.model.entity.Dto

import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.UnitArticle
import com.aymen.store.model.entity.dto.ArticleDto
import com.aymen.store.model.entity.dto.CategoryDto
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.SubCategoryDto

data class ArticleCompanyDto(
    val id : Long? = null,
    var unit: UnitArticle = UnitArticle.U,
    var cost: Double = 0.0,
    var quantity: Double = 0.0,
    var minQuantity: Double = 0.0,
    var sharedPoint: Long? = null,
    var margin: Double = 0.0,
    var category: CategoryDto = CategoryDto(),
    var subCategory: SubCategoryDto = SubCategoryDto(),
    var provider: CompanyDto = CompanyDto(),
    var company: CompanyDto = CompanyDto(),
    var companyId: Long? = null,
    var isVisible: PrivacySetting = PrivacySetting.PUBLIC,
    var sellingPrice: Double = 0.0,
    var isFav : Boolean = false,
    var article : ArticleDto = ArticleDto(),
    var isEnabledToComment : Boolean = false
)
