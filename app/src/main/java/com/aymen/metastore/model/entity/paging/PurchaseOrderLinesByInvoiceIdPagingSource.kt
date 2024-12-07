package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.repository.globalRepository.ServiceApi

class PurchaseOrderLinesByInvoiceIdPagingSource(
    private val api : ServiceApi,
    private val companyId : Long,
    private val invoiceId : Long
    ) : PagingSource<Int, PurchaseOrderLine> (){
    override fun getRefreshKey(state: PagingState<Int, PurchaseOrderLine>): Int? {
        Log.e("getAllOrdersLineByInvoiceId", "begin in paging source")
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PurchaseOrderLine> {
        val currentPage = params.key ?: 0

        Log.e("getAllOrdersLineByInvoiceId", "begin in paging source")
        val response = api.getAllMyOrdersLinesByInvoiceId(companyId = companyId, invoiceId = invoiceId ,page = currentPage, pageSize = PAGE_SIZE)
        val endOfPaginationReached = response.isEmpty()
        return try {
            LoadResult.Page(
                data = response.map { it.toPurchaseOrderLineModel() },
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (endOfPaginationReached) null else currentPage + 1,
            )
        }catch (ex : Exception){
            LoadResult.Error(ex)
        }
    }
}