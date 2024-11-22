package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.Inventory
import com.aymen.metastore.model.entity.room.entity.User

data class InventoryWithArticle(
    @Embedded val inventory: Inventory,

    @Relation(
        parentColumn = "articleId",
        entityColumn = "id",
        entity = ArticleCompany::class
    )
    val article : ArticleWithArticleCompany,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "userId",
        entity = Company::class
    )
    val company : CompanyWithUser
){
    fun toInventory(): com.aymen.metastore.model.entity.model.Inventory {
        return inventory.toInventory(
            article = article.toArticleRelation(),
            company = company.toCompany()
        )
    }
}
