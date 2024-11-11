package com.aymen.store.model.repository.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.entity.converterRealmToApi.mapCategoryToRoomCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapInvoiceToRoomInvoice
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderLineToRoomPurchaseOrderLine
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderToRoomPurchaseOrder
import com.aymen.metastore.model.entity.converterRealmToApi.mapSubCategoryToRoomSubCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.Article
import com.aymen.metastore.model.entity.room.ArticleCompany
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.PurchaseOrder
import com.aymen.metastore.model.entity.room.PurchaseOrderLine
import com.aymen.metastore.model.entity.room.User
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderLineWithPurchaseOrderOrInvoice
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderWithCompanyAndUserOrClient
import com.aymen.store.model.entity.dto.PurchaseOrderLineDto
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.converterRealmToApi.mapArticelDtoToRoomArticle
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRoomArticleCompany
import com.aymen.store.model.entity.converterRealmToApi.mapRoomArticleToArticleDto
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.InvoiceDto
import com.aymen.store.model.entity.dto.PurchaseOrderDto
import com.aymen.store.model.entity.dto.UserDto
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject
@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val room: AppDatabase,
    private val sharedViewModel: SharedViewModel,
    private val appViewModel: AppViewModel,
    private val context : Context
): ViewModel() {

    var qte by mutableDoubleStateOf(0.0)
    var comment by mutableStateOf("")
    var order by mutableStateOf(PurchaseOrderLineWithPurchaseOrderOrInvoice(PurchaseOrderLine()))

    var orderArray by mutableStateOf(listOf<PurchaseOrderLineWithPurchaseOrderOrInvoice>())

    var delivery by mutableStateOf(false)
    var randomArticle by mutableStateOf(ArticleWithArticleCompany(articleCompany = ArticleCompany(), article = Article(), company = Company()))
    // StateFlow properties that will update reactively
    private val _myCompany = MutableStateFlow(sharedViewModel.company.value)
    val myCompany: StateFlow<CompanyDto?> = _myCompany

    private val _myUser = MutableStateFlow(sharedViewModel.user.value)
    val myUser: StateFlow<UserDto?> = _myUser

    private val _accountType = MutableStateFlow(sharedViewModel.accountType)
    val accountType: StateFlow<AccountType> = _accountType
    private val _allMyOrdersLine = MutableStateFlow<List<PurchaseOrderLineWithPurchaseOrderOrInvoice>>(emptyList())
    val allMyOrdersLine: StateFlow<List<PurchaseOrderLineWithPurchaseOrderOrInvoice>> = _allMyOrdersLine

    private val _allMyOrders = MutableStateFlow<List<PurchaseOrderWithCompanyAndUserOrClient>>(emptyList())
    val allMyOrders: StateFlow<List<PurchaseOrderWithCompanyAndUserOrClient>> = _allMyOrders


    var Order by mutableStateOf(PurchaseOrder())
    var cost by mutableStateOf(BigDecimal.ZERO)
    var isLoading by mutableStateOf(false)
init {
//     myCompany = sharedViewModel.company.value
//     myUser  by mutableStateOf( sharedViewModel.user.value)
//     accountType by mutableStateOf( sharedViewModel.accountType)
}
    fun removeOrderById(index: Int) {
        orderArray = orderArray.toMutableList().also {
            val quantity = it[index].purchaseOrderLine.quantity
            val sellingPrice = it[index].article?.articleCompany?.sellingPrice
            val price = BigDecimal(quantity!!).multiply(BigDecimal(sellingPrice!!))
            sharedViewModel.returnThePrevioseBalance(price)
            it.removeAt(index)
        }
    }

fun submitShopping(newBalance: BigDecimal) {
    val existingOrder = orderArray.find { it.article?.articleCompany?.id == randomArticle.articleCompany.id }

    if (existingOrder != null) {
        val updatedOrder = existingOrder.copy(
            purchaseOrderLine = existingOrder.purchaseOrderLine.copy(quantity = qte, comment = comment , delivery = delivery)
        )
        orderArray = orderArray.map {
            if (it == existingOrder) updatedOrder else it
        }
    } else {
        val newOrder = PurchaseOrderLineWithPurchaseOrderOrInvoice(
            purchaseOrderLine = PurchaseOrderLine(quantity = qte, comment = comment , delivery = delivery , articleId = randomArticle.articleCompany.id),
            article = ArticleWithArticleCompany(articleCompany = randomArticle.articleCompany , article = randomArticle.article, company = randomArticle.company  )
        )
        orderArray = orderArray + newOrder
    }
    calculateCost()
    sharedViewModel.updateBalance(newBalance)
    remiseAZero()
}


    fun beforSendOrder(onFinish : (Boolean, Double) -> Unit){
        when(accountType.value){
            AccountType.COMPANY -> if(cost < BigDecimal(30)){
                    onFinish(false, myCompany.value?.balance!!)
            }else{
                onFinish(true, myCompany.value?.balance!!)
            }
            AccountType.USER -> if(cost < BigDecimal(30)){
                onFinish(false, myCompany.value?.balance!!)
            }else{
                onFinish(true, myCompany.value?.balance!!)
            }
            AccountType.AYMEN -> TODO()
        }
    }
    fun sendOrder(index : Int, myBalance : Double){
        viewModelScope.launch (Dispatchers.IO){
            if(orderArray.isNotEmpty() && index == -1){
                try {
                    val sn = mapOrderArrayToDto(orderArray)
                    val response = repository.sendOrder(sn)
                    if(response.isSuccessful){
                        val diff = BigDecimal(myBalance).subtract(cost)
                        if(diff<BigDecimal(30)){
                            val newBalance = BigDecimal(myBalance).subtract(BigDecimal(3))
                            sharedViewModel.updateBalance(newBalance)
                        }
                        calculateCost()
                    }
                }catch (ex : Exception){
                    Log.e("sendOrder","exception : ${ex.message}")
                }
                orderArray = emptyList()
            }else{
                try {
                    when(accountType.value){
                        AccountType.COMPANY -> if(cost < BigDecimal(30)){
                            Toast.makeText(context, "3dt", Toast.LENGTH_SHORT).show()
                        }
                        AccountType.USER -> if(cost < BigDecimal(30)){
                            Toast.makeText(context, "3dt", Toast.LENGTH_SHORT).show()
                        }
                        AccountType.AYMEN -> TODO()
                    }
                val newOrderArray = orderArray.toMutableList()
                newOrderArray.retainAll { newOrderArray.indexOf(it) == index }
                val response = repository.sendOrder(mapOrderArrayToDto(newOrderArray))
                    if (response.isSuccessful) {
                        removeOrderById(index)
                    }
                }catch (ex : Exception){
                    Log.e("sendOrder","exception : ${ex.message}")
                }
            }
        }
    }


    fun mapOrderArrayToDto(orderArray : List<PurchaseOrderLineWithPurchaseOrderOrInvoice>):List<PurchaseOrderLineDto>{
        val dtoArray = emptyList<PurchaseOrderLineDto>().toMutableList()
        orderArray.forEach {
            val line = PurchaseOrderLineDto()
            line.article = mapRoomArticleToArticleDto(it.article?.articleCompany!!)
            line.quantity = it.purchaseOrderLine.quantity
            line.delivery = it.purchaseOrderLine.delivery
            line.comment = it.purchaseOrderLine.comment
             dtoArray += line
        }
        return dtoArray
    }

    fun remiseAZero(){
        delivery = false
        order = PurchaseOrderLineWithPurchaseOrderOrInvoice(PurchaseOrderLine())
        qte = 0.0
        comment = ""
        randomArticle = ArticleWithArticleCompany(ArticleCompany(), Article(), Company())
    }

    fun returnAllMyMony(){
        viewModelScope.launch {
            calculateCost()
            sharedViewModel.returnThePrevioseBalance(cost)
            orderArray = emptyList()
        }
    }

    fun calculateCost(){
        cost = BigDecimal.ZERO
        orderArray.forEach {
            cost = cost.add(BigDecimal(it.article?.articleCompany?.sellingPrice!!).multiply(BigDecimal(it.purchaseOrderLine.quantity!!)))
        }
    }

    fun getAllMyOrdersLine() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                     isLoading = true
                    val orders = repository.getAllMyOrdersLinesByOrderId(Order.id!!)
                    Log.e("getAllMyOrders","size : ${orders.body()?.size}")
                    if (orders.isSuccessful) {
                        orders.body()?.forEach { purchaseLineDto ->
                           insertShopping(purchaseLineDto)
                        }
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenbabayOrder", "all my orders exception: $_ex")
                }finally {
                            isLoading = false
                }
                _allMyOrdersLine.value = room.purchaseOrderLineDao().getAllMyOrdersLinesByOrderId(Order.id!!)
            }
        }
    }

    @Transaction
    suspend fun insertShopping(shopping : PurchaseOrderLineDto){
        room.categoryDao().insertCategory(mapCategoryToRoomCategory(shopping.article!!.category!!))
        room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(shopping.article!!.subCategory!!))
        room.userDao().insertUser(mapUserToRoomUser(shopping.article!!.company?.user))
        shopping.article!!.provider?.user?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        room.companyDao().insertCompany(mapCompanyToRoomCompany(shopping.article!!.provider))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(shopping.article!!.company))
        room.articleDao().insertArticle(mapArticelDtoToRoomArticle(shopping.article?.article!!))
        room.articleCompanyDao().insertArticle(mapArticleCompanyToRoomArticleCompany(shopping.article))
        shopping.invoice?.let {
            room.invoiceDao().insertInvoice(mapInvoiceToRoomInvoice(it))
        }
        room.purchaseOrderDao().insertOrder(mapPurchaseOrderToRoomPurchaseOrder(shopping.purchaseorder!!))
        room.purchaseOrderLineDao().insertOrderLine(
            mapPurchaseOrderLineToRoomPurchaseOrderLine(shopping)
        )
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
        room.companyDao().insertCompany(mapCompanyToRoomCompany(invoice.provider))
        room.invoiceDao().insertInvoice(mapInvoiceToRoomInvoice(invoice))
    }

    fun getAllMyOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading = true
                val response = repository.getAllMyOrdersLines(sharedViewModel.company.value.id ?: 0)
                Log.e("purchaseorderfromroom", "Fetched orders size: ${response.body()?.size}, company id: ${sharedViewModel.company.value.id}")

                if (response.isSuccessful) {
                    response.body()?.forEach { order ->
                        inserPurchaseorder(order)
                    }
                }
                _allMyOrders.value = when (sharedViewModel.accountType ) {
                    AccountType.COMPANY -> room.purchaseOrderDao().getAllMyOrdersAsCompany(sharedViewModel.company.value.id!!)
                    AccountType.USER -> room.purchaseOrderDao().getAllMyOrdersAsUser(sharedViewModel.user.value.id!!)
                    else -> {
                        Log.e("purchaseorderfromroom", "Account type not recognized, returning empty list.")
                        emptyList()
                    }
                }
                Log.e("purchaseorderfromroom", "Account type: ${sharedViewModel.accountType}, Retrieved orders size: ${_allMyOrders.value.size}")

            } catch (ex: Exception) {
                Log.e("purchaseorderfromroom", "Exception in getAllMyOrders: ${ex.message}")
            } finally {
                isLoading = false
            }
        }
    }

