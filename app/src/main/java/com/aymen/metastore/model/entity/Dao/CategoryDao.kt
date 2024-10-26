package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Category
import com.aymen.metastore.model.entity.roomRelation.CategoryWithCompanyAndUser

@Dao
interface CategoryDao {

    @Upsert
    suspend fun insertCategory(cat : Category)

    @Query("select * from category_werehouse WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId : Long) : Category

    @Query("SELECT * FROM category_werehouse WHERE companyId = :companyId")
    suspend fun getAllCategoriesByCompanyId(companyId : Long):List<Category>

    @Transaction
    @Query("SELECT * FROM category_werehouse WHERE id = :categoryId")
    suspend fun getCategoryWithCompanyAndUser(categoryId: Long): CategoryWithCompanyAndUser?

}