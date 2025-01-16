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
                     0
                }

                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem()
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    previousePage
                }

                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem()
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }
            val response = api.getAllMyInvetations(companyId,currentPage, PAGE_SIZE)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
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

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return invitationDao.getFirstInvitationRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
       return invitationDao.getLatestInvitationRemoteKey()?.nextPage
    }

    private suspend fun deleteCache(){
        invitationDao.clearAllRemoteKeysTables()
        invitationDao.clearAllInvitationTables()
    }
}