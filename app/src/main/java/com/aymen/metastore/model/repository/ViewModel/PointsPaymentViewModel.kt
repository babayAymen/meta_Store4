package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.api.PaymentForProviderPerDayDto
import com.aymen.metastore.model.entity.api.PointsPaymentDto
import com.aymen.metastore.model.entity.realm.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.PointsPayment
import com.aymen.metastore.model.entity.realm.User
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.entity.api.CompanyDto
import com.aymen.store.model.entity.api.UserDto
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PointsPaymentViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val sharedViewModel: SharedViewModel,
    private val realm : Realm
) :ViewModel(){

    var pointPaymentDto by mutableStateOf(PointsPaymentDto())
    var isLoding by mutableStateOf(false)

    fun sendPoints(user: User, amount : Long, client : Company){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                if (pointPaymentDto.clientUser == null) {
                    pointPaymentDto.clientUser = UserDto()
                }
                if (pointPaymentDto.clientCompany == null) {
                    pointPaymentDto.clientCompany = CompanyDto()
                }
                pointPaymentDto.amount = amount
                pointPaymentDto.clientUser?.id = user.id
                pointPaymentDto.clientCompany?.id = client.id
                repository.sendPoints(pointPaymentDto)
            }catch (ex : Exception){
                Log.e("sendPointsexeption",ex.message.toString())
            }
        }
    }

    private val _allMyPointsPayment = MutableStateFlow<List<PointsPayment>>(emptyList())
    val allMyPointsPayment: StateFlow<List<PointsPayment>> = _allMyPointsPayment


    private val _allMyProfits = MutableStateFlow<List<PaymentForProviderPerDay>>(emptyList())
    val allMyProfits : StateFlow<List<PaymentForProviderPerDay>> = _allMyProfits

    private val _myProfits = MutableStateFlow<String>("")
    val myProfits : StateFlow<String> = _myProfits


    fun getAllMyPointsPayment() {
            viewModelScope.launch(Dispatchers.IO) {
                    isLoding = true
                    try {
                        val response = repository.getAllMyPointsPayment(sharedViewModel.company.value.id?: 0)
                        if (response.isSuccessful) {
                            isLoding = false
                            response.body()?.forEach { pointsPayment ->
                                realm.write {
                                    copyToRealm(pointsPayment, UpdatePolicy.ALL)
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        Log.e("getAllMyPointsPayment", ex.message.toString())
                    }
                    _allMyPointsPayment.value = repository.getAllMyPointsPaymentLocally()

            }

    }

    fun getMyProfitByDate(beginDate : String, finalDate : String){
        isLoding = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getMyProfitByDate(beginDate, finalDate)
                if(response.isSuccessful){
                    isLoding = false
                    _myProfits.value = response.body()?:""
                }
            }catch (ex : Exception){
                Log.e("getMyProfitByDateException","${ex.message}")
            }
        }
    }

    fun getAllMyProfits(){
        isLoding = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllMyProfits()
                if(response.isSuccessful){
                    isLoding = false
                    Log.e("getAllMyProfitsException","${response.body()?.size}")
//                    _allMyProfits.value = response.body()?: emptyList()
                    response.body()?.forEach { paymentForProviderPerDay ->
                        realm.write {
                            copyToRealm(paymentForProviderPerDay, UpdatePolicy.ALL)
                        }
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMyProfitsException","${ex.message}")
            }
            _allMyProfits.value = repository.getAllMyProfitsLocally()
        }
    }

    fun getMyHistoryProfitByDate(beginDate : String, finalDate : String){
        isLoding = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getMyHistoryProfitByDate(beginDate, finalDate)
                if(response.isSuccessful){
                    isLoding = false
                    _allMyProfits.value = response.body()?: emptyList()
                }
            }catch (ex : Exception){
                Log.e("getMyHistoryProfitByDateException","${ex.message}")
            }
        }
    }











}