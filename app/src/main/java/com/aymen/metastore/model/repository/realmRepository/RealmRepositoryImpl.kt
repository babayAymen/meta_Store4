package com.aymen.store.model.repository.realmRepository

import android.util.Log
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Category
import com.aymen.store.model.entity.realm.ClientProviderRelation
import com.aymen.store.model.entity.realm.Comment
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
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import javax.inject.Inject

class RealmRepositoryImpl @Inject constructor(
    private val realm: Realm
)
    : RealmRepository {
//
//    override fun getAllSubCategoriesLocallyy(companyId: Long): List<SubCategory> {
//        return realm
//            .query<SubCategory>(
//                query = "category.company.id == $0",companyId
//            )
//            .find()
//    }
//
//    override fun getSubCategoriesByCategoryLocally(categoryId: Long,companyId: Long): List<SubCategory> {
//        return realm
//            .query<SubCategory>(
//                query = "category.id == $0",categoryId
//            ).find()
//    }
//
//    override fun getAllCategoriesLocally(companyId : Long): List<Category> {
//        return realm
//            .query<Category>(
//                query = "company.id == $0",companyId
//            )
//            .find()
//    }
//
//    override fun getRandomArticlesByCategoryLocally(categoryId: Long, companyId: Long): List<ArticleCompany> {
//        return realm.query<ArticleCompany>(
//            query = "category.id == $0 and company.id == $1", categoryId, companyId
//        )
//            .find()
//    }
//
//    override fun getRandomArticlesBySubCategoryLocally(
//        subcategoryId: Long,
//        companyId: Long
//    ): List<ArticleCompany> {
//        return realm.query<ArticleCompany>(
//            query = "subCategory.id == $0 and category.company.id == $1",subcategoryId , companyId
//        )
//            .find()
//    }
//
//    override fun getInventoryLocally(): List<Inventory> {
//        return realm
//            .query<Inventory>()
//            .find()
//    }
//
//    override fun getAllMyClientLocally(): List<ClientProviderRelation> {
//        return  realm
//            .query<ClientProviderRelation>()
//            .find()
//    }
//
//    override fun getAllMyProviderLocally(): List<Provider> {
//        return realm
//            .query<Provider>()
//            .find()
//    }
//
//    override fun getAllArticlesLocaly(companyId : Long): List<ArticleCompany> {
//        return realm
//            .query<ArticleCompany>(
//                query = "company.id == $0",companyId
//            )
//            .find()
//    }
//
//    override fun getRandomArticleLocally(): List<ArticleCompany> {
//        return realm
//            .query<ArticleCompany>(
//                query = "isRandom == $0",true
//            )
//            .find()
//    }
//
//    override fun getRandomArticleByCompanyCategoryLocally(categName : String): List<ArticleCompany> {
//        return realm
//            .query<ArticleCompany>(
//                query = "isRandom == $0 AND company.category == $1",true,categName
//            )
//            .find()
//    }
//
//    override fun getAllMyPaymentsLocally(): List<Payment> {
//        return realm
//            .query<Payment>()
//            .find()
//    }
//
//    override fun getAllMyOrdersLocally(): List<PurchaseOrder> {
//        val allOrders = realm.query<PurchaseOrder>().find()
//        Log.d("DEBUG", "Found ${allOrders.size} purchase orders")
//        allOrders.forEach { order ->
//            Log.d("DEBUG", "Order ID: ${order.id}, Lines Count: ${order.purchaseorderlines.size}")
//            order.purchaseorderlines.forEach { line ->
//                Log.d("DEBUG", "Line Status: ${line.status}")
//            }
//        }
//
//        return realm.query<PurchaseOrder>(
//         query = "ANY purchaseorderlines.status == $0",Status.ACCEPTED.toString()
//     ).find()
//    }
//
//    override fun getAllMyWorkerLocally(): List<Worker> {
//        return realm
//            .query<Worker>()
//            .find()
//    }
//
//    override fun getMyParentLocally(): List<Parent> {
//        return realm
//            .query<Parent>()
//            .find()
//    }
//
//    override fun getAllMyOrdersLinesByOrderIdLocally(orderId: Long): List<PurchaseOrderLine> {
//        val rt = realm
//            .query<PurchaseOrderLine>(
//               query = "purchaseorder.id == $0",orderId
//            )
//            .find()
//        return rt
//    }
//
//    override fun getAllMyInvoicesAsProviderLocally(myCompanyId: Long): List<Invoice> {
//        return realm
//            .query<Invoice>(
//                query = "provider.id == $0",myCompanyId
//            )
//            .find()
//    }
//
//    override fun getAllMyInvoicesAsProviderAndStatusLocally(
//        companyId: Long,
//        status: PaymentStatus
//    ): List<Invoice> {
//        return realm.query<Invoice>(
//            query = "provider.id == $0 and paid == $1",companyId,status.toString()
//        ).find()
//    }
//
//    override fun getAllMyPaymentNotAcceptedLocally(companyId : Long): List<Invoice> {
//        return realm.query<Invoice>(
//            query = "status == $0 and provider.id == $1",Status.INWAITING.toString() , companyId
//        ).find()
//    }
//
//    override fun getAllMyInvoicesAsClientLocally(myCompanyId : Long): List<Invoice> {
//        return if(myCompanyId != 0L) {
//            realm
//                .query<Invoice>(
//                    query = "client.id == $0", myCompanyId
//                )
//                .find()
//        }else{
//            realm
//                .query<Invoice>(
//                    query = "status == $0", Status.ACCEPTED.toString()
//                ).find()
//        }
//    }
//
//    override fun getAllMyConversationsLocally(): List<Conversation> {
//        return realm
//            .query<Conversation>()
//            .find()
//    }
//
//    override fun getAllMyMessageByConversationIdLocally(conversationId : Long): List<Message> {
//            return realm
//                .query<Message>(
//                    query = "conversation.id == $0",conversationId
//                )
//                .sort("id", sortOrder = Sort.ASCENDING)
//                .find()
//    }
//
//    override fun getAllMyInvetationsLocally(): List<Invetation> {
//        return realm
//            .query<Invetation>()
////            .sort("id", sortOrder = Sort.ASCENDING)
//            .find()
//    }
//
//    override suspend fun getCommentsLocally(articleId: Long): List<Comment> {
//        return realm
//            .query<Comment>(
//                query = "article.id == $0",articleId
//            )
//            .find()
//    }
//
//    override suspend fun updateLastMessage(conversation: Conversation) {
//        realm.write {
//            copyToRealm(conversation, UpdatePolicy.ALL)
//
//        }
//    }
//
//    override suspend fun getAllMyPointsPaymentLocally(): List<PointsPayment> {
//        return realm
//            .query<PointsPayment>()
//            .find()
//    }
//
//    override suspend fun deleteInvitation(id: Long) {
//        realm.write {
//            val invitation = query<Invetation>("id == $0", id).find()
//            delete(invitation)
//        }
//    }
//
//    override suspend fun getAllHistoryLocally(): List<SearchHistory> {
//        return realm
//            .query<SearchHistory>()
//            .find()
//    }
//
//override suspend fun changeStatusLocally(status: String, id: Long, isAll: Boolean): List<PurchaseOrderLine> {
//
//    realm.write {
//        if (!isAll) {
//            val purchaseOrderLine = query<PurchaseOrderLine>(
//                query = "id == $0", id
//            ).first().find()
//
//            purchaseOrderLine?.let {
//                //it.status = status
//                it.purchaseorder?.let { it1 -> delete(it1) }
//            }
//
//        } else {
//            val purchaseOrderLines = query<PurchaseOrderLine>(
//                query = "purchaseorder.id == $0", id
//            ).find()
//
//            purchaseOrderLines.forEach { purchaseOrderLine ->
//                purchaseOrderLine.status = status
//            }
//        }
//    }
//
//    return getAllMyOrdersLinesByOrderIdLocally(id)
//}
//
//
//    override fun getAllMyPaymentsEspeceLocally(id: Long): List<PaymentForProviders> {
//       return realm.query<PaymentForProviders>(
//                query = "purchaseOrderLine.purchaseorder.client.id == $0 or purchaseOrderLine.purchaseorder.company.id == $0",id
//            ).find()
//
//    }
//
//    override fun getAllMyPaymentsHistoryLocally(id: Long): List<PaymentForProviders> {
//        return realm.query<PaymentForProviders>(
//            query = "purchaseOrderLine.purchaseorder.person.id == $0",id
//        ).find()
//
//    }
//
//    override fun getAllMyProfitsLocally(): List<PaymentForProviderPerDay> {
//        return realm.query<PaymentForProviderPerDay>(
//        ).find()
//    }
//
//    override fun getAllArticlesByCategoryLocaly(myCompanyId: Long, myCompanyCategory: String): List<Article> {
//        // Retrieve articles that are not associated with any ArticleCompany
//        val associatedArticleIds = realm.query<ArticleCompany>()
//            .find()
//            .mapNotNull {
//                if (it.company?.id == myCompanyId) it.article?.id else null
//            }  // Extract associated article IDs
//        // Retrieve all articles and filter out those that are associated with ArticleCompany
//        return realm.query<Article>(
//            query = "category == $0",myCompanyCategory
//        )
//            .find()
//            .filter { it.id !in associatedArticleIds }
//    }
//
//    override fun getAllMyInvoicesNotAcceptedLocally(id: Long): List<Invoice> {
//      return realm.query<Invoice>(
//          query = "status == $0 and (client.id == $1 or person.id == $1 or provider.id == $1)",Status.INWAITING.toString() , id
//      ).find()
//    }
//
//    override fun updateInvoiceStatusLocally(invoiceId: Long, status: String) {
//      val invoice = realm.query<Invoice>(
//           query = "id == $0",invoiceId
//       ).first().find()
//            invoice?.status = status
//
//    }




}