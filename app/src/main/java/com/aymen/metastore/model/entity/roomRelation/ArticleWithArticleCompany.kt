package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.Article
import com.aymen.metastore.model.entity.room.ArticleCompany
import com.aymen.metastore.model.entity.room.Company

data class ArticleWithArticleCompany(
    @Embedded var articleCompany: ArticleCompany,

    @Relation(
        parentColumn = "articleId",
        entityColumn = "id"
    )
    val article : Article,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "id"
    )
    val company : Company
)
