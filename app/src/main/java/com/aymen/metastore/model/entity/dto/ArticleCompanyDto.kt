package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.UnitArticle
import kotlinx.serialization.Serializable

@Serializable
data class ArticleCompanyDto(
    val id : Long? = null,
    var unit: UnitArticle? = UnitArticle.U,
    var cost: Double? = 0.0,
    var quantity: Double? = 0.0,
    var minQuantity: Double? = 0.0,
    var sharedPoint: Long? = null,
    var margin: Double? = 0.0,
    var isVisible: PrivacySetting? = PrivacySetting.PUBLIC,
    var sellingPrice: Double? = 0.0,
    var isFav : Boolean? = false,
    var isEnabledToComment : Boolean? = false,
    val likeNumber : Long? = 0,
    var article : ArticleDto? = null,
    var subCategory: SubCategoryDto? = null,
    var provider: CompanyDto? = null,
    var category: CategoryDto? = null,
    var company: CompanyDto? = null,
    val isDeleted : Boolean?= false,
    val isBought : Boolean?= true,
    val commentNumber : Long? = 0,
    val rate : Double? = null,
    val raters : Int? = null
){
    fun toArticleCompany(isRandom : Boolean, isSearch : Boolean? = false, isMy : Boolean? = false): ArticleCompany {
    return ArticleCompany(
        id,
        unit,
        cost,
        quantity,
        minQuantity,
        sharedPoint,
        categoryId = category?.id,
        subCategoryId = subCategory?.id,
        providerId = provider?.id,
        companyId = company?.id,
        isRandom = isRandom,
        isVisible = isVisible,
        sellingPrice = sellingPrice,
        isFav = isFav,
        articleId = article?.id,
        isEnabledToComment = isEnabledToComment,
         likeNumber = likeNumber,
        isSearch = isSearch,
        commentNumber = commentNumber,
        isMy = isMy,
        isBought = isBought,
        rate = rate,
        raters = raters
    )
    }
    fun toArticleCompanyModel():com.aymen.metastore.model.entity.model.ArticleCompany{
        return com.aymen.metastore.model.entity.model.ArticleCompany(
            id,
            unit,
            cost,
            quantity,
            minQuantity,
            sharedPoint,
            category = category?.toCategoryModel(),
            subCategory = subCategory?.toSubCategoryModel(),
            provider = provider?.toCompanyModel(),
            company = company?.toCompanyModel(),
            isVisible = isVisible,
            sellingPrice = sellingPrice,
            isFav = isFav,
            article = article?.toArticleModel(),
            isEnabledToComment = isEnabledToComment,
            likeNumber = likeNumber,
            commentNumber = commentNumber,
            isBought = isBought,
            raters = raters,
            rate = rate
        )
    }
}
