package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.model.PaymentForProviders
import com.aymen.metastore.model.entity.model.PointsPayment
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PointsPaymentViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val sharedViewModel: SharedViewModel,
    private val room : AppDatabase,
    private val useCases: MetaUseCases
) :ViewModel(){

    var pointPaymentDto by mutableStateOf(PointsPayment())
    fun sendPoints(user: User, amount : Long, client : Company){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                if (pointPaymentDto.clientUser == null) {
                    pointPaymentDto.clientUser = User()
                }
                if (pointPaymentDto.clientCompany == null) {
                    pointPaymentDto.clientCompany = Company()
                }
                pointPaymentDto.amount = amount
                pointPaymentDto.clientUser?.id = user.id
                pointPaymentDto.clientCompany?.id = client.id
                repository.sendPoints(pointPaymentDto.toPointPaymentDto())
            }catch (ex : Exception){
                Log.e("sendPointsexeption",ex.message.toString())
            }
        }
    }

    private val _allMyPointsPaymentForProviders : MutableStateFlow<PagingData<PaymentForProviders>> = MutableStateFlow(PagingData.empty())
    val allMyPointsPaymentForProviders: StateFlow<PagingData<PaymentForProviders>> get() = _allMyPointsPaymentForProviders

    private val _allMyPointsPaymentRecharge : MutableStateFlow<PagingData<PointsPayment>> = MutableStateFlow(PagingData.empty())
    val allMyPointsPaymentRecharge: StateFlow<PagingData<PointsPayment>> get() = _allMyPointsPaymentRecharge


    private val _allMyProfitsPerDay : MutableStateFlow<PagingData<PaymentForProviderPerDay>> = MutableStateFlow(PagingData.empty())
    val allMyProfitsPerDay : StateFlow<PagingData<PaymentForProviderPerDay>> get() = _allMyProfitsPerDay

    private val _allMyProfitsPerDayByDate : MutableStateFlow<PagingData<PaymentForProviderPerDay>> = MutableStateFlow(PagingData.empty())
    val allMyProfitsPerDayByDate : StateFlow<PagingData<PaymentForProviderPerDay>> get() = _allMyProfitsPerDayByDate

    private val _myProfits = MutableStateFlow<String>("")
    val myProfits : StateFlow<String> = _myProfits

init {

    val id = if(sharedViewModel.accountType.value == AccountType.COMPANY)sharedViewModel.company.value.id else sharedViewModel.user.value.id
    getAllMyPointsPaymentt(sharedViewModel.company.value.id?:0)
    getAllMyPointsPaymentRecharge(id?:0)
}
    fun getAllMyPointsPaymentt(companyId: Long) {
//        if(!NetworkUtil.isOnline(context)){
//            Toast.makeText(context, "You are offline", Toast.LENGTH_LONG).show()
//            return
//        }
            viewModelScope.launch {
             useCases.getAllMyPointsPaymentForProvider(companyId)
                 .distinctUntilChanged()
                 .cachedIn(viewModelScope)
                 .collect{
                     _allMyPointsPaymentForProviders.value = it
                 }
            }

    }

    fun getAllMyPointsPaymentRecharge(companyId : Long) {
        viewModelScope.launch {
                useCases.getAllRechargeHistory(companyId)
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collect {
                        _allMyPointsPaymentRecharge.value =
                            it.map { pointPayment -> pointPayment.toPointsWithProvidersClientCompanyAndUser() }
                    }

        }
    }

    fun getAllMyProfitsPerDay(){
        viewModelScope.launch {
            useCases.getAllMyProfitsPerDay(sharedViewModel.company.value.id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _allMyProfitsPerDay.value = it.map { profit -> profit.toPaymentPerDayWithProvider() }
                }
        }
    }

    fun getMyProfitByDate(beginDate : String, finalDate : String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getMyProfitByDate(beginDate, finalDate)
                if(response.isSuccessful){
                    _myProfits.value = response.body()?:""
                }
            }catch (ex : Exception){
                Log.e("getMyProfitByDateException","${ex.message}")
            }
        }
    }


    fun getMyHistoryProfitByDate(beginDate : String, finalDate : String){
        viewModelScope.launch {
            useCases.getMyHistoryProfitByDate(sharedViewModel.company.value.id!!,beginDate, finalDate)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                  _allMyProfitsPerDayByDate.value = it.map{ profit -> profit.toPaymentPerDayWithProvider()}
                }

        }
    }











}