package com.aymen.store.model.repository.ViewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.dto.PurchaseOrderLineDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.OrderNotAcceptedKeysEntity
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.util.ORDER
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode
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

    private val purchaseOrderDao = room.purchaseOrderDao()
    private val purchaseOrderLineDao = room.purchaseOrderLineDao()
//
//    var rawInput by mutableStateOf("")
//    var qte by mutableDoubleStateOf(1.0)
//    var comment by mutableStateOf("")
    var order by mutableStateOf(PurchaseOrderLine())

    var orderArray by mutableStateOf(listOf<PurchaseOrderLine>())

    var delivery by mutableStateOf(true)
    var deliveryFee by mutableStateOf(BigDecimal(3))
    var randomArticle by mutableStateOf(ArticleCompany())

    val myCompany: StateFlow<Company> = sharedViewModel.company
    val myUser: StateFlow<User> = sharedViewModel.user

    val accountType: StateFlow<AccountType> = sharedViewModel.accountType

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
                AccountType.DELIVERY -> sharedViewModel.user.value.id
                else -> 0
            }

                    getAllMyOrdersNotAccepted(id ?: 0)
        }
    }

}
    fun removeOrderById(index: Int, restore : Boolean) {
        orderArray = orderArray.toMutableList().also {
            if(orderArray.size == 1){
                delivery = true
            }
            it.removeAt(index)
        }
    }
