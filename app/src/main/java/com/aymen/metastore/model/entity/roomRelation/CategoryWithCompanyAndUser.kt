    package com.aymen.metastore.model.entity.roomRelation

    import androidx.room.Embedded
    import androidx.room.Relation
    import com.aymen.metastore.model.entity.room.Category
    import com.aymen.metastore.model.entity.room.Company

    data class CategoryWithCompanyAndUser(
        @Embedded val category: Category,

        @Relation(
            parentColumn = "companyId",
            entityColumn = "id",
            entity = Company::class
        )
        val companyWithUser: CompanyWithUser?
    )
