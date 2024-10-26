package com.aymen.store.model.repository.ViewModel

import android.util.Log
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
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderDtoToPurchaseOrderRealm
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderLineToRoomPurchaseOrderLine
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderToRoomPurchaseOrder
import com.aymen.metastore.model.entity.converterRealmToApi.mapSubCategoryToRoomSubCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.dto.PurchaseOrderLineDto
import com.aymen.store.model.entity.realm.PurchaseOrder
import com.aymen.store.model.entity.realm.PurchaseOrderLine
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.converterRealmToApi.mapArticelDtoToRoomArticle
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToDto
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRealm
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRoomArticleCompany
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.InvoiceDto
import com.aymen.store.model.entity.dto.PurchaseOrderDto
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
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
    private val realm : Realm,
    private val room: AppDatabase,
    private val sharedViewModel: SharedViewModel,
    private val appViewModel: AppViewModel
): ViewModel() {

    var qte by mutableDoubleStateOf(0.0)
    var comment by mutableStateOf("")
    var order by mutableStateOf(PurchaseOrderLineDto())
    var orderArray: List<PurchaseOrderLineDto> by mutableStateOf(emptyList())
    var delivery by mutableStateOf(false)
    var randomArtilce by mutableStateOf(ArticleCompany())

    private val _allMyOrdersLine = MutableStateFlow<List<PurchaseOrderLine>>(emptyList())
    val allMyOrdersLine: StateFlow<List<PurchaseOrderLine>> = _allMyOrdersLine

    private val _allMyInvoiceNotAccepted = MutableStateFlow<List<Invoice>>(emptyList())
    val allMyInvoiceNotAccepted: StateFlow<List<Invoice>> = _allMyInvoiceNotAccepted

    var allMyOrders by mutableStateOf(emptyList<PurchaseOrder>())
    var Order by mutableStateOf(PurchaseOrder())
    var cost by mutableStateOf(BigDecimal.ZERO)
    var isLoading by mutableStateOf(false)

    fun clearAllOrdersLine() {
        _allMyOrdersLine.value = emptyList()
    }

    fun removeOrderById(index: Int) {
        orderArray = orderArray.toMutableList().also {
            val quantity = it[index].quantity
            val sellingPrice = it[index].article?.sellingPrice
            val price = BigDecimal(quantity!!).multiply(BigDecimal(sellingPrice!!))
            sharedViewModel.returnThePrevioseBalance(price)
            it.removeAt(index)
        }
    }

fun submitShopping(newBalance : BigDecimal) {
    val existingOrder = orderArray.find { it.article?.id == randomArtilce.id }
    if (existingOrder != null) {
        val updatedOrderArray = orderArray.map {
            if (it.article?.id == randomArtilce.id) {
                it.apply {
                    quantity = qte
                    comment = this@ShoppingViewModel.comment
                    delivery = it.delivery
                    article = it.article
                }

            } else {
                it
            }
        }
        orderArray = updatedOrderArray

    } else {
        val newOrder = order.apply {
            quantity = qte
            comment = this@ShoppingViewModel.comment
            delivery = this@ShoppingViewModel.delivery
            article = mapArticleCompanyToDto(randomArtilce)
        }
        orderArray = orderArray + newOrder

    }
   calculateCost()
    Log.e("balancecost","cost : $cost")
    sharedViewModel.updateBalance(newBalance)
    remiseAZero()
}

    fun remiseAZero(){
        delivery = false
        order = PurchaseOrderLineDto()
        qte = 0.0
        comment = ""
        randomArtilce = ArticleCompany()
    }

    fun returnAllMyMony(){
        viewModelScope.launch {
            calculateCost()
            Log.e("cost","cost : $cost")
            sharedViewModel.returnThePrevioseBalance(cost)
            orderArray = emptyList()
        }
    }

    fun calculateCost(){
        cost = BigDecimal.ZERO
        orderArray.forEach {
            cost = cost.add(BigDecimal(it.article?.sellingPrice!!).multiply(BigDecimal(it.quantity!!)))
        Log.e("cost","cost for each itiration : $cost")
        }
    }
    fun sendOrder(index : Int) {
        if (orderArray.isNotEmpty() && index == -1) {
            viewModelScope.launch(Dispatchers.IO){
                try {
                    val response = repository.sendOrder(orderArray)
                    if(response.isSuccessful){
                      calculateCost()
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenbabayorder", "error : $_ex")
                }
                orderArray = emptyList()
                sharedViewModel.getMyCompany {
                    sharedViewModel._company.value = it ?: CompanyDto()
                }
            }
        }else{
            viewModelScope.launch(Dispatchers.IO) {

                try {
                    val newOrderArray = orderArray.toMutableList()
                    newOrderArray.retainAll { newOrderArray.indexOf(it) == index }
                    val response = repository.sendOrder(newOrderArray)
                    if (response.isSuccessful) {
                        removeOrderById(index)
                        sharedViewModel.getMyCompany {
                            sharedViewModel._company.value = it ?: CompanyDto()
                        }
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenbabayorder", "error : $_ex")
                }
            }
        }



    }

    fun getAllMyOrdersLine() {
        isLoading = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val orders = repository.getAllMyOrdersLinesByOrderId(Order.id!!)
                    if (orders.isSuccessful) {
                        // Separate the write block to ensure it is not nested
                        realm.writeBlocking {
                            orders.body()?.forEach { purchaseLineDto ->
                                val line = PurchaseOrderLine().apply {
                                    id = purchaseLineDto.id
                                    article = mapArticleCompanyToRealm(purchaseLineDto.article!!)
                                    comment = purchaseLineDto.comment ?: ""
                                    quantity = purchaseLineDto.quantity!!
                                    status = purchaseLineDto.status.toString()
                                    purchaseorder = mapPurchaseOrderDtoToPurchaseOrderRealm(purchaseLineDto.purchaseorder!!)
                                }
                                copyToRealm(line, UpdatePolicy.ALL)
                            }
                        }

                        orders.body()?.forEach { purchaseLineDto ->
                           insertShopping(purchaseLineDto)
                        }
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenbabayOrder", "all my orders exception: $_ex")
                }finally {
                            isLoading = false
                }
                _allMyOrdersLine.value = repository.getAllMyOrdersLinesByOrderIdLocally(Order.id!!)
            }
        }
    }

    @Transaction
    suspend fun insertShopping(shopping : PurchaseOrderLineDto){
        room.categoryDao().insertCategory(mapCategoryToRoomCategory(shopping.article!!.category))
        room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(shopping.article!!.subCategory))
        room.userDao().insertUser(mapUserToRoomUser(shopping.article!!.company.user))
        shopping.article!!.provider.user?.let {
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


    fun getAllMyInvoicesNotAccepted(){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val respons = repository.getAllMyInvoicesNotAcceptedd()
                if(respons.isSuccessful){
                    respons.body()?.forEach { invoice ->
                    realm.write {
                        Invoice().apply {
                            copyToRealm(invoice, updatePolicy = UpdatePolicy.ALL)
                        }                    }
                    }
                }
                val response = repository.getAllMyInvoicesNotAccepted()
                if(response.isSuccessful){
                    response.body()?.forEach { invoice ->
                        insertInvoice(invoice)
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMyInvoicesNotAccepted","exception : ${ex.message}")
            }
            if (sharedViewModel.accountType == AccountType.USER){
                _allMyInvoiceNotAccepted.value = repository.getAllMyInvoicesNotAcceptedLocally(sharedViewModel.user.value.id!!)
            }

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


    fun getAllMyOrders(){
            viewModelScope.launch(Dispatchers.IO) {
                     isLoading = true
                    try {
                    val allMyOrder =  repository.getAllMyOrdersLiness(sharedViewModel.company.value.id?:0)
                        if (allMyOrder.isSuccessful) {
                                allMyOrder.body()?.forEach{ order ->
                                    realm.write {
                                        copyToRealm(order, UpdatePolicy.ALL)
                                    }
                                }
                            }
                    val response =  repository.getAllMyOrdersLines(sharedViewModel.company.value.id?:0)
                        if (response.isSuccessful) {
                                response.body()?.forEach{ order ->
                                   inserPurchaseorder(order)
                                }
                            }
                    }catch (_ex : Exception){
                        Log.e("purchaseorderfromroom","all my orders exption: $_ex ")
                    }finally {
                    isLoading = false
                    }
                    allMyOrders = repository.getAllMyOrdersLocally()

            }
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
        room.userDao().insertUser(mapUserToRoomUser(order.company?.user))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(order.company))
        room.purchaseOrderDao().insertOrder(
            mapPurchaseOrderToRoomPurchaseOrder(order)
        )
    }
    fun orderLineResponse(status: String, id : Long, isAll : Boolean){
        viewModelScope.launch(Dispatchers.IO) {
                try {
                    val order = repository.orderLineResponse(status,id,isAll)
                    if(order.isSuccessful){
                      val re = repository.changeStatusLocally(status,id,isAll)
                        _allMyOrdersLine.value = re
                        appViewModel.updateCompanyBalance(order.body()!!)
                    }
                }catch (_ex : Exception){
                    Log.e("aymenbabayOrder","orderLineResponse exption: $_ex")
                }
        }
    }
}
