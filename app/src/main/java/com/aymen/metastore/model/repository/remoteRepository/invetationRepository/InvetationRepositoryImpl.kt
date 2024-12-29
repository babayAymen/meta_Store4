package com.aymen.store.model.repository.remoteRepository.invetationRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.entity.model.Invitation
import com.aymen.metastore.model.entity.paging.remotemediator.InvitationRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvetationRepositoryImpl @Inject constructor(
    private val api: ServiceApi,
    private val room : AppDatabase
)
    : InvetationRepository {

        private val invitationDao = room.invetationDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyInvetations(companyId : Long): Flow<PagingData<Invitation>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = InvitationRemoteMediator(
                api = api,room = room, companyId = companyId
            ),
            pagingSourceFactory = { invitationDao.getAllInvitations()}
        ).flow.map {
            it.map { article ->
                article.toInvitationWithClientOrWorkerOrCompany()
            }
        }
    }
    override suspend fun RequestResponse(status : Status ,id: Long) = api.requestResponse(status,id)
    override suspend fun cancelInvitation(id: Long) = api.cancelInvitation(id)
}