//
//    fun getAllMyOrders() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                isLoading = true
//                val response = repository.getAllMyOrdersLines(myCompany.id ?: 0)
//                Log.e("purchaseorderfromroom","size : ${response.body()?.size} and company id : ${myCompany.id}")
//                if (response.isSuccessful) {
//                    response.body()?.forEach { order ->
//                        inserPurchaseorder(order)
//                    }
//                }
//            } catch (_ex: Exception) {
//                Log.e("purchaseorderfromroom", "all my orders exception: ${_ex.message} ")
//            } finally {
//                isLoading = false
//            }
//            _allMyOrders.value = when (accountType) {
//                AccountType.COMPANY -> room.purchaseOrderDao().getAllMyOrdersAsCompany(myCompany.id!!)
//                AccountType.USER -> room.purchaseOrderDao().getAllMyOrdersAsUser(myUser.id!!)
//                else -> {
//                    Log.e("purchaseorderfromroom", "in else section")
//                    emptyList()
//                }
//            }
//            val orders = room.purchaseOrderDao().getAllMyOrdersAsUser(myUser.id!!)
//            Log.d("Database", "Retrieved orders: ${orders.size}")
//            orders.forEach { Log.d("Database", it.toString()) }
//            Log.e("getAllmyorders","account type : ${sharedViewModel.accountType} and response size : ${_allMyOrders.value.size}")
//        }
//    }

    fun deleteAll(){
        _allMyOrders.value = emptyList()
        _allMyOrdersLine.value = emptyList()
    }
    @Transaction
    suspend fun inserPurchaseorder(order : PurchaseOrderDto){
        order.person?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        order.client?.let {
            room.userDao().insertUser(mapUserToRoomUser(it.user))
            room.companyDao().insertCompany(mapCompanyToRoomCompany(it))
        }
        order.person?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        room.userDao().insertUser(mapUserToRoomUser(order.company?.user))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(order.company))
        room.purchaseOrderDao().insertOrder(
            mapPurchaseOrderToRoomPurchaseOrder(order)
        )
    }
    fun orderLineResponse(status: Status, id : Long, isAll : Boolean){
        viewModelScope.launch(Dispatchers.IO) {
                try {
                    val order = repository.orderLineResponse(status,id,isAll)
                    if(order.isSuccessful){
                        if(!isAll){
                            room.purchaseOrderLineDao().changeStatusByLine(status,id)
                            _allMyOrdersLine.value //should retreive some thing
                        }else{
                             room.purchaseOrderLineDao().changeStatusByOrder(status,id)
                            _allMyOrdersLine.value // should also
                        }
                        appViewModel.updateCompanyBalance(order.body()!!)
                    }
                }catch (_ex : Exception){
                    Log.e("aymenbabayOrder","orderLineResponse exption: $_ex")
                }
        }
    }
}
