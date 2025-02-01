package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.model.SubArticleModel
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.SubArticle

data class SubArticleWithArticles(
    @Embedded val subArticle: SubArticle,

    @Relation(
        parentColumn = "parentArticleId",
        entityColumn = "id",
        entity = ArticleCompany::class
    )
    val parentArticle : ArticleWithArticleCompany?,
    @Relation(
        parentColumn = "childArticleId",
        entityColumn = "id",
        entity = ArticleCompany::class
    )
    val childArticle : ArticleWithArticleCompany?,
){
   fun toSubArticleModel() : SubArticleModel{
       return subArticle.toSubArticle(
           parentArticle = parentArticle?.toArticleRelation(),
           childArticle = childArticle?.toArticleRelation()
       )
   }
}
