package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.SearchHistory
import com.aymen.store.model.Enum.SearchCategory
@Entity(tableName = "search_history",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(
            entity = ArticleCompany::class,
            parentColumns = ["id"],
            childColumns = ["articleId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = ClientProviderRelation::class, parentColumns = ["id"], childColumns = ["userRelationId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = ClientProviderRelation::class, parentColumns = ["id"], childColumns = ["clientRelationId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
    ])
data class SearchHistory(

    @PrimaryKey val id : Long? = null,

    val  companyId : Long? = null,

    val articleId : Long? = null,

    val userId : Long? = null,

    val userRelationId : Long? = null,

    val clientRelationId : Long? = null,

    val searchCategory : SearchCategory? = SearchCategory.OTHER,

    val createdDate : String? = "",

    val lastModifiedDate : String? = ""
){
    fun toSearchHitoryModel(     company: com.aymen.metastore.model.entity.model.Company?,
                                 article: com.aymen.metastore.model.entity.model.ArticleCompany?,
                                 user: com.aymen.metastore.model.entity.model.User?,
                                 userRelation: com.aymen.metastore.model.entity.model.ClientProviderRelation?,
                                 clientRelation : com.aymen.metastore.model.entity.model.ClientProviderRelation?
    ): SearchHistory {
        return SearchHistory(
            id = id,
            company = company,
            article = article,
            user = user,
            userRelation = userRelation,
            clientRelation = clientRelation,
            searchCategory = searchCategory,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
