package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
@Entity(tableName = "comment",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(entity = Article::class, parentColumns = ["id"], childColumns = ["articleId"]),
    ])
data class Comment(

    @PrimaryKey val id : Long? = null,

    val content: String = "",

    val userId: Long? = null,

    val companyId: Long? = null,

    val articleId: Long? = null,

    val createdDate : String = "",

    val lastModifiedDate : String = "",

)
