package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.UnitArticle

@Entity(tableName = "article_company",
    foreignKeys = [
        ForeignKey(entity = Article::class, parentColumns = ["id"], childColumns = ["articleId"]),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(entity = SubCategory::class, parentColumns = ["id"], childColumns = ["subCategoryId"]),
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["providerId"]),
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["companyId"]),
    ]
)
data class ArticleCompany(
    @PrimaryKey
    val id : Long? = null,
    val unit: UnitArticle? = UnitArticle.U,
    val cost: Double? = 0.0,
    val quantity: Double? = 0.0,
    val minQuantity: Double? = 0.0,
    val sharedPoint: Long? = null,
    val sellingPrice: Double? = 0.0,
    val categoryId: Long? = null,
    val subCategoryId: Long? = null,
    val providerId: Long? = null,
    val companyId: Long? = null,
    val isRandom : Boolean? = false,
    val isFav : Boolean? = false,
    val likeNumber : Long? = null,
    val commentNumber : Long? = null,
    val isVisible : PrivacySetting? = PrivacySetting.ONLY_ME,
    var articleId : Long? = null,
    val isEnabledToComment : Boolean? = false,
)
