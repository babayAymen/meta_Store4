package com.aymen.metastore.model.entity.paging.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

class CompanyArticlesByCategoryOrSubCategoryPagingSource(
    private val api : ServiceApi,
    private val comapnyId : Long,
    private val categoryId : Long,
    private val subCategoryId : Long,
): PagingSource<Int, ArticleCompanyDto>() {

    override fun getRefreshKey(state: PagingState<Int, ArticleCompanyDto>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleCompanyDto> {
        return try {
        val currentPage = params.key ?: 0
        val response = api.companyArticlesByCategoryOrSubCategory(companyId = comapnyId, categoryId = categoryId, subcategoryId = subCategoryId ,page = currentPage, pageSize = PAGE_SIZE)
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