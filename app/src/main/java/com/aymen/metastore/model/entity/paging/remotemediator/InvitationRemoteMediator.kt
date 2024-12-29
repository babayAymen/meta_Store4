package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.InvitationRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InvitationWithClientOrWorkerOrCompany
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class InvitationRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val companyId : Long
): RemoteMediator<Int, InvitationWithClientOrWorkerOrCompany> (){

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val invitationDao = room.invetationDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, InvitationWithClientOrWorkerOrCompany>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    getNextPageClosestToCurrentPosition(state)?.minus(1) ?: 0
                }

                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem(state)
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    previousePage
                }

                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem(state)
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    nextePage
                }
            }
            val response = api.getAllMyInvetations(companyId,currentPage, PAGE_SIZE)
            Log.e("aztshxfgh","response: ${response.content[0]}")
            val endOfPaginationReached = response.last
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    invitationDao.insertInvitationKeys(response.content.map { article ->
                        InvitationRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.worker?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.client?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.companySender?.user?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.companyReceiver?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.companySender?.toCompany()})
                    companyDao.insertCompany(response.content.map {company -> company.companyReceiver?.toCompany()})
                    invitationDao.insertInvitation(response.content.map { invitation -> invitation.toInvitation() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, InvitationWithClientOrWorkerOrCompany>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val remoteKey = entity?.let { invitationDao.getInvitationRemoteKey(it.invitation.id!!) }
        return remoteKey?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, InvitationWithClientOrWorkerOrCompany>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        val remoteKey = entity?.let { invitationDao.getInvitationRemoteKey(it.invitation.id!!) }
        return remoteKey?.nextPage
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, InvitationWithClientOrWorkerOrCompany>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val remoteKey = entity?.invitation?.id?.let { invitationDao.getInvitationRemoteKey(it) }
        return remoteKey?.nextPage

    }

    private suspend fun deleteCache(){
        invitationDao.clearAllRemoteKeysTables()
        invitationDao.clearAllInvitationTables()
    }
}