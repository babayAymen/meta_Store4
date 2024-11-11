package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.dto.InvoiceDto
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val sharedViewModel: SharedViewModel
):ViewModel() {

    val company by mutableStateOf(sharedViewModel.company.value)
    val user by mutableStateOf(sharedViewModel.user.value)
    val type by mutableStateOf(sharedViewModel.accountType)
    private val _paymentsEspece = MutableStateFlow<List<PaymentForProvidersWithCommandLine>>(emptyList())
    val paymentsEspece: StateFlow<List<PaymentForProvidersWithCommandLine>> = _paymentsEspece

    private val _myAllInvoice = MutableStateFlow<List<InvoiceWithClientPersonProvider>>(emptyList())
    val myAllInvoice: StateFlow<List<InvoiceWithClientPersonProvider>> = _myAllInvoice

    fun getAllMyPaymentsEspeceFromMetaByDate(date : String, finDate : String){
        sharedViewModel.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllMyPaymentsEspeceByDate(date, finDate)
                if(response.isSuccessful){
                    response.body()!!.forEach {
                      insertPayment(it)
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMyPaymentsEspeceByDateException","${ex.message}")
            }finally {
                    sharedViewModel.isLoading = false
            }
            _paymentsEspece.value = room.paymentForProvidersDao().getMyPaymentByDate(date , finDate)
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
        viewModelScope.launch(Dispatchers.IO) {
        sharedViewModel.isLoading = true
            try {
               val response = repository.getAllMyPaymentsEspece(id?:0L)
                if (response.isSuccessful) {
                    response.body()?.forEach {
                        insertPayment(it)
                    }
                }
            }catch (ex : Exception){
                Log.e("aymenbabat","${ex.message}")
            }finally {
                    sharedViewModel.isLoading = false
            }
              _paymentsEspece.value = room.paymentForProvidersDao().getAllMyPaymentsEspece()

        }
    }


    fun getAllMyPaymentFromInvoice(status : PaymentStatus){
        viewModelScope.launch(Dispatchers.IO){
            try {
                sharedViewModel.isLoading = true
                _myAllInvoice.value = emptyList()
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
            _myAllInvoice.value = room.invoiceDao().getAllInvoicesAsProviderAndPaymentStatus(sharedViewModel.company.value.id!!, status)
        }
    }


    fun getAllMyPaymentNotAccepted(){
        viewModelScope.launch(Dispatchers.IO) {
        sharedViewModel.isLoading = true
            try {
                val response = repository.getAllMyPaymentNotAccepted(company.id!!)
                if(response.isSuccessful){
                    response.body()?.forEach {
                        insertInvoice(it)
                    }
                }
            }catch(ex : Exception) {
                Log.e("getAllMyPaymentNotAccepted","exception : ${ex.message}")
            }
            finally {
                sharedViewModel.isLoading = true
            }
            if(type == AccountType.COMPANY){
            _myAllInvoice.value = room.invoiceDao().getAllMyInvoicesAsProviderAndStatus(company.id!!, Status.INWAITING)
            }
            if(type == AccountType.USER){
                _myAllInvoice.value = room.invoiceDao().getAllMyInvoicesAsPersonAndStatus(user.id!!, Status.INWAITING)
            }
        }
    }

    @Transaction
    suspend fun insertInvoice(invoice : InvoiceDto){
        invoice.client?.let {
            room.userDao().insertUser(mapUserToRoomUser(it.user))
            room.companyDao().insertCompany(mapCompanyToRoomCompany(it))
        }
        invoice.person?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        room.companyDao().insertCompany(mapCompanyToRoomCompany(invoice.provider))
        room.invoiceDao().insertInvoice(mapInvoiceToRoomInvoice(invoice))
    }

}