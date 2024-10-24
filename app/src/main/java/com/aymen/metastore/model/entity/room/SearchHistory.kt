package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.SearchCategory
@Entity(tableName = "search_history",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["companyId"]),
        ForeignKey(
            entity = ArticleCompany::class,
            parentColumns = ["id"],
            childColumns = ["articleId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
    ])
data class SearchHistory(

    @PrimaryKey val id : Long? = null,

    val  companyId : Long? = null,

    val articleId : Long? = null,

    val userId : Long? = null,

    val searchCategory : SearchCategory? = SearchCategory.OTHER,

    val createdDate : String = "",

    val lastModifiedDate : String = ""
)
