package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.SubCategory
import com.aymen.metastore.model.entity.room.remoteKeys.CategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.SubCategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory

@Dao
interface SubCategoryDao {

    @Upsert
    suspend fun insert(sub : List<SubCategory>)

    suspend fun insertSubCategory(sub: List<SubCategory?>) {
        sub.filterNotNull()
            .takeIf { it.isNotEmpty() }
            ?.let {
                insert(it)
            }
    }
    @Upsert
    suspend fun insertKeys(subCategoryRemoteKeysEntity: List<SubCategoryRemoteKeysEntity>)
    @Query("SELECT * FROM sub_category_remote_keys_table WHERE id = :id")
    suspend fun getSubCategoryRemoteKey(id : Long) : SubCategoryRemoteKeysEntity
    @Query("select * from subcategory_werehouse WHERE companyId = :companyId ORDER BY id DESC")
     fun getAllSubCategories(companyId  :Long) : PagingSource<Int, SubCategoryWithCategory>
    @Query("DELETE FROM sub_category_remote_keys_table")
    suspend fun clearAllRemoteKeysTable()
    @Query("DELETE FROM subcategory_werehouse WHERE companyId = :id AND isSubcategory = 1")
    suspend fun clearAllSubCategoryTable(id : Long)
    @Query("SELECT MAX(id) FROM subcategory_werehouse WHERE id = :id")
    suspend fun getLatestSubCategoryId(id : Long) : Long?
    @Query("SELECT COUNT(*) FROM subcategory_werehouse WHERE id = :id")
    suspend fun getSubCategoryCount(id : Long) : Int
    @Upsert
    suspend fun insertSingleSubCategory(subCategory : SubCategory)
    @Upsert
    suspend fun insertSingleSubCategoryRemoteKey(key : SubCategoryRemoteKeysEntity)
    @Query("DELETE FROM subcategory_werehouse WHERE id = :id")
    suspend fun deleteSubCategoryById(id :Long)
    @Query("DELETE FROM sub_category_remote_keys_table WHERE id = :id")
    suspend fun deleteSubCategoryRemoteKey(id : Long)
    @Query("SELECT * FROM sub_category_remote_keys_table ORDER BY id ASC LIMIT 1")
    suspend fun getLatestSubCategoryRemoteKey(): SubCategoryRemoteKeysEntity?

}