package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderDtoToPurchaseOrderRealm
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.store.model.entity.api.ArticleDto
import com.aymen.store.model.entity.api.CompanyDto
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.api.PurchaseOrderDto
import com.aymen.store.model.entity.api.PurchaseOrderLineDto
import com.aymen.store.model.entity.api.UserDto
import com.aymen.store.model.entity.realm.PurchaseOrder
import com.aymen.store.model.entity.realm.PurchaseOrderLine
import com.aymen.metastore.model.entity.realm.User
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.converterRealmToApi.mapApiArticleToRealm
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToDto
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRealm
import com.aymen.store.model.entity.converterRealmToApi.mapRealmArticleToApi
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
    private val companyViewModel: CompanyViewModel,
    private val sharedViewModel: SharedViewModel
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
            val sellingPrice = it[index].article.sellingPrice
            val price = BigDecimal(quantity).multiply(BigDecimal(sellingPrice))
            sharedViewModel.returnThePrevioseBalance(price)
            it.removeAt(index)
        }
    }

fun submitShopping(newBalance : BigDecimal) {
    val existingOrder = orderArray.find { it.article.id == randomArtilce.id }
    if (existingOrder != null) {
        val updatedOrderArray = orderArray.map {
            if (it.article.id == randomArtilce.id) {
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
            cost = cost.add(BigDecimal(it.article.sellingPrice).multiply(BigDecimal(it.quantity)))
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
                    sharedViewModel._company.value = it ?: Company()
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
                            sharedViewModel._company.value = it ?: Company()
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
                                    article = mapArticleCompanyToRealm(purchaseLineDto.article)
                                    comment = purchaseLineDto.comment ?: ""
                                    quantity = purchaseLineDto.quantity
                                    status = purchaseLineDto.status.toString()
                                    purchaseorder = mapPurchaseOrderDtoToPurchaseOrderRealm(purchaseLineDto.purchaseorder)
                                }
                                copyToRealm(line, UpdatePolicy.ALL)
                            }
                            isLoading = false
                        }
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenbabayOrder", "all my orders exception: $_ex")
                }
                _allMyOrdersLine.value = repository.getAllMyOrdersLinesByOrderIdLocally(Order.id!!)
            }
        }
    }

//    fun getAllMyOrdersLine(){
//         viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                try {
//                    val orders = repository.getAllMyOrdersLinesByOrderId(orderId)
//                    Log.e("aymenbabayOrder", "orderId: $orderId")
//                    if (orders.isSuccessful) {
//                    Log.e("aymenbabayOrder", "orders size: ${orders.body()?.size}")
////                    allMyOrdersLine = orders.body()!!
//                        orders.body()!!.forEach { purchaseLineDto ->
//                                Log.e("aymenbabayOrder", "qte lineDto: ${purchaseLineDto.quantity}")
//
//                            realm.write {
//                                val line = PurchaseOrderLine().apply {
//                                    id = purchaseLineDto.id
//                                    article = convertArticleToReal(purchaseLineDto.article)
//                                    comment = purchaseLineDto.comment ?: ""
//                                    quantity = purchaseLineDto.quantity
//                                    purchaseorder =  convertPurchaseOrder(purchaseLineDto.purchaseorder)
//                                }
//                                Log.e("shouppingviewmodel", " company id from purchase order view model ${purchaseLineDto.purchaseorder.company.id}")
//
//                                copyToRealm(line, UpdatePolicy.ALL)
//                            }
//                        }
//                    }
//                    } catch (_ex : Exception){
//                        Log.e("aymenbabayOrder", "all my orders exption: $_ex")
//                    }
//
//                    allMyOrdersLine = repository.getAllMyOrdersLinesByOrderIdLocally(orderId)
//                    Log.e("aymenbabayOrder", "all my orders line size: ${allMyOrdersLine.size}")
//                }
//
//        }
//    }

    fun getAllMyInvoicesNotAccepted(){
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("getAllMyInvoicesNotAccepted","launched")
            try{
                val response = repository.getAllMyInvoicesNotAccepted()
                Log.e("getAllMyInvoicesNotAccepted","response size : ${response.body()!!.size}")
                if(response.isSuccessful){
                    response.body()?.forEach { invoice ->
                    Log.e("getAllMyInvoicesNotAccepted",invoice.paid)
                    realm.write {
                        Invoice().apply {
                            copyToRealm(invoice, updatePolicy = UpdatePolicy.ALL)
                        }                    }
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


    fun getAllMyOrders(){
        isLoading = true
        companyViewModel.getMyCompany { myCompany ->
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    try {
                    val allMyOrder = myCompany?.let { repository.getAllMyOrdersLines(myCompany.id?:0) }
                        if (allMyOrder != null) {
                            if(allMyOrder.isSuccessful){
                                isLoading = false
                                allMyOrder.body()?.forEach{ order ->
                                    realm.write {
//                                        val pur = PurchaseOrder().apply {
//                                            id = it.id
//                                            orderNumber = it.orderNumber
//                                            client = it.client
//                                            person = it.person
//                                            company = it.company
//                                            createdDate = it.createdDate.toString()
//                                        }
                                        Log.e("getallmyorders","size : ${allMyOrder.body()?.toString()}")
                                        copyToRealm(order, UpdatePolicy.ALL)
                                    }
                                }
                            }
                        }
                    }catch (_ex : Exception){
                        Log.e("aymenbabayOrder","all my orders exption: $_ex ")
                    }
                    isLoading = false
                    allMyOrders = repository.getAllMyOrdersLocally()
                }
            }
        }
    }

    fun orderLineResponse(status: String, id : Long, isAll : Boolean){
        viewModelScope.launch(Dispatchers.IO) {
                try {
                    val order = repository.orderLineResponse(status,id,isAll)
                    if(order.isSuccessful){
                      val re = repository.changeStatusLocally(status,id,isAll)
                        _allMyOrdersLine.value = re

                    }
                }catch (_ex : Exception){
                    Log.e("aymenbabayOrder","orderLineResponse exption: $_ex")
                }
        }
    }
}