//    private fun startTimer() {
//        viewModelScope.launch {
//            delay(1 * 60 * 1000) // 15 minutes in milliseconds
//            delivery = false
//            Log.e("starttimer","delivery is : $delivery")
//        }
//    }
fun submitShopping(qte : Double , comment : String) {
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
    remiseAZero()
}


    fun sendOrder(){
        calculateNotDiscountedArticlesCost{// this implementation is for next app update
        }
            Log.e("costfromviemodel","cost $cost my balance ${myCompany.value.balance!!}")
        val oldBalance: BigDecimal
        val newBalance: BigDecimal
        if(delivery && cost<BigDecimal(30))
            cost = cost.add(BigDecimal(3))
        if(accountType.value == AccountType.COMPANY){
            oldBalance = BigDecimal(myCompany.value.balance!!)
            newBalance = oldBalance.subtract(cost).setScale(2,RoundingMode.HALF_UP)
        }else{
                oldBalance = BigDecimal(myUser.value.balance!!)
                newBalance = oldBalance.subtract(cost).setScale(2,RoundingMode.HALF_UP)
        }
        sharedViewModel.updateBalance(newBalance)
        viewModelScope.launch (Dispatchers.IO){
            val listOfIds: MutableList<OrderNotAcceptedKeysEntity> = mutableListOf()
            orderArray.forEach { item ->

                val latestOrderRemoteKey = purchaseOrderDao.getLatestOrderRemoteKey()
                val orderId = if (latestOrderRemoteKey == null) 1 else latestOrderRemoteKey.id + 1
                val ordersCount = purchaseOrderDao.getOrderCount()
                val page =
                    if (latestOrderRemoteKey?.prevPage == null) 0 else latestOrderRemoteKey.prevPage + 1
                val prevPage = if (page == 0) null else page - 1
                val remain = ordersCount % PAGE_SIZE
                val nextPage =
                    if (remain < PAGE_SIZE - 1 && latestOrderRemoteKey?.nextPage != null) page + 1 else null
                val newOrderRemoteKey = OrderNotAcceptedKeysEntity(
                    id = orderId,
                    prevPage = prevPage,
                    nextPage = nextPage
                )
                listOfIds += newOrderRemoteKey
                val latestPurchaseOrderLineId = purchaseOrderLineDao.getLatestPurchaseOrderId()
                room.withTransaction {
                    purchaseOrderDao.insertOrderNotAcceptedKeys(listOf(newOrderRemoteKey))
                    val purchaseOrder = PurchaseOrder(
                        id = orderId,
                        company = item.article?.company,
                        client = if (sharedViewModel.accountType.value == AccountType.COMPANY) sharedViewModel.company.value else null,
                        person = if (sharedViewModel.accountType.value == AccountType.USER) sharedViewModel.user.value else null,
                        orderNumber = 1
                    )
                    purchaseOrderDao.insertOrder(listOf(purchaseOrder.toPurchaseOrderEntity()))
                    purchaseOrderLineDao.insertOrderLine(
                        listOf(
                            PurchaseOrderLine(
                                id = if (latestPurchaseOrderLineId == null) 1 else latestPurchaseOrderLineId + 1,
                                purchaseorder = purchaseOrder,
                                article = item.article,
                                quantity = item.quantity,
                                comment = item.comment,
                                delivery = delivery
                            ).toPurchaseOrderLineEntity()
                        )
                    )
                }
            }
                    val result : Result<Response<List<PurchaseOrderLineDto>>> = runCatching {
                         repository.sendOrder(orderArray)
                    }
                    result.fold(
                        onSuccess = {success ->
                            delivery = true
                            if(success.isSuccessful){
                                val response = success.body()
                                room.withTransaction {
                                    listOfIds.forEach{key ->
                                    purchaseOrderDao.deletePurchaseOrderById(key.id)
                                    purchaseOrderDao.deleteOrderNotAcceptedKeysById(key.id)
                                    purchaseOrderLineDao.deleteByPurchaseOrderId(key.id)
                                    }
                                if(response != null){
                                    purchaseOrderDao.insertOrder(response.map { order -> order.purchaseorder?.toPurchaseOrder() })
                                    listOfIds.zip(response) { key, res ->
                                        key.copy(id = res.purchaseorder?.id ?: throw IllegalArgumentException("Purchase order ID is null"))
                                    }.let { updatedKeys ->
                                        purchaseOrderDao.insertOrderNotAcceptedKeys(updatedKeys)
                                    }
                                    purchaseOrderLineDao.insertOrderLine(response.map { line -> line.toPurchaseOrderLine() })
                                    }
                                }
                            }else{

                                sharedViewModel.updateBalance(oldBalance)
                            }
                        },
                        onFailure = {}
                    )
                returnAllMyMony()
        }
    }

    fun calculateNotDiscountedArticlesCost(isAbove : (Boolean) -> Unit){
        val notDiscountedArticlesCost = orderArray.filter { order -> order.article?.article?.isDiscounted == false }
            .sumOf { order ->
                val sellingPrice = order.article?.sellingPrice!!
                val quantity = order.quantity!!
                BigDecimal(sellingPrice).multiply(BigDecimal(quantity))
            }
        Log.e("costfromviemodel","cost : $cost discounted cost : $notDiscountedArticlesCost")

        val threshold = BigDecimal("0.2").multiply(cost)
        val isAbov = notDiscountedArticlesCost >= threshold
        if(isAbov) isAbove(true)
        else isAbove(false)
    }

    fun remiseAZero(){
        calculateCost()
        order = PurchaseOrderLine()
        randomArticle = ArticleCompany()
    }

    fun returnAllMyMony(){
        viewModelScope.launch {
             orderArray = emptyList()
            delivery = true
            cost = BigDecimal.ZERO
        }
    }

    fun calculateCost(){
        cost = BigDecimal.ZERO
        orderArray.forEach {
            val articelPriceTtc = BigDecimal(it.article?.sellingPrice!!).multiply(BigDecimal(1).add(BigDecimal(it.article.article?.tva?:0.0).divide(BigDecimal(100)))).setScale(2,RoundingMode.HALF_UP)
            cost = cost.add(articelPriceTtc.multiply(BigDecimal(it.quantity!!))).setScale(2,RoundingMode.HALF_UP)
        }
        Log.e("testbalance","cost $cost")
    }

    fun getAllMyOrdersLine(orderId : Long) {
        viewModelScope.launch {
             useCases.getPurchaseOrderDetails(orderId)
                 .distinctUntilChanged()
                 .cachedIn(viewModelScope)
                 .collect{order ->
                     isDeleted = false
                    _allMyOrdersLineDetails.value = order
            }
        }
    }
    var isDeleted  = false

    fun resetPurchaseOrderLine(){
            if (!isDeleted) {
                isDeleted = true
                orderArray = emptyList()
            }
        _allMyOrdersLineDetails.value = PagingData.empty()
    }


    fun getAllMyOrdersNotAccepted(id : Long) {
        viewModelScope.launch {
                useCases.getAllMyOrdersNotAccepted(id)
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collect{
                        _allMyOrdersNotAccepted.value = it
                    }

        }
    }



    fun orderLineResponse(status: Status, ids : List<Long>, price : Double?=0.0, withDelivery : Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            when(status){
                Status.ACCEPTED -> {
                    if(!withDelivery) {
                        val priceForCompany = BigDecimal(price!!).multiply(
                            BigDecimal(0.9).multiply(
                                BigDecimal(0.2)
                            )
                        ).setScale(2, RoundingMode.HALF_UP)
                        appViewModel.addCompanyBalance(priceForCompany.toDouble())
                    }
                }
                Status.CANCELLED -> appViewModel.addCompanyBalance(price!!)
                else -> {}
            }
            ids.forEach {
                purchaseOrderLineDao.changeStatusByLine(status,it)
            }

            val result : Result<Response<Double>> = runCatching{
                repository.orderLineResponse(status,ids)
            }
            result.fold(
                onSuccess = {success ->
                        appViewModel.updateView(ORDER)
                    val order = success.body()
                    if(order != null) {
                        appViewModel.updateCompanyBalance(order)
                    }

                },
                onFailure = {

                }
            )
        }
    }
}
