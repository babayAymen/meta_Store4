package com.aymen.metastore.model.entity.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.repository.globalRepository.ServiceApi

class ArticleCompanyPagingSource(
    private val api : ServiceApi,
    private val sharedViewModel: SharedViewModel,
    private val loadType: LoadType
): PagingSource<Int , ArticleCompany>() {
    override fun getRefreshKey(state: PagingState<Int, ArticleCompany>): Int? {
       return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleCompany> {
        val currentPage = params.key ?: 0
        val id = if(sharedViewModel.accountType == AccountType.USER) sharedViewModel.user.value.id else sharedViewModel.company.value.id
        val response = when(loadType){
            LoadType.RANDOM -> api.getRandomArticles(offset = currentPage, pageSize = PAGE_SIZE)
            LoadType.ADMIN -> api.getAll(companyId = id!! ,offset = currentPage, pageSize = PAGE_SIZE)
            LoadType.CONTAINING -> TODO()
        }
        val endOfPaginationReached = response.isEmpty()
        return try {
            LoadResult.Page(
                data = response.map { it.toArticleCompanyModel() },
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (endOfPaginationReached) null else currentPage + 1,
            )
        }catch (ex : Exception){
            LoadResult.Error(ex)
        }
    }

}