package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.SearchHistory
import com.aymen.metastore.model.entity.room.entity.User

data class SearchHistoryWithClientOrProviderOrUserOrArticle(
    @Embedded var searchHistory: SearchHistory,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "userId",
        entity = Company::class
    )
    val company : CompanyWithUser? = null,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user : User? = null,

    @Relation(
        parentColumn = "articleId",
        entityColumn = "id",
        entity = ArticleCompany::class
    )
    val article : ArticleWithArticleCompany? = null
){
    fun toSearchHistoryModel(): com.aymen.metastore.model.entity.model.SearchHistory {
        return searchHistory.toSearchHitoryModel(
            company = company?.toCompany(),
            article = article?.toArticleRelation(),
            user = user?.toUser()
        )
    }
}
