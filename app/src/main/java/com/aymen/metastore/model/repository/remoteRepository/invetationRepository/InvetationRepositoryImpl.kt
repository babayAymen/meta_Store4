package com.aymen.store.model.repository.remoteRepository.invetationRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.paging.CompanyRemoteMediator
import com.aymen.metastore.model.entity.paging.InvitationRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.InvitationWithClientOrWorkerOrCompany
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.globalRepository.ServiceApi
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
    override fun getAllMyInvetations(): Flow<PagingData<InvitationWithClientOrWorkerOrCompany>>{
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = InvitationRemoteMediator(
                api = api,room = room
            ),
            pagingSourceFactory = { invitationDao.getAllInvitations()}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }
    override suspend fun RequestResponse(status : Status ,id: Long) = api.RequestResponse(status,id)
    override suspend fun cancelInvitation(id: Long) = api.cancelInvitation(id)
}