package com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.dto.PointsPaymentDto
import com.aymen.metastore.model.entity.paging.PointsEspeceRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.entity.roomRelation.PointsWithProviderclientcompanyanduser
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PointPaymentRepositoryImpl @Inject constructor(
    private val api: ServiceApi,
    private val room : AppDatabase
)
    : PointPaymentRepository {

        private val pointPaymentForProviderDao = room.paymentForProvidersDao()
    private val pointsPaymentDao = room.pointsPaymentDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyPaymentsEspeceByDate(
        id: Long,
        beginDate: String,
        finalDate: String
    ): Flow<PagingData<PaymentForProvidersWithCommandLine>> {
        return Pager(

            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = PointsEspeceRemoteMediator(
                api = api, room = room, type = LoadType.ADMIN, id = id, beginDate = beginDate, finalDate = finalDate, status = null
            ),
            pagingSourceFactory = { pointPaymentForProviderDao.getAllMyPaymentsEspeceByDate(id = id, beginDate = beginDate, finalDate = finalDate)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyPointsPayment(companyId: Long) : Flow<PagingData<PaymentForProvidersWithCommandLine>> {
        return Pager(

            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = PointsEspeceRemoteMediator(
                api = api, room = room, type = LoadType.CONTAINING, id = companyId, beginDate = null, finalDate = null, status = null
            ),
            pagingSourceFactory = { pointsPaymentDao.getAllMyPointsPayment(companyId = companyId)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }
//    @OptIn(ExperimentalPagingApi::class)
//    override fun getAllMyPaymentNotAccepted(): Flow<PagingData<PointsWithProviderclientcompanyanduser>> {
//        return Pager(
//
//            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
//            remoteMediator = PointsEspeceRemoteMediator(
//                api = api, room = room, type = LoadType.CONTAINING, id = null, beginDate = null, finalDate = null, status = null
//            ),
//            pagingSourceFactory = { pointPaymentForProviderDao.getAllMyPaymentNotAccepted()}
//        ).flow.map {
//            it.map { article ->
//                article
//            }
//        }
//    }

//    @OptIn(ExperimentalPagingApi::class)
//    override fun getAllMyPaymentFromInvoice(companyId : Long ,status: PaymentStatus): Flow<PagingData<PointsWithProviderclientcompanyanduser>> {
//        return Pager(
//
//            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
//            remoteMediator = PointsEspeceRemoteMediator(
//                api = api, room = room, type = LoadType.CONTAINING, id = companyId, beginDate = null, finalDate = null,status = status
//            ),
//            pagingSourceFactory = { pointPaymentForProviderDao.getAllMyPaymentFromInvoice(id = companyId , status = status)}
//        ).flow.map {
//            it.map { article ->
//                article
//            }
//        }
//    }

    override suspend fun sendPoints(pointsPayment: PointsPaymentDto) = api.sendPoints(pointsPayment)
    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyPaymentsEspece(companyId: Long) : Flow<PagingData<PaymentForProvidersWithCommandLine>>{
        return Pager(

            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = PointsEspeceRemoteMediator(
                api = api, room = room, type = LoadType.RANDOM, id = companyId, beginDate = null, finalDate = null, status = null
            ),
            pagingSourceFactory = { pointPaymentForProviderDao.getAllMyPaymentsEspece()}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }
    override suspend fun getMyProfitByDate(beginDate: String, finalDate: String) = api.getMyProfitByDate(beginDate, finalDate)
    override suspend fun getAllMyProfits() = api.getAllMyProfits()
    override suspend fun getMyHistoryProfitByDate(beginDate: String, finalDate: String) = api.getMyHistoryProfitByDate(beginDate, finalDate)


}