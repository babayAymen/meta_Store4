package com.aymen.metastore.model.entity.paging.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.entity.dto.CategoryDto
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

class CategoryPagingSource(private val api : ServiceApi, private val companyId: Long) : PagingSource<Int, Category>() {

    override fun getRefreshKey(state: PagingState<Int, Category>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Category> {
        return try {
        val currentPage = params.key ?: 0
        val response = api.getPagingCategoryByCompany(companyId = companyId ,page = currentPage, pageSize = PAGE_SIZE)
        val endOfPaginationReached = response.isEmpty()
        LoadResult.Page(
         data = response.map { categ -> categ.toCategoryModel() },
         prevKey = if (currentPage == 0) null else currentPage - 1,
         nextKey = if (endOfPaginationReached) null else currentPage + 1,
        )
        }catch (ex : Exception){
            LoadResult.Error(ex)
        }

    }
}