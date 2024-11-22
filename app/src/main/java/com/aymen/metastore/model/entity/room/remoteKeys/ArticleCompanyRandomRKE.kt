package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "article_company_random_remote_keys")
data class ArticleCompanyRandomRKE(
    @PrimaryKey val id : Long,
    val prevPage : Int?,
    val nextPage : Int?
)
