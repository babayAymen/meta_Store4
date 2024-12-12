package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
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
) {
    fun toArticleCompanyDto(): ArticleCompanyDto {
        return ArticleCompanyDto(
            id = id,
            unit = unit,
            cost = cost,
            quantity = quantity,
            minQuantity = minQuantity,
            sharedPoint = sharedPoint,
            margin = margin,
            category = category?.toCategoryDto(),
            subCategory = subCategory?.toSubCategoryDto(),
            provider = provider?.toCompanyDto(),
            company = company?.toCompanyDto(),
            isVisible = isVisible,
            sellingPrice = sellingPrice,
            isFav = isFav,
            article = article?.toArticleDto(),
            isEnabledToComment = isEnabledToComment,
            likeNumber = likeNumber

        )
    }
    fun toArticleCompanyEntity(isSync : Boolean): ArticleCompany {
        return ArticleCompany(
            id = id,
            unit = unit,
            cost = cost,
            quantity = quantity,
            minQuantity = minQuantity,
            sharedPoint = sharedPoint,
            categoryId = category?.id,
            subCategoryId = subCategory?.id,
            providerId = provider?.id,
            companyId = company?.id,
            isVisible = isVisible,
            sellingPrice = sellingPrice,
            isFav = isFav,
            articleId = article?.id,
            isEnabledToComment = isEnabledToComment,
            likeNumber = likeNumber,
            isSync = isSync

        )
    }
}
