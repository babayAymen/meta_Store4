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
import androidx.room.withTransaction
import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.SearchPaymentEnum
import com.aymen.metastore.model.entity.dto.CommandLineDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.PurchaseOrder
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode
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
    private val purchaseOrderDao = room.purchaseOrderDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val commandLineDao = room.commandLineDao()

    private val _invoiceMode : MutableStateFlow<InvoiceMode> = MutableStateFlow(InvoiceMode.CREATE)
    val invoiceModeState : StateFlow<InvoiceMode> = _invoiceMode
    val listState = LazyListState()
    private val _ordersLine : MutableStateFlow<PagingData<PurchaseOrderLine>> = MutableStateFlow(PagingData.empty())
    val ordersLine: StateFlow<PagingData<PurchaseOrderLine>> get() = _ordersLine
    private val _commandLineInvoice : MutableStateFlow<PagingData<CommandLine>> = MutableStateFlow(PagingData.empty())
    val commandLineInvoice: StateFlow<PagingData<CommandLine>> get() = _commandLineInvoice
    private val _invoiceForSerch : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val invoiceForSearch : StateFlow<PagingData<Invoice>> = _invoiceForSerch
    var invoice by mutableStateOf(Invoice())
    var purchaseOrder by mutableStateOf(PurchaseOrder())
    var lastInvoiceCode by mutableLongStateOf(0)
    var clientCompany by mutableStateOf(Company())
    private val _providerCompany : MutableStateFlow<Company> = MutableStateFlow(Company())
    val providerCompany : StateFlow<Company> = _providerCompany
    var clientUser by mutableStateOf(User())
    var clientType by mutableStateOf(AccountType.USER)
    var article by mutableStateOf(ArticleCompany())
    var commandLineDto by mutableStateOf(CommandLine())
    var asProvider by mutableStateOf(true)
    private val _commandLines :  MutableStateFlow<List<CommandLine>> = MutableStateFlow(emptyList())
    val commandLine : StateFlow<List<CommandLine>> get() = _commandLines
    var discount by mutableDoubleStateOf(0.0)
    val company by mutableStateOf(sharedViewModel.company.value)
