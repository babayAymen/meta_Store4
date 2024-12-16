package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.Comment
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.User

data class CommentWithArticleAndUserOrCompany(
    @Embedded val comment : Comment,

    @Relation(
        entity = ArticleCompany::class,
        parentColumn = "articleId",
        entityColumn = "id"
    )
    val article : ArticleWithArticleCompany,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user : User?,

    @Relation(
        entity = Company::class,
        parentColumn = "companyId",
        entityColumn = "companyId"
    )
    val company: CompanyWithUser
){

    fun toCommentModel() : com.aymen.metastore.model.entity.model.Comment{
        return comment.toComment(
            user = user?.toUser(),
            company = company.toCompany(),
            article = article.toArticleRelation()
        )
    }
}
