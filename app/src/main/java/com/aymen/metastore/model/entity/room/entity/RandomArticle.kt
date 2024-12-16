package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.UnitArticle

@Entity(tableName = "random_article_company",
    foreignKeys = [
        ForeignKey(entity = Article::class, parentColumns = ["id"], childColumns = ["articleId"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = SubCategory::class, parentColumns = ["id"], childColumns = ["subCategoryId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["providerId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
    ],
    indices = [Index("articleId"), Index("categoryId"), Index("subCategoryId"), Index("providerId"), Index("companyId"), Index("id")]
)
data class RandomArticle(
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
){
    fun toArticle(category: com.aymen.metastore.model.entity.model.Category,
                  subCategory: com.aymen.metastore.model.entity.model.SubCategory?,
                  provider: com.aymen.metastore.model.entity.model.Company,
                  company : com.aymen.metastore.model.entity.model.Company,
                  article: com.aymen.metastore.model.entity.model.Article) : ArticleCompany {
        return ArticleCompany(
            id = id,
            unit = unit,
            cost = cost,
            quantity = quantity,
            minQuantity = minQuantity,
            sharedPoint = sharedPoint,
            sellingPrice = sellingPrice,
            category = category,
            subCategory = subCategory,
            provider = provider,
            company = company,
            article = article,
            isFav = isFav,
            likeNumber = likeNumber,
            commentNumber = commentNumber,
            isVisible = isVisible,
            isEnabledToComment = isEnabledToComment


            )
    }
}
