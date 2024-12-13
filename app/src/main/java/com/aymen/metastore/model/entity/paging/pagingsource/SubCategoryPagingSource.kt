package com.aymen.metastore.model.entity.paging.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

class SubCategoryPagingSource(
    private val api : ServiceApi,
    private val categoryId : Long,
    private val companyId : Long
) : PagingSource<Int, SubCategory>() {
    override fun getRefreshKey(state: PagingState<Int, SubCategory>): Int? {
       return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SubCategory> {
        val currentPage = params.key ?: 0
        Log.e("affectsubcategory","categ id : $categoryId and company id : $companyId")
        val response = api.getAllSubCategoriesByCategoryId(companyId = companyId, categoryId = categoryId ,page = currentPage, pageSize = PAGE_SIZE)
        val endOfPaginationReached = response.isEmpty()
        return try {
            LoadResult.Page(
                data = response.map { it.toSubCategoryModel() },
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (endOfPaginationReached) null else currentPage + 1,
            )
        }catch (ex : Exception){
            LoadResult.Error(ex)
        }
    }
}