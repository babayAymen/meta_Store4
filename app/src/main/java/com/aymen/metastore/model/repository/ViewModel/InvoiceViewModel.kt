package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.entity.Dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapCategoryToRoomCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapCommandLineToRoomCommandLine
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapInvoiceToRoomInvoice
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderLineToRoomPurchaseOrderLine
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderToRoomPurchaseOrder
import com.aymen.metastore.model.entity.converterRealmToApi.mapSubCategoryToRoomSubCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.Invoice
import com.aymen.metastore.model.entity.room.User
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.converterRealmToApi.mapArticelDtoToRoomArticle
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRoomArticleCompany
import com.aymen.store.model.entity.dto.CommandLineDto
import com.aymen.store.model.entity.dto.InvoiceDto
import com.aymen.store.model.entity.dto.PurchaseOrderLineDto
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
     private  val sharedViewModel : SharedViewModel
) : ViewModel(){

    private val _myInvoicesAsProvider = MutableStateFlow<List<InvoiceWithClientPersonProvider>>(emptyList())
    val myInvoicesAsProvider : StateFlow<List<InvoiceWithClientPersonProvider>> = _myInvoicesAsProvider

    private val _myInvoicesAsClient = MutableStateFlow<List<InvoiceWithClientPersonProvider>>(emptyList())
    val myInvoicesAsClient: StateFlow<List<InvoiceWithClientPersonProvider>> = _myInvoicesAsClient

    private val _ordersLine = MutableStateFlow(PurchaseOrderLineDto())
    val ordersLine: StateFlow<PurchaseOrderLineDto> = _ordersLine


    private val _allMyInvoiceNotAccepted = MutableStateFlow<List<InvoiceWithClientPersonProvider>>(emptyList())
    val allMyInvoiceNotAccepted: StateFlow<List<InvoiceWithClientPersonProvider>> = _allMyInvoiceNotAccepted

    val _ordersLineArray = MutableStateFlow<List<PurchaseOrderLineDto>>(emptyList())
    val ordersLineArray: StateFlow<List<PurchaseOrderLineDto>> = _ordersLineArray


    var lastInvoiceCode by mutableLongStateOf(0)
    var clientCompany by mutableStateOf(Company())
    var clientUser by mutableStateOf(User())
    var clientType by mutableStateOf(AccountType.USER)
    var article by mutableStateOf(ArticleCompanyDto())
    var commandLineDto by mutableStateOf(CommandLineDto())
    var commandLineDtos by  mutableStateOf(emptyList<CommandLineDto>())
    var isLoading by  mutableStateOf(true)
    var discount by mutableDoubleStateOf(0.0)
    val company by mutableStateOf(sharedViewModel.company.value)
    var invoice by mutableStateOf(InvoiceWithClientPersonProvider
        (Invoice(),
        Company(),
        User(),
        Company()))
    var invoiceMode by mutableStateOf(InvoiceMode.CREATE)
    val accountType by mutableStateOf(sharedViewModel.accountType)
    val meAsUser by mutableStateOf(sharedViewModel.user.value)



    fun getAllMyInvoicesAsProvider(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllMyInvoicesAsProvider(company.id!!)
                if(response.isSuccessful){
                    response.body()?.forEach{invoice ->
                        insertInvoice(invoice)
                    }
                }
            }catch (ex: Exception){
                Log.e("aymenbabayinvoice","error is :$ex")
            }finally {
                    isLoading = false
            }
           _myInvoicesAsProvider.value = room.invoiceDao().getAllInvoicesAsProvider(company.id!!)

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
        invoice.provider?.user.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        room.companyDao().insertCompany(mapCompanyToRoomCompany(invoice.provider))
        room.invoiceDao().insertInvoice(mapInvoiceToRoomInvoice(invoice))
    }

    fun getAllMyInvoicesAsProviderAndPaymentStatus(status : PaymentStatus){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getAllMyInvoicesAsProviderAndStatus(company.id!!, status)
                if(response.isSuccessful){
                    response.body()?.forEach {invoice ->
                        insertInvoice(invoice)
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMyInvoicesAsProviderAndStatus","exception : ${ex.message}")
            }
            _myInvoicesAsProvider.value = room.invoiceDao().getAllInvoicesAsProviderAndPaymentStatus(company.id!! , status)
            Log.e("getAllMyInvoicesAsProviderAndPaymentStatus","status : $status , asprovider size : ${_myInvoicesAsProvider.value.size}")
        }
    }

    fun getAllMyInvoiceAsClientAndStatus(status : Status){
        viewModelScope.launch(Dispatchers.IO) {
            var id  : Long = 0
            try {
                if (sharedViewModel.accountType == AccountType.USER) {
                  id = sharedViewModel.user.value.id!!
                }
                if (sharedViewModel.accountType == AccountType.COMPANY) {
                    id = sharedViewModel.company.value.id!!
                }
                    val response = repository.getAllMyInvoicesAsClientAndStatus(id, status)
                Log.e("getallmyinvoiceasclientandstatus","response server size : ${response.body()?.size} id is : $id status is : $status")
                    if (response.isSuccessful) {
                        response.body()?.forEach { invoice ->
                            insertInvoice(invoice)
                        }
                    }
            }catch (ex : Exception){
                Log.e("getallmyinvoiceasclientandstatus","exception : ${ex.message}")
            }
            _allMyInvoiceNotAccepted.value = room.invoiceDao().getAllMyInvoicesAsClientAndStatus(id,status)
            _allMyInvoiceNotAccepted.value += room.invoiceDao().getAllMyInvoicesAsPersonAndStatus(id,status)
            Log.e("getallmyinvoiceasclientandstatus","response server size : ${_allMyInvoiceNotAccepted.value.size}")
        }
    }

    fun getAllMyInvoicesAsClient(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllMyInvoicesAsClient(sharedViewModel.company.value.id?:0)
                if(response.isSuccessful) {
                    Log.e("myinvoicesaccepted","response from view model size is !: ${response.body()?.size}")
                    response.body()?.forEach {invoice ->
                        insertInvoice(invoice)
                    }
                }
            }catch (ex: Exception){
                Log.e("getAllMyInvoicesAsClient","exception: ${ex.message}")

            }finally {
            isLoading = false
            }
            _myInvoicesAsClient.value = when (sharedViewModel.accountType){
                AccountType.USER ->
                    room.invoiceDao().getAllMyInvoicesAsPersonAndStatus(sharedViewModel.user.value.id!!, Status.ACCEPTED)
                AccountType.COMPANY ->
                room.invoiceDao().getAllMyInvoicesAsClientAndStatus(sharedViewModel.company.value.id!!, Status.ACCEPTED)
                else -> emptyList()
            }
            Log.e("myinvoicesaccepted","_myInvoicesAsClient from view model size is !: ${_myInvoicesAsClient.value.size}")
        }
    }
