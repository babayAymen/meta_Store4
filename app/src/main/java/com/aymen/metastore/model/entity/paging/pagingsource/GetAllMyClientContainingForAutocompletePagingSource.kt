package com.aymen.metastore.model.entity.paging.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.SearchType
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

class GetAllMyClientContainingForAutocompletePagingSource(
    private val api : ServiceApi,
    private val companyId : Long,
    private val clientName : String
): PagingSource<Int , ClientProviderRelationDto>() {
    override fun getRefreshKey(state: PagingState<Int, ClientProviderRelationDto>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ClientProviderRelationDto> {
        val currentPage = params.key ?: 0

        val response = api.getAllMyClientContaining(companyId = companyId, searchType = SearchType.CLIENT, clientName = clientName,page = currentPage, pageSize = PAGE_SIZE)
        val endOfPaginationReached = response.isEmpty()
        return try {
            LoadResult.Page(
                data = response,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (endOfPaginationReached) null else currentPage + 1,
            )
        }catch (ex : Exception){
            Log.e("getAllMyClientContaining","error : $ex")
            LoadResult.Error(ex)
        }
    }
}