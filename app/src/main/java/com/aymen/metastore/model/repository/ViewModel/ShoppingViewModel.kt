package com.aymen.store.model.repository.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import retrofit2.Response
import java.math.BigDecimal
import javax.inject.Inject
@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val room: AppDatabase,
    private val sharedViewModel: SharedViewModel,
    private val appViewModel: AppViewModel,
    private val context : Context,
    private val useCases: MetaUseCases,

): ViewModel() {

    var rawInput by mutableStateOf("")
    var qte by mutableDoubleStateOf(0.0)
    var comment by mutableStateOf("")
    var order by mutableStateOf(PurchaseOrderLine())

    var orderArray by mutableStateOf(listOf<PurchaseOrderLine>())

    var delivery by mutableStateOf(false)
    var randomArticle by mutableStateOf(ArticleCompany())

    private var _myCompany = MutableStateFlow(Company())
    val myCompany: StateFlow<Company?> get() = _myCompany

    private var _myUser = MutableStateFlow(User())
    val myUser: StateFlow<User?> get() = _myUser

    private val _accountType = MutableStateFlow(AccountType.USER)
    val accountType: StateFlow<AccountType> = _accountType

    private val _allMyOrdersLineDetails : MutableStateFlow<PagingData<PurchaseOrderLine>> = MutableStateFlow(PagingData.empty())
    val allMyOrdersLineDetails: StateFlow<PagingData<PurchaseOrderLine>> get() = _allMyOrdersLineDetails

    private val _allMyOrdersNotAccepted : MutableStateFlow<PagingData<PurchaseOrder>> = MutableStateFlow(PagingData.empty())
    val allMyOrdersNotAccepted: StateFlow<PagingData<PurchaseOrder>> get() = _allMyOrdersNotAccepted

    var Order by mutableStateOf(PurchaseOrder())
    var cost by mutableStateOf(BigDecimal.ZERO)

init {
    viewModelScope.launch {
        sharedViewModel.user.collect { user ->
            val id = when (sharedViewModel.accountType.value) {
                AccountType.COMPANY -> sharedViewModel.company.value.id
                AccountType.USER -> sharedViewModel.user.value.id
                else -> 0
            }

            when (sharedViewModel.accountType.value) {
                AccountType.COMPANY -> {
                    Log.e("collectid","company id : $id")
                    getAllMyOrdersNotAccepted(id ?: 0)
                }
                AccountType.USER -> {
                    Log.e("collectid","user id : $id")
                    getAllMyOrdersNotAccepted(id ?: 0)
                }
                AccountType.AYMEN -> {}
                AccountType.NULL -> {}
            }
        }
    }

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
            AccountType.NULL -> TODO()
        }
    }
    fun sendOrder(index : Int, myBalance : BigDecimal){
        viewModelScope.launch (Dispatchers.IO){
            if(orderArray.isNotEmpty() && index == -1){
                try {
                    val response = repository.sendOrder(orderArray)
                    if(response.isSuccessful){
                            sharedViewModel.updateBalance(myBalance)
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
                        AccountType.NULL -> TODO()
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




    fun getAllMyOrdersNotAccepted(id : Long) {
        viewModelScope.launch {
                useCases.getAllMyOrdersNotAccepted(id)
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collect{
                        _allMyOrdersNotAccepted.value = it.map { line -> line.toPurchaseOrderWithCompanyAndUserOrClient() }
                    }

        }
    }



    fun orderLineResponse(status: Status, id : Long, isAll : Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            if (isAll) {
                room.purchaseOrderLineDao().deleteByPurchaseOrderId(id)
            } else {
                        room.purchaseOrderDao().deleteOrderNotAcceptedKeysById(id)
            }
            val result : Result<Response<Double>> = runCatching{
                repository.orderLineResponse(status,id,isAll)
            }
            result.fold(
                onSuccess = {success ->
                        appViewModel.updateShow("order")
                    val order = success.body()
                    if(order != null) {
                        appViewModel.updateCompanyBalance(order)
                        if (isAll) {
                            room.purchaseOrderLineDao().deleteByPurchaseOrderId(id)
                        } else {
                            room.purchaseOrderDao().deletePurchaseOrderById(id)
                        }
                    }

                },
                onFailure = {

                }
            )
        }
    }
}
