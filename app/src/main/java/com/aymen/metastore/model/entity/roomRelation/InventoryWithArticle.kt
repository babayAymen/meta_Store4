package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.Article
import com.aymen.metastore.model.entity.room.ArticleCompany
import com.aymen.metastore.model.entity.room.Inventory

data class InventoryWithArticle(
    @Embedded val inventory: Inventory,

    @Relation(
        parentColumn = "articleId",
        entityColumn = "id",
        entity = ArticleCompany::class
    )
    val article : ArticleWithArticleCompany
)
