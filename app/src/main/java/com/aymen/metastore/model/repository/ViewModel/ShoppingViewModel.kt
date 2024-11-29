package com.aymen.store.model.repository.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val context : Context,
    private val useCases: MetaUseCases
): ViewModel() {

    var qte by mutableDoubleStateOf(0.0)
    var comment by mutableStateOf("")
    var order by mutableStateOf(PurchaseOrderLine())

    var orderArray by mutableStateOf(listOf<PurchaseOrderLine>())

    var delivery by mutableStateOf(false)
    var randomArticle by mutableStateOf(ArticleCompany())
    // StateFlow properties that will update reactively
    private val _myCompany = MutableStateFlow(sharedViewModel.company.value)
    val myCompany: StateFlow<Company?> = _myCompany

    private val _myUser = MutableStateFlow(sharedViewModel.user.value)
    val myUser: StateFlow<User?> = _myUser

    private val _accountType = MutableStateFlow(sharedViewModel.accountType)
    val accountType: StateFlow<AccountType> = _accountType

    private val _allMyOrdersLineDetails : MutableStateFlow<PagingData<PurchaseOrderLine>> = MutableStateFlow(PagingData.empty())
    val allMyOrdersLineDetails: StateFlow<PagingData<PurchaseOrderLine>> get() = _allMyOrdersLineDetails

    private val _allMyOrdersNotAccepted : MutableStateFlow<PagingData<PurchaseOrder>> = MutableStateFlow(PagingData.empty())
    val allMyOrdersNotAccepted: StateFlow<PagingData<PurchaseOrder>> get() = _allMyOrdersNotAccepted

    private val _allMyOrders = MutableStateFlow<List<PurchaseOrder>>(emptyList())
    val allMyOrders: StateFlow<List<PurchaseOrder>> = _allMyOrders


    var Order by mutableStateOf(PurchaseOrder())
    var cost by mutableStateOf(BigDecimal.ZERO)

init {
    getAllMyOrdersNotAccepted()
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

fun submitShopping(newBalance: BigDecimal) {
    val existingOrder = orderArray.find { it.article?.id == randomArticle.id }

    if (existingOrder != null) {
        val updatedOrder = existingOrder.copy(quantity = qte, comment = comment , delivery = delivery)
        orderArray = orderArray.map {
            if (it == existingOrder) updatedOrder else it
        }
    } else {
        val newOrder =  PurchaseOrderLine(quantity = qte, comment = comment , delivery = delivery , article = randomArticle)
        orderArray = orderArray + newOrder
    }
    calculateCost()
    sharedViewModel.updateBalance(newBalance)
    remiseAZero()
}


    fun beforSendOrder(onFinish : (Boolean, Double) -> Unit){
        if(delivery)
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
                    val response = repository.sendOrder(orderArray)
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
                val response = repository.sendOrder(newOrderArray)
                    if (response.isSuccessful) {
                        removeOrderById(index)
                    }
                }catch (ex : Exception){
                    Log.e("sendOrder","exception : ${ex.message}")
                }
            }
        }
    }

//
//    fun mapOrderArrayToDto(orderArray : List<PurchaseOrderLineWithPurchaseOrderOrInvoice>):List<PurchaseOrderLineDto>{
//        val dtoArray = emptyList<PurchaseOrderLineDto>().toMutableList()
//        orderArray.forEach {
//            val line = PurchaseOrderLineDto()
//            line.article = mapRoomArticleToArticleDto(it.article?.articleCompany!!)
//            line.quantity = it.purchaseOrderLine.quantity
//            line.delivery = it.purchaseOrderLine.delivery
//            line.comment = it.purchaseOrderLine.comment
//             dtoArray += line
//        }
//        return dtoArray
//    }

    fun remiseAZero(){
        delivery = false
        order = PurchaseOrderLine()
        qte = 0.0
        comment = ""
        randomArticle = ArticleCompany()
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
            cost = cost.add(BigDecimal(it.article?.sellingPrice!!).multiply(BigDecimal(it.quantity!!)))
        }
    }

    fun getAllMyOrdersLine(orderId : Long) {
        viewModelScope.launch {
             useCases.getPurchaseOrderDetails(orderId)
                 .distinctUntilChanged()
                 .cachedIn(viewModelScope)
                 .collect{order ->
                 _allMyOrdersLineDetails.value = order.map { or -> or.toPurchaseOrderineWithPurchaseOrderOrinvoice() }
            }
        }
    }




    fun getAllMyOrdersNotAccepted() {
        viewModelScope.launch {
            val id = if(sharedViewModel.accountType == AccountType.COMPANY) sharedViewModel.company.value.id else sharedViewModel.user.value.id
            Log.e("getallorders","id : $id")
          useCases.getAllMyOrdersNotAccepted(id?:0)
              .distinctUntilChanged()
              .cachedIn(viewModelScope)
              .collect{
                  _allMyOrdersNotAccepted.value = it.map { line -> line.toPurchaseOrderWithCompanyAndUserOrClient() }
              }
        }
    }



    fun orderLineResponse(status: Status, id : Long, isAll : Boolean){
        viewModelScope.launch(Dispatchers.IO) {
                try {
                    val order = repository.orderLineResponse(status,id,isAll)
                    if(order.isSuccessful){
                        appViewModel.updateCompanyBalance(order.body()!!)
                    }
                }catch (_ex : Exception){
                    Log.e("aymenbabayOrder","orderLineResponse exption: $_ex")
                }
        }
    }
}
