package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Category
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.SubCategory
import com.aymen.metastore.model.entity.room.entity.User

data class SubCategoryWithCategory(
    @Embedded val subCategory : SubCategory,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category : Category,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val company : CompanyWithUser?
){
    fun toSubCategory() : com.aymen.metastore.model.entity.model.SubCategory{
        return subCategory.toSubCategory(
            category = category.toCategory(company?.toCompany()?:com.aymen.metastore.model.entity.model.Company()),
            company = company?.toCompany()?:com.aymen.metastore.model.entity.model.Company()
        )
    }
}
