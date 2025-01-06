package com.aymen.metastore.model.entity.paging.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.Enum.SearchPaymentEnum
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.metastore.util.PAGE_SIZE

class SearchInvoicePagingSource(
    private val api : ServiceApi,
    private val type : SearchPaymentEnum,
    private val text : String,
    private val companyId : Long
): PagingSource<Int , Invoice>() {
    override fun getRefreshKey(state: PagingState<Int, Invoice>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Invoice> {
        val currentPage = params.key ?: 0
        return try {
            val response = api.searchInvoice(id = companyId ,type = type, text = text ,page = currentPage, pageSize = PAGE_SIZE)
            val endOfPaginationReached = response.isEmpty()
            LoadResult.Page(
                data = response.map { it.toInvoiceModel() },
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (endOfPaginationReached) null else currentPage + 1,
            )
        }catch (ex : Exception){
            LoadResult.Error(ex)
        }
    }
}