package com.aymen.store.model.repository.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.dependencyInjection.NetworkUtil
import com.aymen.metastore.model.entity.Dto.PaymentForProviderPerDayDto
import com.aymen.metastore.model.entity.Dto.PointsPaymentDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapPaymentForProviderPerDayToRoomPaymentForProviderPerDay
import com.aymen.metastore.model.entity.converterRealmToApi.mapPointsPaymentToRoomPointsPayment
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.realm.PaymentForProviderPerDay
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.PointsPayment
import com.aymen.metastore.model.entity.realm.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PointsPaymentViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val sharedViewModel: SharedViewModel,
    private val realm : Realm,
    private val room : AppDatabase
) :ViewModel(){

    var pointPaymentDto by mutableStateOf(PointsPaymentDto())
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


    fun getAllMyPointsPayment(context : Context) {
        if(!NetworkUtil.isOnline(context)){
            Toast.makeText(context, "You are offline", Toast.LENGTH_LONG).show()
            return
        }
            viewModelScope.launch(Dispatchers.IO) {
                    sharedViewModel.isLoading = true
                    _allMyPointsPayment.value = emptyList()
                    try {
                        val respons = repository.getAllMyPointsPaymentt(sharedViewModel.company.value.id?: 0)
                        if (respons.isSuccessful) {
                            respons.body()?.forEach { pointsPayment ->
                                realm.write {
                                    copyToRealm(pointsPayment, UpdatePolicy.ALL)
                                }
                            }
                        }
                        val response = repository.getAllMyPointsPayment(sharedViewModel.company.value.id?: 0)
                        if (response.isSuccessful) {
                            response.body()?.forEach { pointsPayment ->
                                insertPointPayment(pointsPayment)
                            }
                        }
                    } catch (ex: Exception) {
                        Log.e("getAllMyPointsPayment", ex.message.toString())
                    }finally {
                        // Turn off loading regardless of success or failure
                        sharedViewModel.isLoading = false
                    }
                    _allMyPointsPayment.value = repository.getAllMyPointsPaymentLocally()

            }

    }

    @Transaction
    suspend fun insertPointPayment(pointsPayment : PointsPaymentDto){
        pointsPayment.clientUser?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        pointsPayment.clientCompany?.let {
            room.userDao().insertUser(mapUserToRoomUser(it.user))
            room.companyDao().insertCompany(mapCompanyToRoomCompany(it))
        }
        room.userDao().insertUser(mapUserToRoomUser(pointsPayment.provider?.user))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(pointsPayment.provider))
        room.pointsPaymentDao().insertPointsPayment(
            mapPointsPaymentToRoomPointsPayment(pointsPayment)
        )
    }

    @Transaction
    suspend fun insertPointPerDay(pointperday : PaymentForProviderPerDayDto){
        room.userDao().insertUser(mapUserToRoomUser(pointperday.provider?.user))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(pointperday.provider))
        room.paymentForProviderPerDayDao().insertPaymentForProviderPerDay(
            mapPaymentForProviderPerDayToRoomPaymentForProviderPerDay(pointperday)
        )
    }
    fun getMyProfitByDate(beginDate : String, finalDate : String){
        sharedViewModel.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getMyProfitByDate(beginDate, finalDate)
                if(response.isSuccessful){
                    sharedViewModel.isLoading = false
                    _myProfits.value = response.body()?:""
                }
            }catch (ex : Exception){
                Log.e("getMyProfitByDateException","${ex.message}")
            }
        }
    }

    fun getAllMyProfits(){
        sharedViewModel.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respons = repository.getAllMyProfitss()
                if(respons.isSuccessful){
                    respons.body()?.forEach { paymentForProviderPerDay ->
                        realm.write {
                            copyToRealm(paymentForProviderPerDay, UpdatePolicy.ALL)
                        }
                    }
                }
                val response = repository.getAllMyProfits()
                if(response.isSuccessful){
                    response.body()?.forEach { paymentForProviderPerDay ->
                        insertPointPerDay(paymentForProviderPerDay)
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMyProfitsException","${ex.message}")
            }finally {
                    sharedViewModel.isLoading = false
            }
            _allMyProfits.value = repository.getAllMyProfitsLocally()
        }
    }

    fun getMyHistoryProfitByDate(beginDate : String, finalDate : String){
        sharedViewModel.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respons = repository.getMyHistoryProfitByDatee(beginDate, finalDate)
                if(respons.isSuccessful){
                    _allMyProfits.value = respons.body()?: emptyList()
                }
                val response = repository.getMyHistoryProfitByDate(beginDate, finalDate)
                if(response.isSuccessful){
                    response.body()?.forEach { payment ->
                    insertPointPerDay(payment)
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