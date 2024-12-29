package com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.entity.dto.PointsPaymentDto
import com.aymen.metastore.model.entity.dto.ReglementFoProviderDto
import com.aymen.metastore.model.entity.model.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.model.PointsPayment
import com.aymen.metastore.model.entity.model.ReglementForProviderModel
import com.aymen.metastore.model.entity.paging.remotemediator.PointsEspeceByDateRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.PointsEspeceRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.ProfitOfProviderMediator
import com.aymen.metastore.model.entity.paging.remotemediator.ProfitPerDayByDateRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.ProfitPerDayRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.RechargeRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.ReglementForProviderRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject
@OptIn(ExperimentalPagingApi::class)
class PointPaymentRepositoryImpl @Inject constructor(
    private val api: ServiceApi,
    private val room : AppDatabase
)
    : PointPaymentRepository {

        private val pointPaymentForProviderDao = room.paymentForProvidersDao()
    private val pointsPaymentDao = room.pointsPaymentDao()
    private val paymentForProviderPerDayDao = room.paymentForProviderPerDayDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyPaymentsEspeceByDate(
        id: Long,
        beginDate: String,
        finalDate: String
    ): Flow<PagingData<PaymentForProvidersWithCommandLine>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = PointsEspeceByDateRemoteMediator(
                api = api, room = room, id = id, beginDate = beginDate, finalDate = finalDate
            ),
            pagingSourceFactory = { pointPaymentForProviderDao.getAllMyPaymentsEspeceByDate( beginDate = beginDate, finalDate = finalDate)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }


    @OptIn(ExperimentalPagingApi::class)
    override fun getAllRechargeHistory(id: Long): Flow<PagingData<PointsPayment>> {
       return  Pager(

            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = RechargeRemoteMediator(
                api = api, room = room, id = id,
            ),
            pagingSourceFactory = { pointsPaymentDao.getAllRechargeHistory(id = id)}
        ).flow.map {
            it.map { article ->
                article.toPointsWithProvidersClientCompanyAndUser()
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyProfitsPerDay(companyId: Long): Flow<PagingData<PaymentForProviderPerDay>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = ProfitPerDayRemoteMediator(
                api = api, room = room, id = companyId,
            ),
            pagingSourceFactory = { paymentForProviderPerDayDao.getAllProfitPerDay()}
        ).flow.map {
            it.map { article ->
                article.toPaymentPerDayWithProvider()
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyPointsPaymentForPoviders(companyId: Long): Flow<PagingData<PaymentForProvidersWithCommandLine>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE,
                enablePlaceholders = false),
            remoteMediator = ProfitOfProviderMediator(
                api = api, room = room, id = companyId
            ),
            pagingSourceFactory = { pointPaymentForProviderDao.getAllMyPaymentsEspece()}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }


    override suspend fun sendPoints(pointsPayment: PointsPaymentDto) = api.sendPoints(pointsPayment)
    override suspend fun sendReglement(reglement: ReglementFoProviderDto) = api.sendReglement(reglement)

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyPaymentsEspece(companyId: Long) : Flow<PagingData<PaymentForProvidersWithCommandLine>>{
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = PointsEspeceRemoteMediator(
                api = api, room = room, id = companyId
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

    override fun getMyHistoryProfitByDate(id: Long, beginDate: String, finalDate: String): Flow<PagingData<PaymentPerDayWithProvider>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = ProfitPerDayByDateRemoteMediator(
                api = api, room = room, id = id, beginDate = beginDate , finalDate = finalDate
            ),
            pagingSourceFactory = { paymentForProviderPerDayDao.getMyHistoryProfitByDate(beginDate , finalDate)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    override fun getPaymentForProviderDetails(paymentId: Long): Flow<PagingData<ReglementForProviderModel>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = ReglementForProviderRemoteMediator(
                api = api, room = room, paymentId = paymentId
            ),
            pagingSourceFactory = { paymentForProviderPerDayDao.getMyHistoryReglementForProvider(paymentId)}
        ).flow.map {
            it.map { article ->
                article.toReglementForProviderModel()
            }
        }
    }


}