package com.aymen.store.model.repository.ViewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.entity.Dto.PaymentForProvidersDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapInvoiceToRoomInvoice
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderLineToRoomPurchaseOrderLine
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderToRoomPurchaseOrder
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.converterRealmToApi.mappaymentForProvidersToRoomPaymentForProviders
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.entity.dto.InvoiceDto
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.entity.realm.Payment
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
class PaymentViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val realm: Realm,
    private val room : AppDatabase,
    private val companyViewModel: CompanyViewModel,
    private val sharedViewModel: SharedViewModel
):ViewModel() {

    val company by mutableStateOf(sharedViewModel.company.value)
    val user by mutableStateOf(sharedViewModel.user.value)
    val type by mutableStateOf(sharedViewModel.accountType)
    private val _paymentsEspece = MutableStateFlow<List<PaymentForProviders>>(emptyList())
    val paymentsEspece: StateFlow<List<PaymentForProviders>> = _paymentsEspece

    private val _myAllInvoice = MutableStateFlow<List<Invoice>>(emptyList())
    val myAllInvoice: StateFlow<List<Invoice>> = _myAllInvoice

    fun getAllMyPaymentsEspeceByDate(date : String, finDate : String){
        sharedViewModel.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respons = repository.getAllMyPaymentsEspeceByDatee(date, finDate)
                if(respons.isSuccessful){
                    _paymentsEspece.value = respons.body()?: emptyList()
                    respons.body()!!.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
                val response = repository.getAllMyPaymentsEspeceByDate(date, finDate)
                if(response.isSuccessful){
                 //   _paymentsEspece.value = response.body()?: emptyList() // change to get locally
                    response.body()!!.forEach {
                      insertPayment(it)
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMyPaymentsEspeceByDateException","${ex.message}")
            }finally {
                    sharedViewModel.isLoading = false
            }
        }
    }

    @Transaction
    suspend fun insertPayment(payment : PaymentForProvidersDto){
        room.userDao().insertUser(mapUserToRoomUser(payment.purchaseOrderLine?.purchaseorder?.person))
        room.purchaseOrderDao().insertOrder(mapPurchaseOrderToRoomPurchaseOrder(payment.purchaseOrderLine?.purchaseorder!!))
        room.invoiceDao().insertInvoice(mapInvoiceToRoomInvoice(payment.purchaseOrderLine.invoice!!))
        room.purchaseOrderLineDao().insertOrderLine(mapPurchaseOrderLineToRoomPurchaseOrderLine(payment.purchaseOrderLine))
        room.paymentForProvidersDao().insertPaymentForProviders(
            mappaymentForProvidersToRoomPaymentForProviders(payment)
        )
    }


    fun getAllMyPaymentsEspece(id : Long?){
        sharedViewModel.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
               val respons = repository.getAllMyPaymentsEspecee(id?:0L)
                if (respons.isSuccessful) {
                    sharedViewModel.isLoading = false
                    respons.body()?.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
               val response = repository.getAllMyPaymentsEspece(id?:0L)
                if (response.isSuccessful) {
                    response.body()?.forEach {
                        insertPayment(it)
                    }
                }
            }catch (ex : Exception){
                Log.e("aymenbabat",ex.message.toString())
            }finally {
                    sharedViewModel.isLoading = false
            }
            if(sharedViewModel.accountType == AccountType.COMPANY) {
                companyViewModel.getMyCompany { company ->
                    _paymentsEspece.value = repository.getAllMyPaymentsEspeceLocally(company?.id!!)
                }
            }
            else{
                _paymentsEspece.value = repository.getAllMyPaymentsHistoryLocally(sharedViewModel.user.value.id!!)
            }

        }
    }


    fun getAllMyPaymentFromInvoice(status : PaymentStatus){
        viewModelScope.launch(Dispatchers.IO){
        sharedViewModel.isLoading = true
        _myAllInvoice.value = emptyList()
            try {
                val respons = repository.getAllMyInvoicesAsProviderAndStatuss(companyId = sharedViewModel.company.value.id!!, status)
                if(respons.isSuccessful){
                    respons.body()?.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
                val response = repository.getAllMyInvoicesAsProviderAndStatus(companyId = sharedViewModel.company.value.id!!, status)
                if(response.isSuccessful){
                    response.body()?.forEach {
                        insertInvoice(it)
                    }
                }
            }catch(ex : Exception){
                Log.e("getAllMyPaymentFromInvoice","exception is : ${ex.message}")
            }finally {
                sharedViewModel.isLoading = false
            }
            _myAllInvoice.value = repository.getAllMyInvoicesAsProviderAndStatusLocally(companyId = sharedViewModel.company.value.id!!, status)
        }
    }


    fun getAllMyPaymentNotAccepted(){
        sharedViewModel.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respons = repository.getAllMyPaymentNotAcceptedd(company.id!!)
                if(respons.isSuccessful){
                    respons.body()?.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
                val response = repository.getAllMyPaymentNotAccepted(company.id!!)
                if(response.isSuccessful){
                    response.body()?.forEach {
                        insertInvoice(it)
                    }
                }
            }catch(ex : Exception) {
                Log.e("getAllMyPaymentNotAccepted","exception : ${ex.message}")
            }
            if(type == AccountType.COMPANY){
            _myAllInvoice.value = repository.getAllMyPaymentNotAcceptedLocally(company.id!!)
            }
            if(type == AccountType.USER){
                _myAllInvoice.value = repository.getAllMyPaymentNotAcceptedLocally(user.id!!)
            }
        }
    }

    @Transaction
    suspend fun insertInvoice(invoice : InvoiceDto){
        Log.e("insertinvoice","before client")
        invoice.client?.let {
            room.userDao().insertUser(mapUserToRoomUser(it.user))
            room.companyDao().insertCompany(mapCompanyToRoomCompany(it))
        }
        Log.e("insertinvoice","after client")
        invoice.person?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        Log.e("insertinvoice","before provider")
        room.companyDao().insertCompany(mapCompanyToRoomCompany(invoice.provider))
        Log.e("insertinvoice","before invoice")
        room.invoiceDao().insertInvoice(mapInvoiceToRoomInvoice(invoice))
        Log.e("insertinvoice","fun fun")
    }

}