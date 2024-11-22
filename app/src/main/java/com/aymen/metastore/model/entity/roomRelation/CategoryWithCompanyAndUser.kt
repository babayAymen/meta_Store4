    package com.aymen.metastore.model.entity.roomRelation

    import androidx.room.Embedded
    import androidx.room.Relation
    import com.aymen.metastore.model.entity.room.entity.Category
    import com.aymen.metastore.model.entity.room.entity.Company
    import com.aymen.metastore.model.entity.room.entity.User

    data class CategoryWithCompanyAndUser(
        @Embedded val category: Category,

        @Relation(
            parentColumn = "companyId",
            entityColumn = "companyId",
            entity = Company::class
        )
        val companyWithUser: CompanyWithUser? = null
    ){
        fun toCategoryWithCompanyAndUser(): com.aymen.metastore.model.entity.model.Category {
            return category.toCategory(company = companyWithUser?.toCompany()?:com.aymen.metastore.model.entity.model.Company())
        }
    }
