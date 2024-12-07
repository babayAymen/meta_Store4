package com.aymen.metastore.model.entity.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.repository.globalRepository.ServiceApi

class SubCategoryPagingSource(
    private val api : ServiceApi,
    private val sharedViewModel: SharedViewModel,
    private val categoryId : Long
) : PagingSource<Int, SubCategory>() {
    override fun getRefreshKey(state: PagingState<Int, SubCategory>): Int? {
       return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SubCategory> {
        val currentPage = params.key ?: 0
        val id = if(sharedViewModel.accountType.value == AccountType.USER) sharedViewModel.user.value.id else sharedViewModel.company.value.id
        val response = api.getAllSubCategoriesByCategoryId(companyId = id!!, categoryId = categoryId ,page = currentPage, pageSize = PAGE_SIZE)
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