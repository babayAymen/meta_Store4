package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCateg(cat : List<Category>)

    suspend fun insertCategoryCateg(cat : List<Category?>){
        cat.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                insertCateg(it)
            }
    }


    @Upsert
    suspend fun insertKeys(keys : List<CategoryRemoteKeysEntity>)

    @Query("SELECT * FROM category_remote_keys_table WHERE id = :id")
    suspend fun getCategoryRemoteKey(id : Long): CategoryRemoteKeysEntity

    @Query("select * from category_werehouse WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId : Long) : Category

    @Transaction
    @Query("SELECT * FROM category_werehouse WHERE companyId = :companyId  ORDER BY id DESC")
     fun getAllCategoriesByCompanyId(companyId : Long): PagingSource<Int, CategoryWithCompanyAndUser>

    @Transaction
    @Query("SELECT * FROM category_werehouse WHERE id = :categoryId")
    suspend fun getCategoryWithCompanyAndUser(categoryId: Long): CategoryWithCompanyAndUser?

    @Query("DELETE FROM category_werehouse WHERE companyId = :id AND isCategory = 1")
    suspend fun clearAllCategoryTable(id : Long)

    @Query("DELETE FROM category_remote_keys_table")
    suspend fun clearAllRemoteKeysTable()

    @Query("SELECT MAX(id) FROM category_werehouse WHERE companyId = :companyId")
    suspend fun getLatestCategoryId(companyId : Long) : Long?
    @Query("SELECT * FROM category_remote_keys_table ORDER BY id ASC LIMIT 1")
    suspend fun getLatestCategoryRemoteKey(): CategoryRemoteKeysEntity?
   @Query("SELECT * FROM category_remote_keys_table ORDER BY id DESC LIMIT 1")
    suspend fun getFirstCategoryRemoteKey(): CategoryRemoteKeysEntity?

    @Query("SELECT COUNT(*) FROM category_werehouse WHERE companyId = :companyId")
    suspend fun getCategoryCount(companyId : Long) : Int
    @Upsert
    suspend fun insertSingCategory(cat : Category)
    @Upsert
    suspend fun insertSingelKey(key : CategoryRemoteKeysEntity)
    @Query("DELETE FROM category_werehouse WHERE id = :id")
    suspend fun deleteCategoryById(id : Long)
    @Query("DELETE FROM category_remote_keys_table WHERE id = :id")
    suspend fun deleteCategoryRemoteKeyById(id : Long)

}