//    var invoiceMode by mutableStateOf(InvoiceMode.CREATE)
    var invoiceType by mutableStateOf(InvoiceDetailsType.ORDER_LINE)
    private val _notAcceptedAsProvider : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val notAcceptedAsProvider: StateFlow<PagingData<Invoice>> get() = _notAcceptedAsProvider
    private val _notAcceptedAsClient : MutableStateFlow<PagingData<Invoice>> = MutableStateFlow(PagingData.empty())
    val notAcceptedAsClient: StateFlow<PagingData<Invoice>> get() = _notAcceptedAsClient
    private val _invoicesNotDelivered : MutableStateFlow<PagingData<PurchaseOrder>> = MutableStateFlow(PagingData.empty())
    val invoicesNotDelivered : StateFlow<PagingData<PurchaseOrder>> = _invoicesNotDelivered
    private val _invoicesIDelivered : MutableStateFlow<PagingData<PurchaseOrder>> = MutableStateFlow(PagingData.empty())
    val invoicesIDelivered : StateFlow<PagingData<PurchaseOrder>> = _invoicesIDelivered

    fun setProviderCompany(provider : Company){
        _providerCompany.value = provider
    }

    fun substructCommandsLine(){
        _commandLines.value = _commandLines.value.minus(commandLineDto)
    }
    fun addCommandLine(commandLine: CommandLine){
        _commandLines.value = _commandLines.value.plus(commandLine)
    }

    fun setInvoiceMode(invoiceMode : InvoiceMode){
        Log.e("textinvoice","invoice mode from set $invoiceMode")
        _invoiceMode.value = invoiceMode
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
        _commandLines.value = emptyList()
        discount = 0.0
    }

    fun remiseCommandLineToZero(){
        _commandLineInvoice.value = PagingData.empty()
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
        viewModelScope.launch {

        sharedViewModel.accountType.collect {type ->
        when(type){
            AccountType.COMPANY -> {
            }
            AccountType.USER -> {
            }
            AccountType.META -> {}
            AccountType.NULL -> {}
            AccountType.SELLER -> {}
            AccountType.DELIVERY -> {
                getAllOrdersNotAcceptedAsDelivery(sharedViewModel.user.value.id?:0)
            }
        }
        }

        }
    }

    private fun getAllOrdersNotAcceptedAsDelivery(id : Long){
        viewModelScope.launch {
            useCases.getAllOrdersNotDelivered(id)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{invoice ->
                    Log.e("tetsinvoice","view model collect is not delivery $invoice")
                    _invoicesNotDelivered.value = invoice
                }
        }
    }

     fun getInvoicesIDelivered(){
        viewModelScope.launch {
            useCases.getInvoicesIdelevered()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
            Log.e("tetsinvoice","view model collect isdelivery $it")
                    _invoicesIDelivered.value = it
                }
        }
    }

    fun getLastInvoiceCode(){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val invoiceCode = repository.getLastInvoiceCode(asProvider)
                if(invoiceCode.isSuccessful){
                    lastInvoiceCode = invoiceCode.body()!!
                }
            }catch (ex:Exception){
                Log.e("aymenabaylastinvoice","error is : $ex")
            }
            }
    }
    fun addInvoice(invoiceMode : InvoiceMode, asProvider : Boolean, invoicee : Invoice){
        viewModelScope.launch(Dispatchers.IO) {
            var id = 0L
            var invoiceRemoteKey = InvoiceRemoteKeysEntity(0,null,null)
            if(invoiceMode == InvoiceMode.CREATE) {
                val lastInvoiceRemoteKey = invoiceDao.getLatestInvoiceRemoteKey()
                 id = if (lastInvoiceRemoteKey == null) 1 else lastInvoiceRemoteKey.id + 1
                val invoiceCount = invoiceDao.getInvoiceCountBySource(true)
                val page = invoiceCount.div(PAGE_SIZE)
                val remain = invoiceCount % PAGE_SIZE
                 invoiceRemoteKey = InvoiceRemoteKeysEntity(
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
                    isInvoice = true,
                    asProvider = asProvider
                )

                Log.e("veftiondazncj", "before calling room ${commandLine.value[0].invoice}")
                room.withTransaction {
                    try {

                        userDao.insertUser(listOf(clientUser.toUserEntity()))
                        companyDao.insertCompany(listOf(clientCompany.toCompanyEntity()))
                        invoiceDao.insertKeys(listOf(invoiceRemoteKey))
                        invoiceDao.insertInvoice(listOf(invoice.toInvoiceEntity()))

                        commandLine.value.map { line ->
                            line.invoice = invoice
                            commandLineDao.insertCommandLine(listOf(line.toCommandLineEntity()))
                        }
                    } catch (ex: Exception) {
                        Log.e("veftiondazncj", "exception : $ex")
                    }
                }
            }
            Log.e("veftiondazncj","before calling repo")
            _commandLines.value[0].invoice = invoicee
            val result : Result<Response<List<CommandLineDto>>> = runCatching {
                    (clientCompany.id ?: clientUser.id)?.let {
                        repository.addInvoice(
                            commandLineDtos = commandLine.value,
                            clientId = it,
                            invoiceCode = lastInvoiceCode,
                            discount = discount,
                            clientType = clientType,
                            invoiceMode = invoiceMode,
                            asProvider = asProvider
                        )
                    }!!
                }
            result.fold(
                onSuccess = {success ->
                    Log.e("veftiondazncj","success")
                    if(success.isSuccessful){
                        val response = success.body()
                        if(response != null) {
                            room.withTransaction {

                                if(invoiceMode == InvoiceMode.CREATE) {
                                    invoiceDao.deleteInvoiceById(id)
                                    invoiceDao.deleteInvoiceRemoteKeyById(id)
                                    commandLine.value.map { line ->
                                       // commandLineDao.deleteCommandLineById(line.id!!)

                                    }
                                }
                                invoiceDao.insertInvoice(listOf(response[0].invoice?.toInvoice(true)))
                                    invoiceDao.insertKeys(listOf(invoiceRemoteKey.copy(id = response[0].invoice?.id!!)))
                                response.map {res ->
                                    commandLineDao.insertCommandLine(listOf(res.toCommandLine()))
                                }
                                        Log.e("veftiondazncj","before calling repo mta3 verification")
                            }
                        }
                    }
                },
                onFailure = { failure ->

                    Log.e("veftiondazncj", "Operation failed: ${failure.message}", failure)
                    Log.e("veftiondazncj","failure ${failure}")
                    Log.e("veftiondazncj","failure ${failure.cause}")
                    Log.e("veftiondazncj","failure ${failure.cause?.message}")
                    Log.e("veftiondazncj","failure ${failure.localizedMessage}")
                }
            )
        }

    }
    fun getInvoiceDetails(){
        val id = if(sharedViewModel.accountType.value == AccountType.COMPANY) sharedViewModel.company.value.id else sharedViewModel.user.value.id
        viewModelScope.launch{
            Log.e("tetsinvoice","invoice type : $invoiceType and my id : ${invoice.id}")
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
                    useCases.getAllOrdersLineByInvoiceId(id!! ,purchaseOrder.id?:invoice.purchaseOrder?.id?:0L)
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
                    if(status == Status.ACCEPTED)
                        room.invoiceDao().updateInvoiceStatus(invoiceId , status)
                    else
                        room.invoiceDao().deleteInvoiceById(invoiceId)
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
    fun deleteInvoiceById(invoiceId : Long){
        viewModelScope.launch {
            repository.deleteInvoiceById(invoiceId)
        }
    }
    fun deleteInvoiceByIdLocally(invoiceId : Long) {
        viewModelScope.launch {
            room.invoiceDao().deleteInvoiceById(invoiceId)
        }
    }
    fun acceptInvoiceAsDelivery(){
        viewModelScope.launch {
            val result : Result<Response<String>> = runCatching {
                repository.acceptInvoiceAsDelivery(purchaseOrder.id!!)
            }

            Log.e("fold","success id ${purchaseOrder.id}")
            result.fold(
                onSuccess = { success ->
                    Log.e("fold","success")
                    purchaseOrderDao.makeInvoiceAsTeken(isTaken = true, invoiceId = purchaseOrder.id!!)
                },
                onFailure = { failure ->
                    purchaseOrderDao.makeInvoiceAsTeken(isTaken = true, invoiceId = purchaseOrder.id!!)
                    Log.e("fold","failure")
                }
            )
        }
    }
    fun submitOrderDelivered(code : String){
        viewModelScope.launch {
            val result : Result<Response<String>> = runCatching {
                repository.submitOrderDelivered(purchaseOrder.id!!, code)
            }
        }
    }
    fun setNeccessry(order: PurchaseOrder, clientType : AccountType,invoiceMode: InvoiceMode, invoiceDetailsType: InvoiceDetailsType){
        purchaseOrder = order
        this.clientType = clientType
        discount = order.discount ?: 0.0
        _invoiceMode.value = invoiceMode
        invoiceType = invoiceDetailsType
        invoice.provider = order.company
        invoice.createdDate = order.createdDate
        invoice.prix_invoice_tot = order.prix_order_tot
        invoice.prix_article_tot = order.prix_article_tot
        invoice.tot_tva_invoice = order.tot_tva
        invoice.type = invoiceDetailsType
        invoice.client = order.client
        invoice.person = order.person
        invoice.code = order.orderNumber
    }

    fun calculateInvoiceDetails(onFinish : (BigDecimal,BigDecimal,BigDecimal) -> Unit) {
        val commandLine = commandLine.value
        var totalPrice = BigDecimal.ZERO
        var totalTax = BigDecimal.ZERO

        // Step 1: Initial Calculation
            val cent = BigDecimal(100.0)
        commandLine.forEach { line ->
            val articleSelling = BigDecimal(line.article?.sellingPrice!!)
            val lineDiscount = BigDecimal(line.discount?:0.0)
            val lineQte = BigDecimal(line.quantity)
            val discountedPrice = articleSelling.subtract(articleSelling.multiply(lineDiscount).divide(cent).setScale(2,RoundingMode.HALF_UP))
            val lineTotalPrice = discountedPrice.multiply(lineQte)
            totalPrice = totalPrice.add(lineTotalPrice)
            val articleTva = BigDecimal(line.article?.article?.tva?:0.0)
            val lineTax = lineTotalPrice.multiply(articleTva.divide(cent).setScale(2,RoundingMode.HALF_UP))
            totalTax = totalTax.add(lineTax)

            line.prixArticleTot = lineTotalPrice.toDouble()
            line.totTva = lineTax.toDouble()
        }

        // Total before applying the global discount
        val subtotal = totalPrice.add(totalTax)

        // Step 2: Apply Global Discount
        val globalDiscount = BigDecimal(discount)
        val discountAmount = subtotal.multiply(globalDiscount.divide(cent))
        val globalDiscountFactor = BigDecimal.ONE.subtract(globalDiscount.divide(cent))

        // Step 3: Recalculate Each Line with Global Discount
        totalPrice = BigDecimal.ZERO
        totalTax = BigDecimal.ZERO
        commandLine.forEach { line ->
            // Adjust the line total price with global discount
            val prixArticleTotal = BigDecimal(line.prixArticleTot)
            val adjustedLineTotal = prixArticleTotal.multiply(globalDiscountFactor)
            line.prixArticleTot = adjustedLineTotal.toDouble()

            // Recalculate VAT for the adjusted line total
            val articleTva = BigDecimal(line.article?.article?.tva?:0.0)
            val adjustedLineTax = adjustedLineTotal.multiply(articleTva.divide(cent).setScale(2,RoundingMode.HALF_UP))
            line.totTva = adjustedLineTax.toDouble()

            // Accumulate totals
            totalPrice = totalPrice.add(adjustedLineTotal)
            totalTax = totalTax.add(adjustedLineTax)
        }

        // Step 4: Final Invoice Total
        val invoiceTotal = totalPrice.add(totalTax)

        // Log or return values as needed
        onFinish(totalTax,totalPrice,invoiceTotal)
    }

}









