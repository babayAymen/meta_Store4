package com.aymen.metastore.model.repository.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.dao.PaymentDao
import com.aymen.metastore.model.entity.dto.CashDto
import com.aymen.metastore.model.entity.dto.PaymentDto
import com.aymen.metastore.model.entity.model.CashModel
import com.aymen.metastore.model.entity.model.ErrorResponse
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.Payment
import com.aymen.metastore.model.entity.model.PaymentForProviders
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.CategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PaymentRemoteKeys
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val sharedViewModel: SharedViewModel,
    private val useCases: MetaUseCases,
    private val context : Context
):ViewModel() {

    private val paymentDao = room.paymentDao()
    private val invoiceDao = room.invoiceDao()
    val company by mutableStateOf(sharedViewModel.company.value)
    val user by mutableStateOf(sharedViewModel.user.value)
    val type by mutableStateOf(sharedViewModel.accountType)
  private val _paymentsEspeceByDate : MutableStateFlow<PagingData<PaymentForProviders>> = MutableStateFlow(PagingData.empty())
    val paymentsEspeceByDate: StateFlow<PagingData<PaymentForProviders>> get() = _paymentsEspeceByDate
    private val _paymentHistoric : MutableStateFlow<PagingData<Payment>> = MutableStateFlow(PagingData.empty())
    val paymentHistoric : StateFlow<PagingData<Payment>> get() = _paymentHistoric

    fun setPaymentHistoric(){
        _paymentHistoric.value = PagingData.empty()
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

    fun sendRaglement(companyId : Long , cash : CashModel, onStatus : (Invoice) -> Unit){
        viewModelScope.launch {
            val latestPaymentRemoteKey = paymentDao.getLatestPaymentRemoteKey()
            val id = if(latestPaymentRemoteKey != null) latestPaymentRemoteKey.id!! + 1 else 1
            val paymentCount = if(latestPaymentRemoteKey != null) paymentDao.getPaymentRemoteKeysCount() else 0
            val page = paymentCount.div(PAGE_SIZE)
            val remain = paymentCount% PAGE_SIZE
            val remoteKey = PaymentRemoteKeys(
                id = id,
                prevPage = if(page == 0)null else page-1,
                nextPage = if(remain!=0) null else page+1
            )
            val rest = BigDecimal(cash.invoice?.rest!!).subtract(BigDecimal(cash.amount!!))
            if(rest.compareTo(BigDecimal(0)) > 0){
                cash.invoice?.rest = rest.toDouble()
            }else{
                cash.invoice?.paid = PaymentStatus.PAID
                cash.invoice?.rest = 0.0
            }
            onStatus(cash.invoice!!)
            room.withTransaction {
                invoiceDao.updateInvoicePaidAndRest(cash.invoice?.id!!, cash.invoice?.paid!!, cash.invoice?.rest!!)
                paymentDao.insertKeys(listOf(remoteKey))
                paymentDao.insertPayment(listOf(cash.toPaymentEntity()))
            }
            val result : Result<Response<PaymentDto>> = runCatching {
                repository.sendRaglement(companyId,cash.toCashDto())
            }
            result.fold(
                onSuccess = {success ->
                    val response = success.body()
                    if(success.isSuccessful) {
                        if (response != null) {
                            room.withTransaction {
                                Log.e("responseda","response : $response")
                                invoiceDao.updateInvoicePaidAndRest(response.invoice?.id!!,response.invoice.paid!!, response.invoice.rest!!)
                                paymentDao.deletePaymentById(id)
                                paymentDao.clearAllRemoteKeysTableById(id)
                                paymentDao.insertKeys(listOf(remoteKey.copy(id = response.id)))
                                paymentDao.insertPayment(listOf(response.toPayment()))

                            }
                        }
                    }else{
                        room.withTransaction {
                            invoiceDao.updateInvoicePaidAndRest(response?.invoice?.id!!,response.invoice.paid!!, response.invoice.rest!!)
                            paymentDao.deletePaymentById(id)
                            paymentDao.clearAllRemoteKeysTableById(id)
                        }
                        val errorBodyString = success.errorBody()?.string()
                        errorBlock(errorBodyString)
                    }
                },
                onFailure = {failure ->

                }
            )
        }
    }

    fun getPaymentHystoricByInvoiceId(invoiceId : Long){
        Log.e("veftiondazncj","get payment called")
        viewModelScope.launch {
            Log.e("veftiondazncj","get payment called invoice id $invoiceId")
            useCases.getPaymentHystoricByInvoiceId(invoiceId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _paymentHistoric.value = it
                }
        }
    }
    private fun errorBlock(error : String?){
        viewModelScope.launch{
            val re = Gson().fromJson(error, ErrorResponse::class.java)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "error : ${re.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}