package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.Transaction
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.Payment
import com.aymen.metastore.model.entity.model.PaymentForProviders
import com.aymen.metastore.model.entity.model.PointsPayment
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val sharedViewModel: SharedViewModel,
    private val useCases: MetaUseCases
):ViewModel() {

    val company by mutableStateOf(sharedViewModel.company.value)
    val user by mutableStateOf(sharedViewModel.user.value)
    val type by mutableStateOf(sharedViewModel.accountType)
    private val _paymentsEspece : MutableStateFlow<PagingData<PaymentForProviders>> = MutableStateFlow(PagingData.empty())
    val paymentsEspece: StateFlow<PagingData<PaymentForProviders>> get() = _paymentsEspece

    private val _myAllInvoice : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val myAllInvoice: StateFlow<PagingData<Invoice>> get() = _myAllInvoice

    init {
        when(sharedViewModel.accountType){
            AccountType.USER -> getAllMyPaymentsEspece(sharedViewModel.user.value.id!!)
            AccountType.COMPANY -> getAllMyPaymentsEspece(sharedViewModel.company.value.id!!)
            AccountType.AYMEN -> TODO()
        }

    }

    fun getAllMyPaymentsEspece(id : Long){
        viewModelScope.launch(Dispatchers.IO) {
            useCases.getAllMyPaymentsEspece(id)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _paymentsEspece.value = it.map { roomPayment -> roomPayment.toPaymentForProvidersWithCommandLine() }
                }
        }
    }

    fun getAllMyPaymentsEspeceFromMetaByDate( date : String, finDate : String){
        sharedViewModel.isLoading = true
        val id = sharedViewModel.company.value.id
        viewModelScope.launch(Dispatchers.IO) {
            useCases.getAllMyPaymentsEspeceByDate(id!!,date, finDate)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _paymentsEspece.value = it.map { roomPayment -> roomPayment.toPaymentForProvidersWithCommandLine() }
                }
        }
    }

    fun getAllMyPaymentFromInvoice(status : PaymentStatus){
        viewModelScope.launch(Dispatchers.IO){
//            useCases.getAllMyPaymentFromInvoice(status)
//                .distuctUntilChanged()
        }
    }

    fun getAllMyPaymentNotAccepted(){
        viewModelScope.launch(Dispatchers.IO) {
//            useCases.getAllMyPaymentNotAccepted()
//                .distinctUntilChanged()
        }
    }

//    @Transaction
//    suspend fun insertPayment(payment : PaymentForProvidersDto){
//        room.userDao().insertUser(mapUserToRoomUser(payment.purchaseOrderLine?.purchaseorder?.person))
//        room.purchaseOrderDao().insertOrder(mapPurchaseOrderToRoomPurchaseOrder(payment.purchaseOrderLine?.purchaseorder!!))
//        room.invoiceDao().insertInvoice(mapInvoiceToRoomInvoice(payment.purchaseOrderLine.invoice!!))
//        room.purchaseOrderLineDao().insertOrderLine(mapPurchaseOrderLineToRoomPurchaseOrderLine(payment.purchaseOrderLine))
//        room.paymentForProvidersDao().insertPaymentForProviders(
//            mappaymentForProvidersToRoomPaymentForProviders(payment)
//        )
//    }

}