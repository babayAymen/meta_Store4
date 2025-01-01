package com.aymen.metastore.model.entity.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aymen.metastore.model.entity.dao.ArticleCompanyDao
import com.aymen.metastore.model.entity.dao.ArticleDao
import com.aymen.metastore.model.entity.dao.CategoryDao
import com.aymen.metastore.model.entity.dao.ClientProviderRelationDao
import com.aymen.metastore.model.entity.dao.CommanLineDao
import com.aymen.metastore.model.entity.dao.CommentDao
import com.aymen.metastore.model.entity.dao.CompanyDao
import com.aymen.metastore.model.entity.dao.ConversationDao
import com.aymen.metastore.model.entity.dao.InventoryDao
import com.aymen.metastore.model.entity.dao.InvetationDao
import com.aymen.metastore.model.entity.dao.InvoiceDao
import com.aymen.metastore.model.entity.dao.MessageDao
import com.aymen.metastore.model.entity.dao.PaymentDao
import com.aymen.metastore.model.entity.dao.PaymentForProviderPerDayDao
import com.aymen.metastore.model.entity.dao.PaymentForProvidersDao
import com.aymen.metastore.model.entity.dao.PointsPaymentDao
import com.aymen.metastore.model.entity.dao.PurchaseOrderDao
import com.aymen.metastore.model.entity.dao.PurchaseOrderLineDao
import com.aymen.metastore.model.entity.dao.RatingDao
import com.aymen.metastore.model.entity.dao.SearchHistoryDao
import com.aymen.metastore.model.entity.dao.SubCategoryDao
import com.aymen.metastore.model.entity.dao.UserDao
import com.aymen.metastore.model.entity.dao.WorkerDao
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.Bill
import com.aymen.metastore.model.entity.room.entity.Cash
import com.aymen.metastore.model.entity.room.entity.Category
import com.aymen.metastore.model.entity.room.entity.ClientProviderRelation
import com.aymen.metastore.model.entity.room.entity.CommandLine
import com.aymen.metastore.model.entity.room.entity.Comment
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.Conversation
import com.aymen.metastore.model.entity.room.entity.Inventory
import com.aymen.metastore.model.entity.room.entity.Invitation
import com.aymen.metastore.model.entity.room.entity.Invoice
import com.aymen.metastore.model.entity.room.entity.Message
import com.aymen.metastore.model.entity.room.entity.Payment
import com.aymen.metastore.model.entity.room.entity.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.room.entity.PaymentForProviders
import com.aymen.metastore.model.entity.room.entity.PointsPayment
import com.aymen.metastore.model.entity.room.entity.PurchaseOrder
import com.aymen.metastore.model.entity.room.entity.PurchaseOrderLine
import com.aymen.metastore.model.entity.room.entity.RandomArticle
import com.aymen.metastore.model.entity.room.entity.Rating
import com.aymen.metastore.model.entity.room.entity.ReglementForProvider
import com.aymen.metastore.model.entity.room.entity.SearchHistory
import com.aymen.metastore.model.entity.room.entity.SubCategory
import com.aymen.metastore.model.entity.room.entity.User
import com.aymen.metastore.model.entity.room.entity.Worker
import com.aymen.metastore.model.entity.room.entity.bankTransfer
import com.aymen.metastore.model.entity.room.remoteKeys.AllInvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.AllSearchRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ArtRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleCompanyRandomRKE
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleContainingRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.BuyHistoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.CategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ClientProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ClientRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.CommandLineByInvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.CommentArticleRemoteKeys
import com.aymen.metastore.model.entity.room.remoteKeys.CompanyArticleRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ConversationRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InCompleteRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InventoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvitationRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvoicesAsClientAndStatusRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.MessageRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.NotAcceptedRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.NotPayedRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.OrderLineKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.OrderNotAcceptedKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PayedRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentForProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentPerDayByDateRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentPerDayRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ProviderProfitHistoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.RatingRemoteKeys
import com.aymen.metastore.model.entity.room.remoteKeys.RechargeRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ReglementForProviderRemoteKeys
import com.aymen.metastore.model.entity.room.remoteKeys.SubCategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.WorkerRemoteKeys

