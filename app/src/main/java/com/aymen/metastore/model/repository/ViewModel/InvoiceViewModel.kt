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
import com.aymen.metastore.util.BarcodeScanner
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
    private val useCases: MetaUseCases,
    private val barcodeScanner: BarcodeScanner
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


//
//    private val _myAllBuyHistory : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
//    val myAllBuyHistory: StateFlow<PagingData<Invoice>> get() = _myAllBuyHistory

    private val _inComplete : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val inComplete: StateFlow<PagingData<Invoice>> get() = _inComplete

    private val _paid : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val paid: StateFlow<PagingData<Invoice>> get() = _paid

    private val _notPaid : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val notPaid: StateFlow<PagingData<Invoice>> get() = _notPaid

    private val _notAccepted : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val notAccepted: StateFlow<PagingData<Invoice>> get() = _notAccepted

    fun startScan(onScan : (ArticleCompany?) ->Unit){
        viewModelScope.launch(Dispatchers.Main) {
            barcodeScanner.startScan().toString().also {
               try {
                        val article = repository.getArticleByBarcode(it)
                   onScan(article.body()?.toArticleCompanyModel())
                        Log.e("scanner","article : ${article.body()!!} result $it")
               }catch (ex : Exception){
                   onScan(null)
                   Log.e("scanner","there is no article")
               }

           }
        }
    }


    fun testClientContaing(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = room.clientProviderRelationDao().testClientContaing()
            Log.e("aymenbabay","response from room ${response.size}")
        }
    }


    fun getAllMyPaymentFromInvoicee(status : PaymentStatus, isProvider : Boolean){
        viewModelScope.launch{
            when(status){
                PaymentStatus.NOT_PAID -> {
                    useCases.getNotPaidInvoice(company.id!!, isProvider)
                        .distinctUntilChanged()
                        .cachedIn(viewModelScope)
                        .collect{
                            _notPaid.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                        }

                }
                PaymentStatus.PAID -> {
                    useCases.getPaidInvoice(company.id!!,isProvider)
                        .distinctUntilChanged()
                        .cachedIn(viewModelScope)
                        .collect{
                            _paid.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                        }

                }
                PaymentStatus.INCOMPLETE -> {
                    useCases.getInCompleteInvoice(company.id!!,isProvider)
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
                            _myInvoicesAsProvider.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                        }
                }
            }

        }
    }

    fun getAllMyPaymentNotAccepted(isProvider : Boolean){
        viewModelScope.launch {
            useCases.getNotAcceptedInvoice(company.id!!,isProvider)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _notAccepted.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                }
        }
    }
    init {
        when(sharedViewModel.accountType){
            AccountType.COMPANY -> {
               // getAllMyInvoicesAsProvider()
                getAllMyPaymentFromInvoicee(PaymentStatus.ALL, true)
                getAllMyInvoicesAsClient(sharedViewModel.company.value.id!!, PaymentStatus.ALL)
            }
            AccountType.USER -> {
                getAllMyInvoicesAsClient(sharedViewModel.user.value.id!!, PaymentStatus.ALL)
                getAllMyInvoiceAsClientAndStatus(Status.INWAITING,sharedViewModel.user.value.id!!)
            }
            AccountType.AYMEN -> TODO()
            AccountType.NULL -> TODO()
        }
    }
//
//    fun getAllMyInvoicesAsProvider(){
//        viewModelScope.launch {
//            useCases.getAllInvoices(sharedViewModel.company.value.id!!)
//                .distinctUntilChanged()
//                .cachedIn(viewModelScope)
//                .collect{
//                    _myInvoicesAsProvider.value = it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
//                }
//        }
//    }


    fun getAllMyInvoiceAsClientAndStatus(status : Status, id : Long){
        viewModelScope.launch(Dispatchers.IO) {
                useCases.getAllInvoicesAsClientAndStatus(id, status)
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collect {
                        _allMyInvoiceNotAccepted.value =
                            it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                    }
        }
    }

    fun getAllMyInvoicesAsClient(id : Long, paymentStatus : PaymentStatus){
        viewModelScope.launch(Dispatchers.IO) {
                when(paymentStatus){
                    PaymentStatus.NOT_PAID -> TODO()
                    PaymentStatus.PAID -> TODO()
                    PaymentStatus.INCOMPLETE -> TODO()
                    PaymentStatus.ALL -> {
                        useCases.getAllInvoicesAsClient(id, sharedViewModel.accountType)
                            .distinctUntilChanged()
                            .cachedIn(viewModelScope)
                            .collect {
                                _myInvoicesAsClient.value =
                                    it.map { invoice -> invoice.toInvoiceWithClientPersonProvider() }
                            }
                    }
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