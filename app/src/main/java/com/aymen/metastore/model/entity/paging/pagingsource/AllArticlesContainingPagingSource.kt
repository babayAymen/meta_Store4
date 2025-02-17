package com.aymen.metastore.model.entity.paging.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.SearchType
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

class AllArticlesContainingPagingSource(
    private val api : ServiceApi,
    private val search : String,
    private val searchType: SearchType,
    private val id : Long,
    private val asProvider : Boolean
) : PagingSource<Int, ArticleCompanyDto>(){
    override fun getRefreshKey(state: PagingState<Int, ArticleCompanyDto>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleCompanyDto> {
        return try {
        val currentPage = params.key ?: 0
        val response = api.getAllMyArticleContaining(companyId = id, search = search, searchType = searchType, asProvider = asProvider ,page = currentPage, pageSize = PAGE_SIZE)
        val endOfPaginationReached = response.isEmpty()
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