@Database(entities = [PurchaseOrder::class, PurchaseOrderLine::class, Company::class, User::class,
    Article::class, RandomArticle::class, ArticleCompany::class, bankTransfer::class, Bill::class, Cash::class, CommandLine::class,
    Comment::class, Conversation::class, Category::class, SubCategory::class, Invoice::class, ClientProviderRelation::class,
                     SearchHistory::class, Inventory::class, Invitation::class, Message::class, PaymentForProviders::class, PointsPayment::class,
    ReglementForProvider::class,
    PaymentForProviderPerDay::class, Rating::class, Worker::class, Payment::class, ArticleRemoteKeysEntity::class, CategoryRemoteKeysEntity::class, ClientProviderRemoteKeysEntity::class,
ConversationRemoteKeysEntity::class, InventoryRemoteKeysEntity::class, InvitationRemoteKeysEntity::class, InvoiceRemoteKeysEntity::class, MessageRemoteKeysEntity::class,
 PointsPaymentRemoteKeysEntity::class, PointsPaymentForProviderRemoteKeysEntity::class, RechargeRemoteKeysEntity::class, SubCategoryRemoteKeysEntity::class,
ArtRemoteKeysEntity::class, ArticleCompanyRandomRKE::class, ArticleContainingRemoteKeysEntity::class, ClientRemoteKeysEntity::class, ProviderRemoteKeysEntity::class,
    OrderNotAcceptedKeysEntity::class, OrderLineKeysEntity::class, BuyHistoryRemoteKeysEntity::class, PayedRemoteKeysEntity::class, NotPayedRemoteKeysEntity::class, InCompleteRemoteKeysEntity::class,
NotAcceptedRemoteKeysEntity::class, ProviderProfitHistoryRemoteKeysEntity::class, PointsPaymentPerDayRemoteKeysEntity::class, AllInvoiceRemoteKeysEntity::class,
    PointsPaymentPerDayByDateRemoteKeysEntity::class, AllSearchRemoteKeysEntity::class, InvoicesAsClientAndStatusRemoteKeysEntity::class, CompanyArticleRemoteKeysEntity::class
, CommandLineByInvoiceRemoteKeysEntity::class, CommentArticleRemoteKeys::class, WorkerRemoteKeys::class, ReglementForProviderRemoteKeys::class, RatingRemoteKeys::class
                     ], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyDao() : CompanyDao
    abstract fun userDao() : UserDao
    abstract fun purchaseOrderDao(): PurchaseOrderDao
    abstract fun purchaseOrderLineDao() : PurchaseOrderLineDao
    abstract fun articleDao() : ArticleDao
    abstract fun articleCompanyDao() : ArticleCompanyDao
    abstract fun categoryDao() : CategoryDao
    abstract fun subCategoryDao() : SubCategoryDao
    abstract fun clientProviderRelationDao() : ClientProviderRelationDao
    abstract fun searchHistoryDao() : SearchHistoryDao
    abstract fun inventoryDao() : InventoryDao
    abstract fun invetationDao() : InvetationDao
    abstract fun invoiceDao() : InvoiceDao
    abstract fun commandLineDao() : CommanLineDao
    abstract fun conversationDao() : ConversationDao
    abstract fun messageDao() : MessageDao
    abstract fun paymentForProvidersDao() : PaymentForProvidersDao
    abstract fun pointsPaymentDao() : PointsPaymentDao
    abstract fun paymentForProviderPerDayDao() : PaymentForProviderPerDayDao
    abstract fun ratingDao() : RatingDao
    abstract fun workerDao() : WorkerDao
    abstract fun commentDao() : CommentDao
    abstract fun paymentDao() : PaymentDao
}