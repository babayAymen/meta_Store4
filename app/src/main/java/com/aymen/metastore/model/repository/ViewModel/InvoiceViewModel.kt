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
import com.aymen.metastore.model.entity.converterRealmToApi.mapCommandLineToCommandLineDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapCommandLineToRoomCommandLine
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapInvoiceToRoomInvoice
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderLineToRoomPurchaseOrderLine
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderToRoomPurchaseOrder
import com.aymen.metastore.model.entity.converterRealmToApi.mapSubCategoryToRoomSubCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.converterRealmToApi.mapArticelDtoToRoomArticle
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRoomArticleCompany
import com.aymen.store.model.entity.dto.CommandLineDto
import com.aymen.store.model.entity.dto.InvoiceDto
import com.aymen.store.model.entity.dto.PurchaseOrderDto
import com.aymen.store.model.entity.dto.PurchaseOrderLineDto
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val realm : Realm,
    private val room : AppDatabase,
    private val sharedViewModel : SharedViewModel
) : ViewModel(){

    private val _myInvoicesAsProvider = MutableStateFlow<List<Invoice>>(emptyList())
    val myInvoicesAsProvider : StateFlow<List<Invoice>> = _myInvoicesAsProvider

    private val _myInvoicesAsClient = MutableStateFlow<List<Invoice>>(emptyList())
    val myInvoicesAsClient: StateFlow<List<Invoice>> = _myInvoicesAsClient

    private val _ordersLine = MutableStateFlow(PurchaseOrderLineDto())
    val ordersLine: StateFlow<PurchaseOrderLineDto> = _ordersLine


    val _ordersLineArray = MutableStateFlow<List<PurchaseOrderLineDto>>(emptyList())
    val ordersLineArray: StateFlow<List<PurchaseOrderLineDto>> = _ordersLineArray


    var lastInvoiceCode by mutableLongStateOf(0)
    var clientCompany by mutableStateOf(Company())
    var clientUser by mutableStateOf(User())
    var clientType by mutableStateOf(AccountType.USER)
    var article by mutableStateOf(ArticleCompany())
    var commandLineDto by mutableStateOf(CommandLineDto())
    var commandLineDtos by  mutableStateOf(emptyList<CommandLineDto>())
    var isLoading by  mutableStateOf(true)
    var discount by mutableDoubleStateOf(0.0)
    val company by mutableStateOf(sharedViewModel.company.value)
    var invoice by mutableStateOf(Invoice())
    var invoiceMode by mutableStateOf(InvoiceMode.CREATE)




    fun getAllMyInvoicesAsProvider(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val invoices = repository.getAllMyInvoicesAsProviderr(sharedViewModel.company.value.id!!)
                if(invoices.isSuccessful){
                    invoices.body()?.forEach{
                        realm.write {
                            copyToRealm(it,UpdatePolicy.ALL)
                        }
                    }
                }
                val response = repository.getAllMyInvoicesAsProvider(sharedViewModel.company.value.id!!)
                if(response.isSuccessful){
                    response.body()?.forEach{invoice ->
                        insertInvoice(invoice)
                    }
                }
            }catch (ex: Exception){
                Log.e("aymenbabayinvoice","error is :$ex")
            }
           _myInvoicesAsProvider.value = repository.getAllMyInvoicesAsProviderLocally(sharedViewModel.company.value.id!!)
                    isLoading = false

        }
    }

    @Transaction
    suspend fun insertInvoice(invoice : InvoiceDto){
        Log.e("insertinvoice","before client")
        invoice.client?.let {
            room.userDao().insertUser(mapUserToRoomUser(it.user))
            room.companyDao().insertCompany(mapCompanyToRoomCompany(it))
        }
        Log.e("insertinvoice","after client")
        invoice.person?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        Log.e("insertinvoice","before provider")
        room.companyDao().insertCompany(mapCompanyToRoomCompany(invoice.provider))
        Log.e("insertinvoice","before invoice")
        room.invoiceDao().insertInvoice(mapInvoiceToRoomInvoice(invoice))
        Log.e("insertinvoice","fun fun")
    }

    fun getAllMyInvoicesAsProviderAndStatus( status : PaymentStatus){
        viewModelScope.launch {
            try {
                val respons = repository.getAllMyInvoicesAsProviderAndStatuss(company.id!!, status)
                if(respons.isSuccessful){
                    respons.body()?.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
                val response = repository.getAllMyInvoicesAsProviderAndStatus(company.id!!, status)
                if(response.isSuccessful){
                    response.body()?.forEach {invoice ->
                        insertInvoice(invoice)
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMyInvoicesAsProviderAndStatus","exception : ${ex.message}")
            }
            _myInvoicesAsProvider.value = repository.getAllMyInvoicesAsProviderAndStatusLocally(company.id!! , status)
        }
    }


    fun getAllMyInvoicesAsClient(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val invoicesAsClient = repository.getAllMyInvoicesAsClientt(company.id?:0)
                if(invoicesAsClient.isSuccessful) {
                    invoicesAsClient.body()?.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
                val response = repository.getAllMyInvoicesAsClient(company.id?:0)
                if(response.isSuccessful) {
                    response.body()?.forEach {invoice ->
                        insertInvoice(invoice)
                    }
                }
            }catch (ex: Exception){
                Log.e("getAllMyInvoicesAsClient","exception: ${ex.message}")

            }
            isLoading = false
            _myInvoicesAsClient.value = repository.getAllMyInvoicesAsClientLocally(sharedViewModel.company.value.id!!)

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
            isLoading = true
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getAllCommandLinesByInvoiceId(invoice.id!!)
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
        room.categoryDao().insertCategory(mapCategoryToRoomCategory(article.category))
        room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(article.subCategory))
        room.userDao().insertUser(mapUserToRoomUser(article.company.user))
        article.provider.user.let {
        room.userDao().insertUser(mapUserToRoomUser(article.provider.user))
        }
        room.companyDao().insertCompany(mapCompanyToRoomCompany(article.provider))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(article.company))
        room.articleDao().insertArticle(mapArticelDtoToRoomArticle(article.article))
        room.articleCompanyDao().insertArticle(
            mapArticleCompanyToRoomArticleCompany(article)
        )
        Log.e("insertOrderLine","fin fun article")
    }

    fun getAllOrdersLineByInvoiceId(){
            isLoading = true
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getAllOrdersLineByInvoiceId(invoice.id!!)
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
        Log.e("insertOrderLine","begin")
        order.purchaseorder?.let {
            it.client?.let { client ->
                room.userDao().insertUser(mapUserToRoomUser(client.user))
                room.companyDao().insertCompany(mapCompanyToRoomCompany(client))
            }
            Log.e("insertOrderLine", "after insert client")
            it.person?.let { person ->
            Log.e("insertOrderLine", "after insert person id : ${person.id}")
                room.userDao().insertUser(mapUserToRoomUser(person))
            }
            Log.e("insertOrderLine", "after insert person")
            it.company?.let { company ->
                room.userDao().insertUser(mapUserToRoomUser(company.user))
                room.companyDao().insertCompany(mapCompanyToRoomCompany(company))
            }
            Log.e("insertOrderLine", "after insert company")
        }
        order.invoice?.let {
            Log.e("insertOrderLine", "invoice id : ${it.id}")
            insertInvoice(it)
        }
        Log.e("insertOrderLine","after insert invoice")
            insertArticle(order.article!!)
        Log.e("insertOrderLine","after insert article")
        room.purchaseOrderDao().insertOrder(mapPurchaseOrderToRoomPurchaseOrder(order.purchaseorder!!))
        Log.e("insertOrderLine","after insert purchase order")
        room.purchaseOrderLineDao().insertOrderLine(
            mapPurchaseOrderLineToRoomPurchaseOrderLine(order)
        )
        Log.e("insertOrderLine","fin fun")
    }

    fun accepteInvoice(invoiceId : Long , status : Status){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.accepteInvoice(invoiceId , status)
                if(response.isSuccessful){
                    repository.updateInvoiceStatusLocally(invoiceId , status.toString())
                    room.invoiceDao().updateInvoiceStatus(invoiceId , status)
                }
            }catch (ex : Exception){
                Log.e("accepteInvoice","exception : ${ex.message}")
            }
        }
    }




}