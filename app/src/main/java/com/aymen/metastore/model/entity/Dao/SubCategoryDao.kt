package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.SubCategory

@Dao
interface SubCategoryDao {

    @Upsert
    suspend fun insertSubCategory(sub : SubCategory)

    @Query("select * from subcategory_werehouse")
    suspend fun getAllSubCategories() : List<SubCategory>

    @Query("SELECT * FROM subcategory_werehouse WHERE companyId = :companyId")
    suspend fun getAllSubCategoriesByCompanyId(companyId : Long) : List<SubCategory>

    @Query("SELECT * FROM subcategory_werehouse WHERE categoryId = :categoryId")
    suspend fun getAllSubCategoriesByCategoryId(categoryId : Long) : List<SubCategory>
}