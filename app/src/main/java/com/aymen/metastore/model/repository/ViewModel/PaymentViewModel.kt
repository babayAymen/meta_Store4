package com.aymen.metastore.model.repository.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.PaymentForProviders
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
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
  private val _paymentsEspeceByDate : MutableStateFlow<PagingData<PaymentForProviders>> = MutableStateFlow(PagingData.empty())
    val paymentsEspeceByDate: StateFlow<PagingData<PaymentForProviders>> get() = _paymentsEspeceByDate

    private val _myAllBuyHistory : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val myAllBuyHistory: StateFlow<PagingData<Invoice>> get() = _myAllBuyHistory

    private val _inComplete : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val inComplete: StateFlow<PagingData<Invoice>> get() = _inComplete

    init {
        when(sharedViewModel.accountType){
            AccountType.USER -> {
                getAllMyPaymentsEspece(sharedViewModel.user.value.id!!)
            }
            AccountType.COMPANY -> {
                getAllMyPaymentsEspece(sharedViewModel.company.value.id!!)
                getAllMyPaymentFromInvoicee(PaymentStatus.ALL)

            }
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
        val id = sharedViewModel.company.value.id
        viewModelScope.launch {
            useCases.getAllMyPaymentsEspeceByDate(id!!,date, finDate)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _paymentsEspeceByDate.value = it.map { roomPayment -> roomPayment.toPaymentForProvidersWithCommandLine() }
                }
        }
    }

    fun getAllMyPaymentFromInvoicee(status : PaymentStatus){
        viewModelScope.launch{
            when(status){
                PaymentStatus.NOT_PAID -> {
                    useCases.getNotPaidInvoice(company.id!!)
                        .distinctUntilChanged()
                        .cachedIn(viewModelScope)
                        .collect{
                            _myAllBuyHistory.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                        }

                }
                PaymentStatus.PAID -> {
                    useCases.getPaidInvoice(company.id!!)
                        .distinctUntilChanged()
                        .cachedIn(viewModelScope)
                        .collect{
                            _myAllBuyHistory.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                        }

                }
                PaymentStatus.INCOMPLETE -> {
                    useCases.getInCompleteInvoice(company.id!!)
                        .distinctUntilChanged()
                        .cachedIn(viewModelScope)
                        .collect{
                            _inComplete.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                        }

                }

                PaymentStatus.ALL -> {
                    useCases.getAllInvoices(company.id!!)
                        .distinctUntilChanged()
                        .cachedIn(viewModelScope)
                        .collect{
                            _myAllBuyHistory.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                        }
                }
            }

        }
    }

    fun getAllMyPaymentNotAccepted(){
        viewModelScope.launch {
            useCases.getNotAcceptedInvoice(company.id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _myAllBuyHistory.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                }
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