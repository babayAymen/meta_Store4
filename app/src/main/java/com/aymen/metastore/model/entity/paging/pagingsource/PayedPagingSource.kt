package com.aymen.metastore.model.entity.paging.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.repository.globalRepository.ServiceApi

class PayedPagingSource(
    private val api : ServiceApi,
    private val id : Long,
    private val isProvider : Boolean,
    private val paymentStatus: PaymentStatus
): PagingSource<Int, Invoice> (){

    override fun getRefreshKey(state: PagingState<Int, Invoice>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Invoice> {
        val currentPage = params.key ?: 0

        val response = if(isProvider)api.getAllBuyHistoryByPaidStatusAsProvider(id,
            paymentStatus,currentPage, PAGE_SIZE)
        else api.getAllBuyHistoryByPaidStatusAsClient(id, paymentStatus,currentPage, PAGE_SIZE)
        val endOfPaginationReached = response.isEmpty()
        return try {
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