fun deleteAll(){
    _myInvoicesAsClient.value = emptyList()
    _myInvoicesAsProvider.value = emptyList()
    _allMyInvoiceNotAccepted.value = emptyList()
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
            isLoading = true
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getAllCommandLinesByInvoiceId(invoice.invoice.id!!)
                if(response.isSuccessful){
                    response.body()?.forEach {commandLine ->
                        commandLineDtos += commandLine
                        insertCommandLine(commandLine)
                    }
                        isLoading = false
                }
            }catch (ex : Exception){
                Log.e("getAllCommandLineByInvoiceId","exception: ${ex.message}")
            }
        }
    }

    @Transaction
    suspend fun insertCommandLine(commandLine : CommandLineDto){
        commandLine.invoice?.let {
        insertInvoice(it)
        }
        commandLine.article?.let {
            insertArticle(it)
        }
        room.commandLineDao().insertCommandLine(mapCommandLineToRoomCommandLine(commandLine))
    }

    @Transaction
    suspend fun insertArticle(article : ArticleCompanyDto){
        room.categoryDao().insertCategory(mapCategoryToRoomCategory(article.category!!))
        room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(article.subCategory!!))
        room.userDao().insertUser(mapUserToRoomUser(article.company?.user))
        article.provider?.user.let {
        room.userDao().insertUser(mapUserToRoomUser(article.provider?.user))
        }
        room.companyDao().insertCompany(mapCompanyToRoomCompany(article.provider))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(article.company))
        room.articleDao().insertArticle(mapArticelDtoToRoomArticle(article.article!!))
        room.articleCompanyDao().insertArticle(
            mapArticleCompanyToRoomArticleCompany(article)
        )
        Log.e("insertOrderLine","fin fun article")
    }

    fun getAllOrdersLineByInvoiceId(){
            isLoading = true
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getAllOrdersLineByInvoiceId(invoice.invoice.id!!)
                if(response.isSuccessful){
                    response.body()?.forEach {
                        _ordersLineArray.value += it
                        insert(it)
                    }
                        isLoading = false
                }
            }catch (ex : Exception){
                Log.e("getAllCommandLineByInvoiceId","exception: ${ex.message}")
            }
        }
    }

    @Transaction
    suspend fun insert(order : PurchaseOrderLineDto){
        order.purchaseorder?.let {
            it.client?.let { client ->
                room.userDao().insertUser(mapUserToRoomUser(client.user))
                room.companyDao().insertCompany(mapCompanyToRoomCompany(client))
            }
            it.person?.let { person ->
                room.userDao().insertUser(mapUserToRoomUser(person))
            }
            it.company?.let { company ->
                room.userDao().insertUser(mapUserToRoomUser(company.user))
                room.companyDao().insertCompany(mapCompanyToRoomCompany(company))
            }
        }
        order.invoice?.let {
            insertInvoice(it)
        }
            insertArticle(order.article!!)
        room.purchaseOrderDao().insertOrder(mapPurchaseOrderToRoomPurchaseOrder(order.purchaseorder!!))
        room.purchaseOrderLineDao().insertOrderLine(
            mapPurchaseOrderLineToRoomPurchaseOrderLine(order)
        )
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