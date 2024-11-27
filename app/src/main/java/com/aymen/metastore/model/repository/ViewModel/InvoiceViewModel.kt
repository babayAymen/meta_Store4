package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.Transaction
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.entity.model.User
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
class InvoiceViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
     private  val sharedViewModel : SharedViewModel,
    private val useCases: MetaUseCases
) : ViewModel(){

    private val _myInvoicesAsProvider : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val myInvoicesAsProvider : StateFlow<PagingData<Invoice>> get() = _myInvoicesAsProvider

    private val _myInvoicesAsClient : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val myInvoicesAsClient: StateFlow<PagingData<Invoice>> get() = _myInvoicesAsClient

    private val _ordersLine : MutableStateFlow<PurchaseOrderLine> = MutableStateFlow(PurchaseOrderLine())
    val ordersLine: StateFlow<PurchaseOrderLine> get() = _ordersLine


    private val _allMyInvoiceNotAccepted : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val allMyInvoiceNotAccepted: StateFlow<PagingData<Invoice>> get() = _allMyInvoiceNotAccepted

    val _ordersLineArray : MutableStateFlow<List<PurchaseOrderLine>> = MutableStateFlow(emptyList())
    val ordersLineArray: StateFlow<List<PurchaseOrderLine>> = _ordersLineArray

    var invoice : Invoice = Invoice()

    var lastInvoiceCode by mutableLongStateOf(0)
    var clientCompany by mutableStateOf(Company())
    var clientUser by mutableStateOf(User())
    var clientType by mutableStateOf(AccountType.USER)
    var article by mutableStateOf(ArticleCompany())
    var commandLineDto by mutableStateOf(CommandLine())
    var commandLineDtos by  mutableStateOf(emptyList<CommandLine>())
    var isLoading by  mutableStateOf(true)
    var discount by mutableDoubleStateOf(0.0)
    val company by mutableStateOf(sharedViewModel.company.value)
    var invoiceMode by mutableStateOf(InvoiceMode.CREATE)

    init {
        when(sharedViewModel.accountType){
            AccountType.COMPANY -> {
                getAllMyInvoicesAsProvider()
                getAllMyInvoicesAsClient()
            }
            AccountType.USER -> {
                getAllMyInvoicesAsClient()
                getAllMyInvoiceAsClientAndStatus(Status.INWAITING)
            }
            AccountType.AYMEN -> TODO()
        }
    }

    fun getAllMyInvoicesAsProvider(){
        viewModelScope.launch {
            useCases.getAllInvoices(sharedViewModel.company.value.id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _myInvoicesAsProvider.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                }
        }
    }

    fun getAllMyInvoicesAsProviderAndPaymentStatus(status : PaymentStatus){
        viewModelScope.launch (Dispatchers.IO) {
        }
    }

    fun getAllMyInvoiceAsClientAndStatus(status : Status){
        viewModelScope.launch(Dispatchers.IO) {
            val id = when(sharedViewModel.accountType){
                AccountType.USER -> sharedViewModel.user.value.id
                AccountType.COMPANY -> sharedViewModel.company.value.id
                AccountType.AYMEN -> sharedViewModel.user.value.id
            }
            useCases.getAllInvoicesAsClientAndStatus(id!!,status)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _allMyInvoiceNotAccepted.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                }
        }
    }

    fun getAllMyInvoicesAsClient(){
        viewModelScope.launch(Dispatchers.IO) {
            val id = when(sharedViewModel.accountType) {
             AccountType.USER ->   sharedViewModel.user.value.id
             AccountType.COMPANY ->   sharedViewModel.company.value.id
             AccountType.AYMEN ->   sharedViewModel.user.value.id
            }
            useCases.getAllInvoicesAsClient(id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _myInvoicesAsClient.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                }
         }
    }

    fun getLastInvoiceCode(){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val invoiceCode = repository.getLastInvoiceCode()
                if(invoiceCode.isSuccessful){
                    lastInvoiceCode = invoiceCode.body()!!
                }
            }catch (ex:Exception){
                Log.e("aymenabaylastinvoice","error is : $ex")
            }
            }

    }

    fun addInvoice(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                (clientCompany.id?:clientUser.id)?.let {
                    repository.addInvoice(commandLineDtos = commandLineDtos, clientId = it, invoiceCode = lastInvoiceCode, discount = discount, clientType =  clientType, invoiceMode = invoiceMode) }
            }catch (ex : Exception){
                Log.e("aymenbabayerror","error is : $ex")
                }
            }

    }

    fun getAllCommandLineByInvoiceId(){
        viewModelScope.launch (Dispatchers.IO){

        }
    }


    fun getAllOrdersLineByInvoiceId(){
        viewModelScope.launch (Dispatchers.IO){

        }
    }

    fun accepteInvoice(invoiceId : Long , status : Status){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.accepteInvoice(invoiceId , status)
                if(response.isSuccessful){
                    room.invoiceDao().updateInvoiceStatus(invoiceId , status)
                    room.invoiceDao().updateInvoiceStatus(invoiceId , status)
                }
            }catch (ex : Exception){
                Log.e("accepteInvoice","exception : ${ex.message}")
            }
        }
    }




}