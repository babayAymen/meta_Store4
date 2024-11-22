package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.SubCategory
import com.aymen.metastore.model.entity.room.remoteKeys.SubCategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory

@Dao
interface SubCategoryDao {

    @Upsert
    suspend fun insertSubCategory(sub : List<SubCategory>)

    @Upsert
    suspend fun insertKeys(subCategoryRemoteKeysEntity: List<SubCategoryRemoteKeysEntity>)

    @Query("SELECT * FROM sub_category_remote_keys_table WHERE id = :id")
    suspend fun getSubCategoryRemoteKey(id : Long) : SubCategoryRemoteKeysEntity

    @Query("select * from subcategory_werehouse")
     fun getAllSubCategories() : PagingSource<Int, SubCategoryWithCategory>

    @Query("DELETE FROM sub_category_remote_keys_table")
    suspend fun clearAllRemoteKeysTable()

    @Query("DELETE FROM subcategory_werehouse")
    suspend fun clearAllSubCategoryTable()

}