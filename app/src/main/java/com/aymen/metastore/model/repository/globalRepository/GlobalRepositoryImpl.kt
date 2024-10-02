package com.aymen.store.model.repository.globalRepository

import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.metastore.model.entity.api.PointsPaymentDto
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.repository.remoteRepository.CommandLineRepository.CommandLineRepository
import com.aymen.metastore.model.repository.remoteRepository.aymenRepository.AymenRepository
import com.aymen.metastore.model.repository.remoteRepository.ratingRepository.RatingRepository
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type
import com.aymen.store.model.entity.api.AuthenticationRequest
import com.aymen.store.model.entity.api.RegisterRequest
import com.aymen.store.model.entity.api.CommandLineDto
import com.aymen.store.model.entity.api.ConversationDto
import com.aymen.store.model.entity.api.PurchaseOrderLineDto
import com.aymen.store.model.entity.realm.Conversation
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.repository.realmRepository.RealmRepository
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepository
import com.aymen.store.model.repository.remoteRepository.invetationRepository.InvetationRepository
import com.aymen.store.model.repository.remoteRepository.shoppingRepository.ShoppingRepository
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import com.aymen.store.model.repository.remoteRepository.categoryRepository.CategoryRepository
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepository
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepository
import com.aymen.store.model.repository.remoteRepository.inventoryRepository.InventoryRepository
import com.aymen.store.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import com.aymen.store.model.repository.remoteRepository.messageRepository.MessageRepository
import com.aymen.store.model.repository.remoteRepository.orderRepository.OrderRepository
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepository
import com.aymen.store.model.repository.remoteRepository.providerRepository.ProviderRepository
import com.aymen.store.model.repository.remoteRepository.signInRepository.SignInRepository
import com.aymen.store.model.repository.remoteRepository.subCategoryRepository.SubCategoryRepository
import com.aymen.store.model.repository.remoteRepository.workerRepository.WorkerRepository
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class GlobalRepositoryImpl  @Inject constructor
    (
    private val signInRepository: SignInRepository,
    private val articleRepository: ArticleRepository,
    private val subCategoryRepository: SubCategoryRepository,
    private val categoryRepository: CategoryRepository,
    private val companyRepository: CompanyRepository,
    private val realmRepository: RealmRepository,
    private val inventoryRepository: InventoryRepository,
    private val clientRepository: ClientRepository,
    private val providerRepository: ProviderRepository,
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
    private val workerRepository: WorkerRepository,
    private val invoiceRepository: InvoiceRepository,
    private val messageRepository: MessageRepository,
    private val shoppingRepository: ShoppingRepository,
    private val invetationRepository: InvetationRepository,
    private val pointPaymentRepository: PointPaymentRepository,
    private val ratingRepository: RatingRepository,
    private val aymenRepository: AymenRepository,
    private val commandLineRepository: CommandLineRepository,

    ) : GlobalRepository {
    override suspend fun addArticle(article: String, file: File):Response<Void> {
        return articleRepository.addArticle(article, file)
    }
    override suspend fun addArticleWithoutImage(article: String, articleId : Long):Response<Void> {
        return articleRepository.addArticleWithoutImage(article, articleId)
    }

//    override suspend fun getAllMyArticleContaining(articleLibel: String) = articleRepository.getAllMyArticleContaining(articleLibel)
    override suspend fun getAllArticlesContaining(search: String, searchType : SearchType) = articleRepository.getAllArticlesContaining(search, searchType)
    override suspend fun likeAnArticle(articleId: Long, isFav : Boolean) = articleRepository.likeAnArticle(articleId, isFav)
    override suspend fun sendComment(comment: String, articleId: Long) = articleRepository.sendComment(comment,articleId)
    override suspend fun getComments(articleId: Long) = articleRepository.getComments(articleId)
    override suspend fun getAllArticlesByCategory() = articleRepository.getAllArticlesByCategory()
    override suspend fun addQuantityArticle(quantity: Double, articleId: Long) = articleRepository.addQuantityArticle(quantity, articleId)

    override suspend fun getAll(companyId : Long, offset : Int, pageSize : Int) = articleRepository.getAll(companyId = companyId, offset = offset, pageSize = pageSize)
    override suspend fun deleteArticle(id: String): Response<Void> {
        TODO("Not yet implemented")
    }
    override suspend fun getAllCategoryByCompany(myCompanyId : Long,companyId : Long) =  categoryRepository.getAllCategoryByCompany(myCompanyId = myCompanyId,companyId = companyId)
    override suspend fun addCategoryApiWithImage(category: String, file: File) = categoryRepository.addCategoryApiWithImage(category, file)
    override suspend fun addCategoryApiWithoutImeg(category: String) = categoryRepository.addCategoryApiWithoutImeg(category)
    override suspend fun getSubCategoryByCategory(id: Long,companyId : Long) = subCategoryRepository.getSubCategoryByCategory(id,companyId = companyId)
    override suspend fun getAllSubCategories(companyId : Long) = subCategoryRepository.getAllSubCategories(companyId = companyId)
    override suspend fun addSubCtagoryWithImage(sousCategory: String, file: File) = subCategoryRepository.addSubCtagoryWithImage(sousCategory,file)
    override suspend fun addSubCategoryWithoutImage(sousCategory: String) = subCategoryRepository.addSubCategoryWithoutImage(sousCategory)
    override suspend fun addCompany(company: String, file : File) = companyRepository.addCompany(company, file)
    override suspend fun getAllMyProvider(companyId : Long) = companyRepository.getAllMyProvider(companyId = companyId)
    override suspend fun getMyParent(companyId : Long) = companyRepository.getMyParent(companyId = companyId)
    override suspend fun getMyCompany(companyId : Long) = companyRepository.getMyCompany(companyId = companyId)
    override suspend fun getMe() = companyRepository.getMe()

    override suspend fun getAllCompaniesContaining(search: String) = companyRepository.getAllCompaniesContaining(search)
    override suspend fun updateCompany(company: String, file: File) = companyRepository.updateCompany(company,file)
    override suspend fun updateImage(image: File) = companyRepository.updateImage(image)

    override suspend fun getInventory(companyId : Long) = inventoryRepository.getInventory(companyId = companyId)
    override suspend fun getAllMyClient(companyId : Long) = clientRepository.getAllMyClient(companyId = companyId)
    override suspend fun addClient(client: String, file: File) = clientRepository.addClient(client, file)
    override suspend fun addClientWithoutImage(client: String) = clientRepository.addClientWithoutImage(client)
    override suspend fun getAllMyClientContaining(clientName: String,companyId : Long) = clientRepository.getAllMyClientContaining(clientName,companyId = companyId)
    override suspend fun sendClientRequest(id: Long, type: Type) = clientRepository.sendClientRequest(id,type)
    override suspend fun getAllClientContaining(
        search: String,
        searchType: SearchType,
        searchCategory: SearchCategory
    ) = clientRepository.getAllClientContaining(search,searchType,searchCategory)

    override suspend fun getAllClientUserContaining(
        search: String,
        searchType: SearchType,
        searchCategory: SearchCategory
    ) = clientRepository.getAllClientUserContaining(search,searchType,searchCategory)

    override suspend fun saveHistory(category: SearchCategory, id: Long) = clientRepository.saveHistory(category,id)
    override suspend fun getAllHistory() = clientRepository.getAllHistory()

    override suspend fun addProvider(provider: String, file: File) = providerRepository.addProvider(provider,file)
    override suspend fun addProviderWithoutImage(provider: String) = providerRepository.addProviderWithoutImage(provider)

    override suspend fun getAllMyPayments() = paymentRepository.getAllMyPayments()
    override suspend fun getAllMyPaymentsEspeceByDate(date: String, findate: String) = paymentRepository.getAllMyPaymentsEspeceByDate(date, findate = findate)

    override suspend fun getAllMyOrdersLines(companyId: Long) = orderRepository.getAllMyOrdersLines((companyId))
    override suspend fun getAllMyOrdersLinesByOrderId(orderId: Long) = orderRepository.getAllMyOrdersLinesByOrderId(orderId)

    override suspend fun getAllMyWorker(companyId : Long) = workerRepository.getAllMyWorker(companyId = companyId)
    override suspend fun getAllMyInvoicesAsProvider(companyId : Long) = invoiceRepository.getAllMyInvoicesAsProvider(companyId = companyId)
    override suspend fun getAllMyInvoicesAsClient(companyId : Long) = invoiceRepository.getAllMyInvoicesAsClient(companyId = companyId)
    override suspend fun getLastInvoiceCode() = invoiceRepository.getLastInvoiceCode()
    override suspend fun addInvoice(commandLineDtos: List<CommandLineDto>,
                                    clientId : Long, invoiceCode : Long,
                                    discount : Double, clientType : AccountType,
                                    invoiceMode: InvoiceMode) = invoiceRepository.addInvoice(commandLineDtos, clientId, invoiceCode,discount, clientType, invoiceMode)

    override suspend fun getAllMyInvoicesNotAccepted() = invoiceRepository.getAllMyInvoicesNotAccepted()

    override suspend fun getAllMyConversations() = messageRepository.getAllMyConversations()
    override suspend fun getAllMyMessageByConversationId(conversationId: Long) = messageRepository.getAllMyMessageByConversationId(conversationId)
    override suspend fun sendMessage(conversation: ConversationDto) = messageRepository.sendMessage(conversation)
    override suspend fun getConversationByCaleeId(id: Long, messageType: MessageType) = messageRepository.getConversationByCaleeId(id,messageType)
    override suspend fun getAllMessageByCaleeId(id: Long, typeype: AccountType)= messageRepository.getAllMessageByCaleeId(id, typeype)

    override suspend fun sendOrder(orderList: List<PurchaseOrderLineDto>) = orderRepository.sendOrder(orderList)
    override suspend fun test(order: PurchaseOrderLineDto) = shoppingRepository.test(order)
    override suspend fun orderLineResponse(status: String, id: Long, isAll : Boolean) = shoppingRepository.orderLineResponse(status,id, isAll)

    override suspend fun getAllMyOrders(companyId: Long) = orderRepository.getAllMyOrders(companyId)
    override suspend fun getAllOrdersLineByInvoiceId(invoiceId: Long) = orderRepository.getAllOrdersLineByInvoiceId(invoiceId)

    override suspend fun getAllMyInvetations() = invetationRepository.getAllMyInvetations()
    override suspend fun RequestResponse(status :Status ,id: Long) = invetationRepository.RequestResponse(status,id)
    override suspend fun cancelInvitation(id: Long) = invetationRepository.cancelInvitation(id)

    override suspend fun sendPoints(pointsPayment: PointsPaymentDto) = pointPaymentRepository.sendPoints(pointsPayment)
    override suspend fun getAllMyPointsPayment(companyId: Long) = pointPaymentRepository.getAllMyPointsPayment(companyId)
    override suspend fun getAllMyPaymentsEspece(companyId: Long) = pointPaymentRepository.getAllMyPaymentsEspece(companyId)
    override suspend fun getMyProfitByDate(beginDate: String, finalDate: String) = pointPaymentRepository.getMyProfitByDate(beginDate, finalDate)
    override suspend fun getAllMyProfits() = pointPaymentRepository.getAllMyProfits()
    override suspend fun getMyHistoryProfitByDate(beginDate: String, finalDate: String) = pointPaymentRepository.getMyHistoryProfitByDate(beginDate, finalDate)

    override suspend fun getAllMyRating(id: Long, type: AccountType) = ratingRepository.getAllMyRating(id, type)
    override suspend fun doRating(rating : String, image : File?) = ratingRepository.doRating(rating, image)
    override suspend fun makeAsPointSeller(status: Boolean, id: Long) = aymenRepository.makeAsPointSeller(status,id)
    override suspend fun getAllCommandLinesByInvoiceId(invoiceId: Long) = commandLineRepository.getAllCommandLinesByInvoiceId(invoiceId)

    override suspend fun SignIn(authenticationRequest: AuthenticationRequest) = signInRepository.SignIn(authenticationRequest)
    override suspend fun SignUp(registerRequest: RegisterRequest) = signInRepository.SignUp(registerRequest)
    override suspend fun refreshToken(token: String) = signInRepository.refreshToken(token)
    override suspend fun getMyUserDetails() = signInRepository.getMyUserDetails()
    override suspend fun updateLocations(latitude: Double, logitude: Double) = signInRepository.updateLocations(latitude, logitude)

    ////////////////////////////////////// locally function /////////////////////////////////////////////////////////////////////////
    override fun getAllArticlesLocaly(companyId : Long)= realmRepository.getAllArticlesLocaly(companyId)
    override fun getInventoryLocally() = realmRepository.getInventoryLocally()
    override fun getAllSubCategoriesLocally() = realmRepository.getAllSubCategoriesLocally()
    override fun getSubCategoriesByCategory(categoryId: Long) = realmRepository.getSubCategoriesByCategory(categoryId)
    override fun getAllCategoriesLocally() = realmRepository.getAllCategoriesLocally()
    override fun getAllMyClientLocally() = realmRepository.getAllMyClientLocally()
    override suspend fun getRandomArticles() = articleRepository.getRandomArticles()
    override suspend fun getRandomArticlesByCompanyCategory(categName : String) = articleRepository.getRandomArticlesByCompanyCategory(categName)
    override fun getRandomArticleLocally() =  realmRepository.getRandomArticleLocally()
    override fun getRandomArticleByCompanyCategoryLocally(categName : String) =  realmRepository.getRandomArticleByCompanyCategoryLocally(categName)
    override fun getAllMyPaymentsLocally() = realmRepository.getAllMyPaymentsLocally()
    override fun getAllMyOrdersLocally() = realmRepository.getAllMyOrdersLocally()
    override fun getAllMyWorkerLocally() = realmRepository.getAllMyWorkerLocally()
    override fun getMyParentLocally() = realmRepository.getMyParentLocally()
    override fun getAllMyOrdersLinesByOrderIdLocally(orderId: Long) = realmRepository.getAllMyOrdersLinesByOrderIdLocally(orderId)

    //    override fun getMyCompanyLocally() = realmRepository.getMyCompanyLocally()
    override fun getAllMyInvoicesAsProviderLocally(myCompanyId: Long) = realmRepository.getAllMyInvoicesAsProviderLocally(myCompanyId = myCompanyId)
    override fun getAllMyInvoicesAsClientLocally(myCompanyId: Long) = realmRepository.getAllMyInvoicesAsClientLocally(myCompanyId = myCompanyId)
    override fun getAllMyConversationsLocally() = realmRepository.getAllMyConversationsLocally()
    override fun getAllMyMessageByConversationIdLocally(conversationId : Long) = realmRepository.getAllMyMessageByConversationIdLocally(conversationId)
    override fun getAllMyInvetationsLocally() = realmRepository.getAllMyInvetationsLocally()
    override suspend fun makeItAsFav(article: ArticleCompany) = realmRepository.makeItAsFav(article)
    override suspend fun getCommentsLocally(articleId: Long) = realmRepository.getCommentsLocally(articleId)
    override suspend fun updateLastMessage(conversation: Conversation) = realmRepository.updateLastMessage(conversation)
    override suspend fun getAllMyPointsPaymentLocally() = realmRepository.getAllMyPointsPaymentLocally()
    override suspend fun deleteInvitation(id: Long) = realmRepository.deleteInvitation(id)
    override suspend fun getAllHistoryLocally() = realmRepository.getAllHistoryLocally()
    override suspend fun changeStatusLocally(status: String, id: Long, isAll : Boolean) = realmRepository.changeStatusLocally(status, id, isAll)
    override fun getAllMyPaymentsEspeceLocally(id: Long) = realmRepository.getAllMyPaymentsEspeceLocally(id)
    override fun getAllMyPaymentsHistoryLocally(id: Long) = realmRepository.getAllMyPaymentsHistoryLocally(id)
    override fun getAllMyProfitsLocally() = realmRepository.getAllMyProfitsLocally()
    override fun getAllArticlesByCategoryLocaly(myCompanyId: Long, myCompanyCategory: String) = realmRepository.getAllArticlesByCategoryLocaly(myCompanyId = myCompanyId, myCompanyCategory = myCompanyCategory)
    override fun getAllMyInvoicesNotAcceptedLocally(id: Long) = realmRepository.getAllMyInvoicesNotAcceptedLocally(id = id)

    override fun getAllMyProviderLocally() = realmRepository.getAllMyProviderLocally()

}