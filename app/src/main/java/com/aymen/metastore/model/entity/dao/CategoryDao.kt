package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Category
import com.aymen.metastore.model.entity.room.remoteKeys.CategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.CategoryWithCompanyAndUser

@Dao
interface CategoryDao {

    @Upsert
    suspend fun insert(cat : List<Category>)

    suspend fun insertCategory(cat : List<Category?>){
        cat.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                insert(it)
            }
    }
    @Upsert
    suspend fun insertKeys(keys : List<CategoryRemoteKeysEntity>)

    @Query("SELECT * FROM category_remote_keys_table WHERE id = :id")
    suspend fun getCategoryRemoteKey(id : Long): CategoryRemoteKeysEntity

    @Query("select * from category_werehouse WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId : Long) : Category

    @Transaction
    @Query("SELECT * FROM category_werehouse WHERE companyId = :companyId")
     fun getAllCategoriesByCompanyId(companyId : Long): PagingSource<Int, CategoryWithCompanyAndUser>

    @Transaction
    @Query("SELECT * FROM category_werehouse WHERE id = :categoryId")
    suspend fun getCategoryWithCompanyAndUser(categoryId: Long): CategoryWithCompanyAndUser?

    @Query("DELETE FROM category_werehouse")
    suspend fun clearAllCategoryTable()
    @Query("DELETE FROM category_remote_keys_table")
    suspend fun clearAllRemoteKeysTable()

}