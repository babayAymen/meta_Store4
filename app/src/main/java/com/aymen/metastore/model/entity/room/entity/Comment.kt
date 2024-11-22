package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "comment",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Article::class, parentColumns = ["id"], childColumns = ["articleId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
    ])
data class Comment(

    @PrimaryKey val id : Long? = null,
    val content: String = "",
    val userId: Long? = null,
    val companyId: Long? = null,
    val articleId: Long? = null,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
){
    fun toComment(user: com.aymen.metastore.model.entity.model.User,
                  company: com.aymen.metastore.model.entity.model.Company,
                  article: com.aymen.metastore.model.entity.model.Article):com.aymen.metastore.model.entity.model.Comment{
        return com.aymen.metastore.model.entity.model.Comment(
            id = id,
            content = content,
            user = user,
            company = company,
            article = article,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
