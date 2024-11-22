package com.aymen.metastore.model.repository.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.Transaction
import com.aymen.metastore.dependencyInjection.NetworkUtil
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.model.PaymentForProviders
import com.aymen.metastore.model.entity.model.PointsPayment
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.usecase.MetaUseCases
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
//                repository.sendPoints(pointPaymentDto)
            }catch (ex : Exception){
                Log.e("sendPointsexeption",ex.message.toString())
            }
        }
    }

    private val _allMyPointsPaymentForProviders : MutableStateFlow<PagingData<PaymentForProviders>> = MutableStateFlow(PagingData.empty())
    val allMyPointsPaymentForProviders: StateFlow<PagingData<PaymentForProviders>> get() = _allMyPointsPaymentForProviders

    private val _allMyPointsPayment : MutableStateFlow<PagingData<PointsPayment>> = MutableStateFlow(PagingData.empty())
    val allMyPointsPayment: StateFlow<PagingData<PointsPayment>> get() = _allMyPointsPayment


    private val _allMyProfits : MutableStateFlow<PagingData<PaymentForProviderPerDay>> = MutableStateFlow(PagingData.empty())
    val allMyProfits : StateFlow<PagingData<PaymentForProviderPerDay>> get() = _allMyProfits

    private val _myProfits = MutableStateFlow<String>("")
    val myProfits : StateFlow<String> = _myProfits


    fun getAllMyPointsPayment(context : Context) {
        if(!NetworkUtil.isOnline(context)){
            Toast.makeText(context, "You are offline", Toast.LENGTH_LONG).show()
            return
        }
            viewModelScope.launch(Dispatchers.IO) {
             useCases.getAllMyPointsPayment(sharedViewModel.company.value.id!!)
                 .distinctUntilChanged()
                 .cachedIn(viewModelScope)
                 .collect{
                     _allMyPointsPaymentForProviders.value = it.map { pointPayment -> pointPayment.toPaymentForProvidersWithCommandLine() }
                 }
            }

    }


    fun getMyProfitByDate(beginDate : String, finalDate : String){
        viewModelScope.launch(Dispatchers.IO) {
        sharedViewModel.isLoading = true
            try {
                val response = repository.getMyProfitByDate(beginDate, finalDate)
                if(response.isSuccessful){
                    _myProfits.value = response.body()?:""
                }
            }catch (ex : Exception){
                Log.e("getMyProfitByDateException","${ex.message}")
            }finally {
                    sharedViewModel.isLoading = false
            }
        }
    }

    fun getAllMyProfits(){
        sharedViewModel.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllMyProfits()
                if(response.isSuccessful){
                    response.body()?.forEach { paymentForProviderPerDay ->
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMyProfitsException","${ex.message}")
            }finally {
                    sharedViewModel.isLoading = false
            }
        }
    }

    fun getMyHistoryProfitByDate(beginDate : String, finalDate : String){
        sharedViewModel.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getMyHistoryProfitByDate(beginDate, finalDate)
                if(response.isSuccessful){
                    response.body()?.forEach { payment ->
                    }
                }
            }catch (ex : Exception){
                Log.e("getMyHistoryProfitByDateException","${ex.message}")
            }finally {
                    sharedViewModel.isLoading = false
            }

        }
    }











}