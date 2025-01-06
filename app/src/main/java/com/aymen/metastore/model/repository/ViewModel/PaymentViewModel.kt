package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.dao.PaymentDao
import com.aymen.metastore.model.entity.dto.CashDto
import com.aymen.metastore.model.entity.dto.PaymentDto
import com.aymen.metastore.model.entity.model.CashModel
import com.aymen.metastore.model.entity.model.Payment
import com.aymen.metastore.model.entity.model.PaymentForProviders
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import retrofit2.Response
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
  private val _paymentsEspeceByDate : MutableStateFlow<PagingData<PaymentForProviders>> = MutableStateFlow(PagingData.empty())
    val paymentsEspeceByDate: StateFlow<PagingData<PaymentForProviders>> get() = _paymentsEspeceByDate
    private val _paymentHistoric : MutableStateFlow<PagingData<Payment>> = MutableStateFlow(PagingData.empty())
    val paymentHistoric : StateFlow<PagingData<Payment>> get() = _paymentHistoric

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

    fun sendRaglement(companyId : Long , cash : CashModel){
        viewModelScope.launch {
            val result : Result<Response<PaymentDto>> = runCatching {
                repository.sendRaglement(companyId,cash.toCashDto())
            }
        }
    }

    fun getPaymentHystoricByInvoiceId(invoiceId : Long){
        viewModelScope.launch {
            Log.e("paymenhystoric","fun called")

            useCases.getPaymentHystoricByInvoiceId(invoiceId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _paymentHistoric.value = it
                }
        }
    }

}