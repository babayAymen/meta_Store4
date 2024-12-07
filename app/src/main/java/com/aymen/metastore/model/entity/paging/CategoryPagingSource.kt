package com.aymen.metastore.model.entity.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.entity.dto.CategoryDto
import com.aymen.store.model.repository.globalRepository.ServiceApi

class CategoryPagingSource(private val api : ServiceApi, private val sharedViewModel: SharedViewModel) : PagingSource<Int, CategoryDto>() {// should change to category

    override fun getRefreshKey(state: PagingState<Int, CategoryDto>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CategoryDto> {
        val currentPage = params.key ?: 0
        val id = if(sharedViewModel.accountType.value == AccountType.USER) sharedViewModel.user.value.id else sharedViewModel.company.value.id
        val response = api.getPagingCategoryByCompany(companyId = id!! ,page = currentPage, pageSize = PAGE_SIZE)
        val endOfPaginationReached = response.isEmpty()
        return try {
        LoadResult.Page(
         data = response, // should change to response.map{it.toCategory()}
         prevKey = if (currentPage == 0) null else currentPage - 1,
         nextKey = if (endOfPaginationReached) null else currentPage + 1,
        )
        }catch (ex : Exception){
            LoadResult.Error(ex)
        }

    }
}