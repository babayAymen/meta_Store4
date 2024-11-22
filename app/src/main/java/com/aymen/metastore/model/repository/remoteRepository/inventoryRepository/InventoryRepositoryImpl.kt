package com.aymen.store.model.repository.remoteRepository.inventoryRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.paging.InventoryRemoteMediator
import com.aymen.metastore.model.entity.paging.InvoiceRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.InventoryWithArticle
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val api : ServiceApi,
    private val room : AppDatabase
)
    :InventoryRepository{

        private val inventoryDao = room.inventoryDao()
    @OptIn(ExperimentalPagingApi::class)
    override fun getInventory(companyId : Long) : Flow<PagingData<InventoryWithArticle>>{
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = InventoryRemoteMediator(
                api = api, room = room, id= companyId
            ),
            pagingSourceFactory = { inventoryDao.getAllInventories()}
        ).flow.map {
            it.map { article ->
                article
            }
    }
    }
}
