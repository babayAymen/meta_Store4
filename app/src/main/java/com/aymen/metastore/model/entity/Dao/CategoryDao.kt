package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Category

@Dao
interface CategoryDao {

    @Upsert
    suspend fun insertCategory(cat : Category)

    @Query("select * from category_werehouse")
    suspend fun getAllCategories() : List<Category>
}