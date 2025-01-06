package com.aymen.metastore.model.repository.ViewModel

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.withTransaction
import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.SearchPaymentEnum
import com.aymen.metastore.model.entity.dto.CommandLineDto
import com.aymen.metastore.model.entity.dto.InvoiceDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.InvoiceRemoteKeysEntity
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.util.BarcodeScanner
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import com.aymen.store.ui.screen.user.generatePDF
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
     private  val sharedViewModel : SharedViewModel,
    private val useCases: MetaUseCases,
    private val barcodeScanner: BarcodeScanner,
     private val context : Context
) : ViewModel(){

    private val invoiceDao = room.invoiceDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val commandLineDao = room.commandLineDao()

    val listState = LazyListState()
    private val _ordersLine : MutableStateFlow<PagingData<PurchaseOrderLine>> = MutableStateFlow(PagingData.empty())
    val ordersLine: StateFlow<PagingData<PurchaseOrderLine>> get() = _ordersLine
    private val _commandLineInvoice : MutableStateFlow<PagingData<CommandLine>> = MutableStateFlow(PagingData.empty())
    val commandLineInvoice: StateFlow<PagingData<CommandLine>> get() = _commandLineInvoice
    private val _invoiceForSerch : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val invoiceForSearch : StateFlow<PagingData<Invoice>> = _invoiceForSerch
    private val _allMyInvoiceNotAccepted : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val allMyInvoiceNotAccepted: StateFlow<PagingData<Invoice>> get() = _allMyInvoiceNotAccepted
    var invoice by mutableStateOf(Invoice())
    var lastInvoiceCode by mutableLongStateOf(0)
    var clientCompany by mutableStateOf(Company())
    var clientUser by mutableStateOf(User())
    var clientType by mutableStateOf(AccountType.USER)
    var article by mutableStateOf(ArticleCompany())
    var commandLineDto by mutableStateOf(CommandLine())
    private val _commandLineDtos :  MutableStateFlow<List<CommandLine>> = MutableStateFlow(emptyList())
    val commandLine : StateFlow<List<CommandLine>> get() = _commandLineDtos
    var discount by mutableDoubleStateOf(0.0)
    val company by mutableStateOf(sharedViewModel.company.value)
    var invoiceMode by mutableStateOf(InvoiceMode.CREATE)
    var invoiceType by mutableStateOf(InvoiceDetailsType.ORDER_LINE)
    private val _notAcceptedAsProvider : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val notAcceptedAsProvider: StateFlow<PagingData<Invoice>> get() = _notAcceptedAsProvider
    private val _notAcceptedAsClient : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val notAcceptedAsClient: StateFlow<PagingData<Invoice>> get() = _notAcceptedAsClient
    fun substructCommandsLine(){
        _commandLineDtos.value = _commandLineDtos.value.minus(commandLineDto)
    }
    fun addCommandLine(commandLine: CommandLine){
        _commandLineDtos.value = _commandLineDtos.value.plus(commandLine)
    }

    fun startScan(onScan : (ArticleCompany?) ->Unit){
        viewModelScope.launch(Dispatchers.Main) {
            barcodeScanner.startScan().toString().also {
               try {
                        val article = repository.getArticleByBarcode(it)
                   onScan(article.body()?.toArticleCompanyModel())
               }catch (ex : Exception){
                   onScan(null)
                   Log.e("scanner","there is no article")
               }

           }
        }
    }

    fun remiseOrderLineToZero(){
        _commandLineInvoice.value = PagingData.empty()
        _ordersLine.value = PagingData.empty()
        clientCompany = Company()
        clientUser = User()
        invoice = Invoice()
        _commandLineDtos.value = emptyList()
        discount = 0.0
    }

    private val _filter = MutableStateFlow(PaymentStatus.ALL)
    val filter : StateFlow<PaymentStatus> = _filter

    val invoices = _filter.flatMapLatest { filter->
        useCases.getAllInvoices(company.id!!,true,filter)
            .cachedIn(viewModelScope)
    }

    val invoicesAsClient = _filter.flatMapLatest {filter->
        val id = if(sharedViewModel.accountType.value == AccountType.USER) sharedViewModel.user.value.id else sharedViewModel.company.value.id
        useCases.getAllInvoicesAsClient(id!!, sharedViewModel.accountType.value, filter)
            .cachedIn(viewModelScope)
    }

    fun setFilter(filter : PaymentStatus){
        _filter.value = filter
    }

    private val _statusFilter = MutableStateFlow(Status.INWAITING)
    val statusFilter : StateFlow<Status> = _statusFilter

    val invoicesNotAccepted = _statusFilter.flatMapLatest { filter ->
        useCases.getAllInvoicesAsClientAndStatus(sharedViewModel.user.value.id!!, filter)
            .cachedIn(viewModelScope)

    }
    fun getAllMyPaymentNotAccepted(isProvider : Boolean){
        viewModelScope.launch {
            useCases.getNotAcceptedInvoice(company.id!!,isProvider, Status.INWAITING)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    if(isProvider) _notAcceptedAsProvider.value = it
                    else _notAcceptedAsClient.value = it
                }
        }
    }
    init {

        Log.e("testtoviewmodel","invoice view model")
        viewModelScope.launch {

        sharedViewModel.accountType.collect {type ->
        when(type){
            AccountType.COMPANY -> {
            }
            AccountType.USER -> {
                getAllMyInvoiceAsClientAndStatus(Status.INWAITING,sharedViewModel.user.value.id?:0)
            }
            AccountType.META -> {}
            AccountType.NULL -> {}
            AccountType.SELLER -> {}
        }
        }

        }
    }

    fun getAllMyInvoiceAsClientAndStatus(status : Status, id : Long){
        viewModelScope.launch {
            Log.e("laucnhcrd","launched")
                useCases.getAllInvoicesAsClientAndStatus(id, status)
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collect {
                        _allMyInvoiceNotAccepted.value =
                            it
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
            val lastInvoiceRemoteKey = invoiceDao.getLatestInvoiceRemoteKey()
            val id = if (lastInvoiceRemoteKey == null) 1 else lastInvoiceRemoteKey.id + 1
            val invoiceCount = invoiceDao.getInvoiceCountBySource(true)
            val page = invoiceCount.div(PAGE_SIZE)
            val remain = invoiceCount % PAGE_SIZE
            val invoiceRemoteKey = InvoiceRemoteKeysEntity(
                id = id,
                prevPage = if (page == 0) null else page - 1,
                nextPage = if (remain == 0) page + 1 else null
            )
            val invoice = Invoice(
                id = id,
                code = lastInvoiceCode,
                tot_tva_invoice = commandLine.value[0].invoice?.tot_tva_invoice ?: 0.0,
                prix_invoice_tot = commandLine.value[0].invoice?.prix_invoice_tot ?: 0.0,
                prix_article_tot = commandLine.value[0].invoice?.prix_article_tot ?: 0.0,
                discount = discount,
                status = commandLine.value[0].invoice?.status,
                paid = commandLine.value[0].invoice?.paid,
                type = InvoiceDetailsType.COMMAND_LINE,
                rest = commandLine.value[0].invoice?.rest ?: 0.0,
                person = clientUser,
                client = clientCompany,
                provider = company,
                isInvoice = true
            )

            room.withTransaction {
                userDao.insertUser(listOf(clientUser.toUserEntity()))
                companyDao.insertCompany(listOf(clientCompany.toCompanyEntity()))
                invoiceDao.insertKeys(listOf(invoiceRemoteKey))
                invoiceDao.insertInvoice(listOf(invoice.toInvoiceEntity()))

                commandLine.value.map { line ->
                    commandLineDao.insertCommandLine(listOf(line.toCommandLineEntity()))
                }
            }
                val result : Result<Response<List<CommandLineDto>>> = runCatching {
                    (clientCompany.id ?: clientUser.id)?.let {
                        repository.addInvoice(
                            commandLineDtos = commandLine.value,
                            clientId = it,
                            invoiceCode = lastInvoiceCode,
                            discount = discount,
                            clientType = clientType,
                            invoiceMode = invoiceMode
                        )
                    }!!
                }
            result.fold(
                onSuccess = {success ->
                    if(success.isSuccessful){
                        val response = success.body()
                        if(response != null) {
                            room.withTransaction {
                                invoiceDao.deleteInvoiceById(id)
                                invoiceDao.deleteInvoiceRemoteKeyById(id)
                                commandLine.value.map { line ->
                                    commandLineDao.deleteCommandLineById(line.id!!)
                                }
                                invoiceDao.insertInvoice(listOf(response[0].invoice?.toInvoice(true)))
                                    invoiceDao.insertKeys(listOf(invoiceRemoteKey.copy(id = response[0].invoice?.id!!)))
                                response.map {res ->
                                    commandLineDao.insertCommandLine(listOf(res.toCommandLine()))
                                }
                            }
                        }
                    }
                },
                onFailure = { failure ->

                }
            )
        }

    }



    fun getInvoiceDetails(){
        val id = if(sharedViewModel.accountType.value == AccountType.USER) sharedViewModel.user.value.id else sharedViewModel.company.value.id
        viewModelScope.launch{
            when(invoiceType){
                InvoiceDetailsType.COMMAND_LINE -> {
                    useCases.getAllCommandLineByInvoiceId(id!!, invoice.id!!)
                        .distinctUntilChanged()
                        .cachedIn(viewModelScope)
                        .collect{
                            _commandLineInvoice.value = it
                        }
                }
                InvoiceDetailsType.ORDER_LINE -> {
                    _commandLineInvoice.value = PagingData.empty()
                    useCases.getAllOrdersLineByInvoiceId(id!! ,invoice.id!!)
                        .distinctUntilChanged()
                        .cachedIn(viewModelScope)
                        .collect{
                            _ordersLine.value = it
                        }
                }
            }

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

    fun searchInvoice(type : SearchPaymentEnum, text : String){
        viewModelScope.launch {
            useCases.searchInvoice(type, text, sharedViewModel.company.value.id?:0)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _invoiceForSerch.value = it
                }
        }
    }



}