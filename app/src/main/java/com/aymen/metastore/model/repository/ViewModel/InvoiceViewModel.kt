package com.aymen.metastore.model.repository.ViewModel

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.aymen.metastore.util.MY_NOT_DELIVERED
import com.aymen.metastore.util.NOT_DELIVERED
import com.aymen.metastore.util.ORDER_LINE
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
import kotlinx.coroutines.withContext
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
    private val appViewModel: AppViewModel,
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
    private val _invoice : MutableStateFlow<Invoice> = MutableStateFlow(Invoice())
    val invoice :StateFlow<Invoice> = _invoice
    private val _purchaseOrder : MutableStateFlow<PurchaseOrder> = MutableStateFlow(PurchaseOrder())
    val purchaseOrder : StateFlow<PurchaseOrder> = _purchaseOrder
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
    val company : StateFlow<Company> = sharedViewModel.company
    val user : StateFlow<User> = sharedViewModel.user
    val accountType : StateFlow<AccountType> = sharedViewModel.accountType
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
               }

           }
        }
    }

    fun setInvoice(invoice : Invoice){
        _invoice.value = invoice
    }
    fun remiseOrderLineToZero(){
        _commandLineInvoice.value = PagingData.empty()
        _ordersLine.value = PagingData.empty()
        clientCompany = Company()
        clientUser = User()
        _invoice.value = Invoice()
        _commandLines.value = emptyList()
        discount = 0.0
    }

    fun remiseCommandLineToZero(){
        _commandLineInvoice.value = PagingData.empty()
    }
    private val _filter = MutableStateFlow(PaymentStatus.ALL)
    val filter : StateFlow<PaymentStatus> = _filter

    val invoices = _filter.flatMapLatest { filter->
        useCases.getAllInvoices(company.value.id?:0,true,filter)
            .cachedIn(viewModelScope)
    }


    val invoicesAsClient = _filter.flatMapLatest {filter->
        val id = if(accountType.value == AccountType.USER) user.value.id else company.value.id
        useCases.getAllInvoicesAsClient(id!!, accountType.value, filter)
            .cachedIn(viewModelScope)
    }

    fun setFilter(filter : PaymentStatus){
        _filter.value = filter
    }

    private val _statusFilter = MutableStateFlow(Status.INWAITING)
    val statusFilter : StateFlow<Status> = _statusFilter

    val invoicesNotAccepted = _statusFilter.flatMapLatest { filter ->
        useCases.getAllInvoicesAsClientAndStatus(user.value.id!!, filter)
            .cachedIn(viewModelScope)

    }
    fun getAllMyPaymentNotAccepted(isProvider : Boolean){
        viewModelScope.launch {
            useCases.getNotAcceptedInvoice(company.value.id!!,isProvider, Status.INWAITING)
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
            AccountType.COMPANY -> {}
            AccountType.USER -> {}
            AccountType.META -> {}
            AccountType.NULL -> {}
            AccountType.SELLER -> {}
            AccountType.DELIVERY -> {
                getAllOrdersNotAcceptedAsDelivery(user.value.id?:0)
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
    //should tets the bottom fun is not copmlete
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
                    provider = company.value,
                    isInvoice = true,
                    asProvider = asProvider
                )
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
            invoicee.client = if(clientCompany.id != null) clientCompany else null
            invoicee.person = if(clientUser.id != null) clientUser else null
            _commandLines.value[0].invoice = invoicee
            Log.e("testinvoice","client user id before add in voice ${clientUser.id} and command line client ${commandLine.value[0].invoice}")

            val result : Result<Response<List<CommandLineDto>>> = runCatching {
                        repository.addInvoice(
                            commandLineDtos = commandLine.value,
                            clientId = 0L,
                            invoiceCode = lastInvoiceCode,
                            discount = discount,
                            clientType = clientType,
                            invoiceMode = invoiceMode,
                            asProvider = asProvider
                        )
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
    fun setPurchaseOrder(order : PurchaseOrder){
        _purchaseOrder.value = order
    }
    fun getInvoiceDetails(){
        val id = if(accountType.value == AccountType.COMPANY) company.value.id else user.value.id
        viewModelScope.launch{
            when(invoiceType){
                InvoiceDetailsType.COMMAND_LINE -> {
                    useCases.getAllCommandLineByInvoiceId(id!!, invoice.value.id!!)
                        .distinctUntilChanged()
                        .cachedIn(viewModelScope)
                        .collect{
                            _commandLineInvoice.value = it
                        }
                }
                InvoiceDetailsType.ORDER_LINE -> {
                    _commandLineInvoice.value = PagingData.empty()
                    useCases.getAllOrdersLineByInvoiceId(id!! ,purchaseOrder.value.id?:invoice.value.purchaseOrder?.id?:0L)
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
            useCases.searchInvoice(type, text, company.value.id?:0)
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
    private val _myOrdersNotDelivered : MutableStateFlow<List<PurchaseOrder>> = MutableStateFlow(emptyList())
    val myOrdersNotDelivered : StateFlow<List<PurchaseOrder>> = _myOrdersNotDelivered

    fun setMyOrdersNotDelivered(order : PurchaseOrder){
        _myOrdersNotDelivered.value += order
        val userLatitude = user.value.latitude
        val userLongitude = user.value.longitude
        val sortedOrders = _myOrdersNotDelivered.value.sortedBy { myOrder ->
            val latitude = myOrder.client?.latitude ?: myOrder.person?.latitude ?: 0.0
            val longitude = myOrder.client?.longitude ?: myOrder.person?.longitude ?: 0.0
            calculateDistance(userLatitude!!, userLongitude!!, latitude, longitude)
        }

        _myOrdersNotDelivered.value = sortedOrders
        sortedOrders.map {
        }
    }
    fun calculateDistance(
        userLatitude: Double,
        userLongitude: Double,
        placeLatitude: Double,
        placeLongitude: Double
    ): Float {
        val result = FloatArray(1)
        Location.distanceBetween(userLatitude, userLongitude, placeLatitude, placeLongitude, result)
        return result[0]
    }

    fun acceptInvoiceAsDelivery(){
        viewModelScope.launch {
            val result : Result<Response<Boolean>> = runCatching {
                repository.acceptInvoiceAsDelivery(purchaseOrder.value.id!!)
            }
            result.fold(
                onSuccess = { success ->
                    appViewModel.updateView(NOT_DELIVERED)
                    appViewModel.updateShow(ORDER_LINE)
                    purchaseOrderDao.makeInvoiceAsTeken( id = purchaseOrder.value.id!!)
                },
                onFailure = { failure ->
                    Log.e("fold","failure")
                }
            )
        }
    }
    fun userRejectOrder(){
        viewModelScope.launch {
            repository.userRejectOrder(purchaseOrder.value.id!!)
            appViewModel.updateView(MY_NOT_DELIVERED)
            appViewModel.updateShow(ORDER_LINE)
        }
    }
    fun submitOrderDelivered(code : String){
        viewModelScope.launch(Dispatchers.IO) {
            val result : Result<Response<Boolean>> = runCatching {
                repository.submitOrderDelivered(purchaseOrder.value.id!!, code)
            }
            result.fold(
                onSuccess = {success ->
                    if(success.isSuccessful){
                        val response = success.body()
                        if(response != null) {
                            subtractOrder(purchaseOrder.value.id!!)
                            purchaseOrderDao.updateDeliveryStatus(purchaseOrder.value.id!!)
                            appViewModel.updateView(MY_NOT_DELIVERED)
                            appViewModel.updateShow(ORDER_LINE)
                        }else
                            withContext(Dispatchers.Main){
                            Toast.makeText(context, "you use the wrong code", Toast.LENGTH_LONG).show()
                            }
                    }
                },
                onFailure = {failure ->
                    Log.e("fold","failure")
                }
            )
        }
    }
    private fun subtractOrder(order : Long){
        _myOrdersNotDelivered.value = _myOrdersNotDelivered.value.filter { it.id != order }
    }
    fun setNeccessry(order: PurchaseOrder, clientType : AccountType,invoiceMode: InvoiceMode, invoiceDetailsType: InvoiceDetailsType){
        _purchaseOrder.value = order
        this.clientType = clientType
        discount = order.discount ?: 0.0
        _invoiceMode.value = invoiceMode
        invoiceType = invoiceDetailsType
        invoice.value.provider = order.company
        invoice.value.createdDate = order.createdDate
        invoice.value.prix_invoice_tot = order.prix_order_tot
        invoice.value.prix_article_tot = order.prix_article_tot
        invoice.value.tot_tva_invoice = order.tot_tva
        invoice.value.type = invoiceDetailsType
        invoice.value.client = order.client
        invoice.value.person = order.person
        invoice.value.code = order.orderNumber
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


    fun navigateToGoogleMaps(context: Context, endLat: Double, endLng: Double) {
        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=My+Location&destination=$endLat,$endLng&travelmode=driving")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps") // Ensures it opens in Google Maps

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // If Google Maps is not installed, open in a browser
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(browserIntent)
        }
    }


    fun navigateOptimizedRoute(
        context: Context,
        endLat: Double,
        endLng: Double
    ) {
        val waypoints = myOrdersNotDelivered.value.map {
            Pair(it.client?.latitude ?: it.person?.latitude, it.client?.longitude ?: it.person?.longitude)
        }
        val baseUri = "https://www.google.com/maps/dir/?api=1&origin=My+Location&destination=$endLat,$endLng&travelmode=driving"
        val waypointsString = waypoints.joinToString("|") { "${it.first},${it.second}" }

        val uri = if (waypointsString.isNotEmpty()) {
            Uri.parse("$baseUri&waypoints=$waypointsString")
        } else {
            Uri.parse(baseUri)
        }

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Open in a browser if Google Maps is not installed
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(browserIntent)
        }
    }

}









