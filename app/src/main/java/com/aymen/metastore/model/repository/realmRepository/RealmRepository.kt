package com.aymen.store.model.repository.realmRepository

import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Category
import com.aymen.store.model.entity.realm.Client
import com.aymen.store.model.entity.realm.ClientProviderRelation
import com.aymen.store.model.entity.realm.Comment
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.Conversation
import com.aymen.store.model.entity.realm.Inventory
import com.aymen.store.model.entity.realm.Invetation
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.entity.realm.Message
import com.aymen.store.model.entity.realm.Parent
import com.aymen.store.model.entity.realm.Payment
import com.aymen.store.model.entity.realm.PointsPayment
import com.aymen.store.model.entity.realm.Provider
import com.aymen.store.model.entity.realm.PurchaseOrder
import com.aymen.store.model.entity.realm.PurchaseOrderLine
import com.aymen.store.model.entity.realm.SearchHistory
import com.aymen.store.model.entity.realm.SubCategory
import com.aymen.store.model.entity.realm.Worker

interface RealmRepository {
    fun getAllSubCategoriesLocally(): List<SubCategory>

    fun getSubCategoriesByCategory(categoryId : Long):List<SubCategory>

    fun getAllCategoriesLocally():List<Category>

    fun getInventoryLocally():List<Inventory>

    fun getAllMyClientLocally(): List<ClientProviderRelation>

    fun getAllMyProviderLocally(): List<Provider>

    fun getAllArticlesLocaly(companyId : Long):List<ArticleCompany>

    fun getRandomArticleLocally():List<ArticleCompany>

    fun getRandomArticleByCompanyCategoryLocally(categName : String):List<ArticleCompany>

    fun getAllMyPaymentsLocally(): List<Payment>

    fun getAllMyOrdersLocally(): List<PurchaseOrder>

    fun getAllMyWorkerLocally():List<Worker>

    fun getMyParentLocally(): List<Parent>

    fun getAllMyOrdersLinesByOrderIdLocally(orderId : Long): List<PurchaseOrderLine>

    fun getAllMyInvoicesAsProviderLocally(myCompanyId : Long) : List<Invoice>

    fun getAllMyInvoicesAsClientLocally(myCompanyId : Long) : List<Invoice>

    fun getAllMyConversationsLocally() : List<Conversation>

    fun getAllMyMessageByConversationIdLocally(conversationId : Long): List<Message>

    fun getAllMyInvetationsLocally(): List<Invetation>

    suspend fun makeItAsFav(article: ArticleCompany)

    suspend fun getCommentsLocally(articleId : Long):List<Comment>

    suspend fun updateLastMessage(conversation: Conversation)

    suspend fun getAllMyPointsPaymentLocally(): List<PointsPayment>

    suspend fun deleteInvitation(id : Long)

    suspend fun getAllHistoryLocally():List<SearchHistory>

    suspend fun changeStatusLocally(status : String , id : Long, isAll: Boolean): List<PurchaseOrderLine>

     fun getAllMyPaymentsEspeceLocally(id : Long): List<PaymentForProviders>

     fun getAllMyPaymentsHistoryLocally(id : Long) :List<PaymentForProviders>

     fun getAllMyProfitsLocally(): List<PaymentForProviderPerDay>

     fun getAllArticlesByCategoryLocaly(myCompanyId: Long, myCompanyCategory: String) : List<Article>

     fun getAllMyInvoicesNotAcceptedLocally(id : Long):List<Invoice>
}