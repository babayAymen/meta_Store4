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
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.User
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider
import com.aymen.metastore.model.entity.roomRelation.PointsWithProviderclientcompanyanduser
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PointsPaymentViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val sharedViewModel: SharedViewModel,
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

    private val _allMyPointsPayment = MutableStateFlow<List<PointsWithProviderclientcompanyanduser>>(emptyList())
    val allMyPointsPayment: StateFlow<List<PointsWithProviderclientcompanyanduser>> = _allMyPointsPayment


    private val _allMyProfits = MutableStateFlow<List<PaymentPerDayWithProvider>>(emptyList())
    val allMyProfits : StateFlow<List<PaymentPerDayWithProvider>> = _allMyProfits

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
                    _allMyPointsPayment.value = room.pointsPaymentDao().getAllMyointsPayment()

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
                        insertPointPerDay(paymentForProviderPerDay)
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMyProfitsException","${ex.message}")
            }finally {
                    sharedViewModel.isLoading = false
            }
            _allMyProfits.value = room.paymentForProviderPerDayDao().getAllMyProfits()
        }
    }

    fun getMyHistoryProfitByDate(beginDate : String, finalDate : String){
        sharedViewModel.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
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
            _allMyProfits.value = room.paymentForProviderPerDayDao().getAllMyProfitsByDate(beginDate, finalDate)

        }
    }











}