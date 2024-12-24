package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.model.Invitation
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.InvitationRemoteKeysEntity
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
@HiltViewModel
class InvetationViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val useCases: MetaUseCases,
    private val appViewModel: AppViewModel
): ViewModel() {

    private val invitationDao = room.invetationDao()
    private var _myAllInvetation : MutableStateFlow<PagingData<Invitation>> = MutableStateFlow(PagingData.empty())
    val myAllInvetation: StateFlow<PagingData<Invitation>> = _myAllInvetation

    fun getAllMyInvetations(){
        viewModelScope.launch(Dispatchers.IO) {
            useCases.getAllMyInvitations()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _myAllInvetation.value = it.map { invitation -> invitation.toInvitationWithClientOrWorkerOrCompany() }
                }
        }
    }


    fun RequestResponse(status : Status, id : Long){
        viewModelScope.launch(Dispatchers.IO) {
           invitationDao.requestResponse(status, id)
                    val result : Result<Response<Void>> = runCatching {
                    repository.RequestResponse(status,id)
                    }
            result.fold(
                onSuccess = {success ->
                    if(success.isSuccessful){
                        appViewModel.refreshToken {
                            
                        }
                    }
                },
                onFailure = {

                }
            )



        }
    }

}