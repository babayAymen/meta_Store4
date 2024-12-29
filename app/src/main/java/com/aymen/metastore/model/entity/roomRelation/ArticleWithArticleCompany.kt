package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.Category
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.SubCategory
import com.aymen.metastore.model.entity.room.entity.User

data class ArticleWithArticleCompany(
    @Embedded val articleCompany: ArticleCompany,

    @Relation(
        parentColumn = "articleId",
        entityColumn = "id"
    )
    val article : Article,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val company : CompanyWithUser? =null,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category : Category? = null,

    @Relation(
        parentColumn = "subCategoryId",
        entityColumn = "id",
        entity = SubCategory::class
    )
    val subCategory : SubCategoryWithCategory? = null,

    @Relation(
        parentColumn = "providerId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val provider : CompanyWithUser? = null,

    ){

    fun toArticleRelation(): com.aymen.metastore.model.entity.model.ArticleCompany {
        return articleCompany.toArticle(
            category = category?.toCategory(company = company?.toCompany()?:com.aymen.metastore.model.entity.model.Company()),
            subCategory = subCategory?.toSubCategory(),
            provider = provider?.toCompany()?:com.aymen.metastore.model.entity.model.Company(),
            article = article.toArticle(),
            company = company?.toCompany()?:com.aymen.metastore.model.entity.model.Company()
        )
    }
}
