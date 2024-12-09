package com.aymen.metastore.model.entity.paging.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.entity.dto.CompanyDto
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.globalRepository.ServiceApi

class AllCompaniesContainingPagingSource(
    private val api : ServiceApi,
    private val id : Long,
    private val search : String,
    private val searchType : SearchType
): PagingSource<Int, CompanyDto>() {
    override fun getRefreshKey(state: PagingState<Int, CompanyDto>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CompanyDto> {
        val currentPage = params.key ?: 0
        val response = api.getAllCompaniesContaining(id = id, search = search, searchType = searchType ,page = currentPage, pageSize = PAGE_SIZE)
        val endOfPaginationReached = response.isEmpty()
        return try {
            LoadResult.Page(
                data = response,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (endOfPaginationReached) null else currentPage + 1,
            )
        }catch (ex : Exception){
            LoadResult.Error(ex)
        }
    }
}