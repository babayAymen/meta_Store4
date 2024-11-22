package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.model.Invitation
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class InvetationViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val useCases: MetaUseCases
): ViewModel() {

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
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = repository.RequestResponse(status,id)

            }catch (_ex: Exception) {
                Log.e("aymenababyinvetation", "error: $_ex")
            }
                }
        }
    }

}