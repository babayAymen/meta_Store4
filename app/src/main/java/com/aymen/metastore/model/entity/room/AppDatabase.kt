package com.aymen.metastore.model.entity.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aymen.metastore.model.entity.Dao.ArticleCompanyDao
import com.aymen.metastore.model.entity.Dao.ArticleDao
import com.aymen.metastore.model.entity.Dao.CategoryDao
import com.aymen.metastore.model.entity.Dao.ClientProviderRelationDao
import com.aymen.metastore.model.entity.Dao.CommanLineDao
import com.aymen.metastore.model.entity.Dao.CommentDao
import com.aymen.metastore.model.entity.Dao.CompanyDao
import com.aymen.metastore.model.entity.Dao.ConversationDao
import com.aymen.metastore.model.entity.Dao.InventoryDao
import com.aymen.metastore.model.entity.Dao.InvetationDao
import com.aymen.metastore.model.entity.Dao.InvoiceDao
import com.aymen.metastore.model.entity.Dao.MessageDao
import com.aymen.metastore.model.entity.Dao.PaymentForProviderPerDayDao
import com.aymen.metastore.model.entity.Dao.PaymentForProvidersDao
import com.aymen.metastore.model.entity.Dao.PointsPaymentDao
import com.aymen.metastore.model.entity.Dao.PurchaseOrderDao
import com.aymen.metastore.model.entity.Dao.PurchaseOrderLineDao
import com.aymen.metastore.model.entity.Dao.RatingDao
import com.aymen.metastore.model.entity.Dao.SearchHistoryDao
import com.aymen.metastore.model.entity.Dao.SubCategoryDao
import com.aymen.metastore.model.entity.Dao.UserDao
import com.aymen.metastore.model.entity.Dao.WorkerDao

@Database(entities = [PurchaseOrder::class, PurchaseOrderLine::class, Company::class, User::class,
    Article::class, ArticleCompany::class, bankTransfer::class, Bill::class, Cash::class, CommandLine::class,
    Comment::class, Conversation::class, Category::class, SubCategory::class, Invoice::class, ClientProviderRelation::class,
                     SearchHistory::class, Inventory::class, Invitation::class, Message::class, PaymentForProviders::class, PointsPayment::class,
    PaymentForProviderPerDay::class, Rating::class, Worker::class
                     ], version = 9, exportSchema = false)
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
}