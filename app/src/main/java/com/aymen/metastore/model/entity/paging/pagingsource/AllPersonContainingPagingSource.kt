package com.aymen.metastore.model.entity.paging.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.entity.dto.UserDto
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.SearchType
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

class AllPersonContainingPagingSource(
    private val api : ServiceApi,
    private val companyId : Long,
    private val searchType : SearchType,
    private val libelle : String
) : PagingSource<Int, UserDto>(){
    override fun getRefreshKey(state: PagingState<Int, UserDto>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserDto> {
        val currentPage = params.key ?: 0
        val response = api.getAllClientsPersonContaining(companyId = companyId, searchType = searchType, libelle = libelle ,page = currentPage, pageSize = PAGE_SIZE)
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