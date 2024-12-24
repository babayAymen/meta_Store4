package com.aymen.store.model.repository.globalRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.dto.PointsPaymentDto
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.repository.remoteRepository.CommandLineRepository.CommandLineRepository
import com.aymen.metastore.model.repository.remoteRepository.aymenRepository.AymenRepository
import com.aymen.metastore.model.repository.remoteRepository.ratingRepository.RatingRepository
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type
import com.aymen.metastore.model.entity.dto.AuthenticationRequest
import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.dto.RegisterRequest
import com.aymen.metastore.model.entity.dto.CommandLineDto
import com.aymen.metastore.model.entity.dto.CommentDto
import com.aymen.metastore.model.entity.dto.CompanyDto
import com.aymen.metastore.model.entity.dto.ConversationDto
import com.aymen.metastore.model.entity.dto.PurchaseOrderLineDto
import com.aymen.metastore.model.entity.dto.SearchHistoryDto
import com.aymen.metastore.model.entity.dto.SubCategoryDto
import com.aymen.metastore.model.entity.dto.UserDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.model.PointsPayment
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.entity.model.SearchHistory
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.entity.roomRelation.CommandLineWithInvoiceAndArticle
import com.aymen.metastore.model.entity.roomRelation.CommentWithArticleAndUserOrCompany
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyOrUser
import com.aymen.metastore.model.entity.roomRelation.ConversationWithUserOrCompany
import com.aymen.metastore.model.entity.roomRelation.InventoryWithArticle
import com.aymen.metastore.model.entity.roomRelation.InvitationWithClientOrWorkerOrCompany
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.model.entity.roomRelation.MessageWithCompanyAndUserAndConversation
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider
import com.aymen.metastore.model.entity.roomRelation.PurchaseOrderLineWithPurchaseOrderOrInvoice
import com.aymen.metastore.model.entity.roomRelation.SearchHistoryWithClientOrProviderOrUserOrArticle
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepository
import com.aymen.store.model.repository.remoteRepository.invetationRepository.InvetationRepository
import com.aymen.store.model.repository.remoteRepository.shoppingRepository.ShoppingRepository
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import com.aymen.store.model.repository.remoteRepository.categoryRepository.CategoryRepository
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepository
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepository
import com.aymen.store.model.repository.remoteRepository.inventoryRepository.InventoryRepository
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import com.aymen.metastore.model.repository.remoteRepository.messageRepository.MessageRepository
import com.aymen.metastore.util.Resource
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.metastore.model.repository.remoteRepository.orderRepository.OrderRepository
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepository
import com.aymen.store.model.repository.remoteRepository.providerRepository.ProviderRepository
import com.aymen.store.model.repository.remoteRepository.signInRepository.SignInRepository
import com.aymen.store.model.repository.remoteRepository.subCategoryRepository.SubCategoryRepository
import com.aymen.store.model.repository.remoteRepository.workerRepository.WorkerRepository
import kotlinx.coroutines.flow.Flow
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
    override suspend fun addArticleWithoutImage(article: ArticleCompanyDto, articleId: Long): Response<ArticleCompanyDto> {
        return articleRepository.addArticleWithoutImage(article, articleId)
    }
    override suspend fun getAllArticlesContaining(search: String, searchType : SearchType) = articleRepository.getAllArticlesContaining(search, searchType)
    override suspend fun likeAnArticle(articleId: Long, isFav : Boolean) = articleRepository.likeAnArticle(articleId, isFav)
    override suspend fun sendComment(comment: CommentDto) = articleRepository.sendComment(comment)
    override fun getArticleComments(articleId: Long): Flow<PagingData<CommentWithArticleAndUserOrCompany>>{
        TODO()
    }
    override suspend fun addQuantityArticle(quantity: Double, articleId: Long) = articleRepository.addQuantityArticle(quantity, articleId)
    override suspend fun updateArticle(article: ArticleCompanyDto) = articleRepository.updateArticle(article)

    override suspend fun addCategory(category: String, file: File?) = categoryRepository.addCategory(category, file)
    override suspend fun updateCategory(category: String, file: File?) = categoryRepository.updateCategory(category,file)

     override fun getAllCategory(companyId: Long): Flow<PagingData<Category>> {
        TODO("Not yet implemented")
    }

    override fun getCategoryTemp(companyId: Long): Flow<PagingData<Category>> {
        TODO("Not yet implemented")
    }

    override fun getSubCategoryByCategory(id: Long, companyId : Long): Flow<PagingData<SubCategory>> {
        TODO("Not yet implemented")
    }

    override suspend fun addSubCtagory(sousCategory: String, file: File?): Response<SubCategoryDto> = subCategoryRepository.addSubCtagory(sousCategory,file)
    override suspend fun updateSubCategory(
        sousCategory: String,
        file: File?
    ) = subCategoryRepository.updateSubCategory(sousCategory, file)

    override fun getAllSubCategories(companyId: Long): Flow<PagingData<SubCategoryWithCategory>> {
        TODO("Not yet implemented")
    }

    override fun getAllSubCategoriesByCompanyId(companyId: Long): Flow<PagingData<SubCategoryWithCategory>> {
        TODO("Not yet implemented")
    }


    override suspend fun addCompany(company: String, file : File) = companyRepository.addCompany(company, file)
    override fun getAllMyProvider(companyId: Long): Flow<PagingData<CompanyWithCompanyOrUser>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyParent(companyId : Long) = companyRepository.getMyParent(companyId = companyId)
    override suspend fun getMeAsCompany() = companyRepository.getMeAsCompany()
    override fun getAllCompaniesContaining(
        search: String,
        searchType: SearchType,
        myId: Long
    ): Flow<PagingData<CompanyDto>> {
        TODO("Not yet implemented")
    }


    override fun getAllMyClientContaining(
        id: Long,
        clientName: String
    ): Flow<PagingData<SearchHistoryWithClientOrProviderOrUserOrArticle>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCompany(company: String, file: File) = companyRepository.updateCompany(company,file)
    override suspend fun updateImage(image: File) = companyRepository.updateImage(image)
    override fun getInventory(companyId: Long): Flow<PagingData<InventoryWithArticle>> {
        TODO("Not yet implemented")
    }

    override fun getAllMyClient(companyId: Long): Flow<PagingData<CompanyWithCompanyOrUser>> = clientRepository.getAllMyClient(
        companyId = companyId)

    override fun getAllClientUserContaining(
        companyId: Long,
        searchType: SearchType,
        search: String
    ): Flow<PagingData<UserDto>> {
        TODO("Not yet implemented")
    }

    override fun getMyClientForAutocompleteClient(
        companyId: Long,
        clientName: String
    ): Flow<PagingData<ClientProviderRelationDto>> {
        TODO("Not yet implemented")
    }

 


    override suspend fun addClient(client: String, file: File?) = clientRepository.addClient(client, file)
    override suspend fun updateClient(client: String, file: File?) = clientRepository.updateClient(client , file)
    override suspend fun deleteClient(relationId: Long) = clientRepository.deleteClient(relationId)

    override suspend fun getAllMyClientContaining(clientName: String,companyId : Long) = clientRepository.getAllMyClientContaining(clientName,companyId = companyId)
    override suspend fun sendClientRequest(id: Long, type: Type) = clientRepository.sendClientRequest(id,type)
    override suspend fun getAllClientContaining(search: String, searchType: SearchType, searchCategory: SearchCategory) = clientRepository.getAllClientContaining(search,searchType,searchCategory)
    override suspend fun saveHistory(category: SearchCategory, id: Long): Response<SearchHistoryDto> = clientRepository.saveHistory(category,id)
    override suspend fun deleteSearch(id: Long) = clientRepository.deleteSearch(id)

    override fun getAllHistory(id: Long): Flow<PagingData<SearchHistory>> {
        TODO("Not yet implemented")
    }
   override suspend fun addProvider(provider: String, file: File?): Response<ClientProviderRelationDto> = providerRepository.addProvider(provider,file)
    override suspend fun updateProvider(provider: String, file: File?) = providerRepository.updateProvider(provider,file)

    override suspend fun deleteProvider(id: Long) = providerRepository.deleteProvider(id)

//    override suspend fun addProviderWithoutImage(provider: String) = providerRepository.addProviderWithoutImage(provider)
     override suspend fun getAllMyPaymentsEspeceByDate(date: String, findate: String) = paymentRepository.getAllMyPaymentsEspeceByDate(date, findate = findate)
    override fun getAllMyPaymentsEspeceByDate(
        id: Long,
        beginDate: String,
        finalDate: String
    ): Flow<PagingData<PaymentForProvidersWithCommandLine>> {
        TODO("Not yet implemented")
    }

    override fun getAllMyBuyHistory(id: Long): Flow<PagingData<InvoiceWithClientPersonProvider>> {
        TODO("Not yet implemented")
    }

    override fun getPaidInvoice(id: Long, isProvider: Boolean, paymentStatus: PaymentStatus): Flow<PagingData<Invoice>> {
        TODO("Not yet implemented")
    }

    override fun getNotPaidInvoice(
        id: Long,
        isProvider: Boolean
    ): Flow<PagingData<InvoiceWithClientPersonProvider>> {
        TODO("Not yet implemented")
    }

    override fun getInCompleteInvoice(id: Long, isProvider: Boolean): Flow<PagingData<InvoiceWithClientPersonProvider>> {
        TODO("Not yet implemented")
    }

    override fun getNotAcceptedInvoice(
        id: Long,
        isProvider: Boolean,
        status: Status
    ): Flow<PagingData<Invoice>> {
        TODO("Not yet implemented")
    }

    override fun getAllMyOrdersNotAccepted(id: Long): Flow<PagingData<PurchaseOrder>> {
        TODO("Not yet implemented")
    }

    override fun getPurchaqseOrderDetails(orderId: Long): Flow<PagingData<PurchaseOrderLineWithPurchaseOrderOrInvoice>> {
        TODO("Not yet implemented")
    }


    override suspend fun getAllMyOrdersLines(companyId: Long) = orderRepository.getAllMyOrdersLines((companyId))
    override suspend fun getAllMyOrdersLinesByOrderId(orderId: Long) = orderRepository.getAllMyOrdersLinesByOrderId(orderId)
    override suspend fun getAllMyWorker(companyId : Long) = workerRepository.getAllMyWorker(companyId = companyId)
    override fun getAllMyInvoicesAsProvider(
        companyId: Long,
        isProvider: Boolean,
        status: PaymentStatus
    ): Flow<PagingData<Invoice>> {
        TODO("Not yet implemented")
    }

    override fun getAllInvoicesAsClient(
        clientId: Long,
        accountType: AccountType,
        status: PaymentStatus
    ): Flow<PagingData<Invoice>> {
        TODO("Not yet implemented")
    }

    override fun getAllInvoicesAsClientAndStatus(
        clientId: Long,
        status: Status
    ): Flow<PagingData<Invoice>> {
        TODO("Not yet implemented")
    }

    override fun getAllCommandLineByInvoiceId(
        companyId: Long,
        invoiceId: Long
    ): Flow<PagingData<CommandLineWithInvoiceAndArticle>> {
        TODO("Not yet implemented")
    }


    override suspend fun getLastInvoiceCode() = invoiceRepository.getLastInvoiceCode()
    override suspend fun addInvoice(
        commandLineDtos: List<CommandLine>,
        clientId: Long, invoiceCode: Long,
        discount: Double, clientType: AccountType,
        invoiceMode: InvoiceMode) = invoiceRepository.addInvoice(commandLineDtos, clientId, invoiceCode,discount, clientType, invoiceMode)
    override suspend fun getAllMyInvoicesAsClientAndStatus(id : Long , status : Status) = invoiceRepository.getAllMyInvoicesAsClientAndStatus(id , status)
    override suspend fun accepteInvoice(invoiceId: Long, status: Status) = invoiceRepository.accepteInvoice(invoiceId, status)
     override suspend fun getAllMyPaymentNotAccepted(companyId: Long) = invoiceRepository.getAllMyPaymentNotAccepted(companyId)
    override suspend fun sendMessage(conversation: ConversationDto) = messageRepository.sendMessage(conversation)
    override suspend fun getConversationByCaleeId(id: Long, messageType: MessageType) = messageRepository.getConversationByCaleeId(id,messageType)
    override fun getAllConversation(): Flow<PagingData<ConversationWithUserOrCompany>> {
        TODO("Not yet implemented")
    }

    override fun getAllMessagesByConversationId(
        conversationId: Long,
        accountType: AccountType
    ): Flow<PagingData<MessageWithCompanyAndUserAndConversation>> {
        TODO("Not yet implemented")
    }

     override suspend fun sendOrder(orderList: List<PurchaseOrderLine>) = orderRepository.sendOrder(orderList)
    override suspend fun test(order: PurchaseOrderLineDto) = shoppingRepository.test(order)
    override suspend fun orderLineResponse(status: Status, id: Long, isAll: Boolean): Response<Double> = shoppingRepository.orderLineResponse(status,id, isAll)
    override fun getAllMyInvetations(): Flow<PagingData<InvitationWithClientOrWorkerOrCompany>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMyOrders(companyId: Long) = orderRepository.getAllMyOrders(companyId)
    override fun getAllOrdersLineByInvoiceId(
        companyId: Long,
        invoiceId: Long
    ): Flow<PagingData<PurchaseOrderLine>> {
        TODO("Not yet implemented")
    }

    override suspend fun RequestResponse(status :Status ,id: Long) = invetationRepository.RequestResponse(status,id)
    override suspend fun cancelInvitation(id: Long) = invetationRepository.cancelInvitation(id)


    override fun getAllRechargeHistory(id: Long): Flow<PagingData<PointsPayment>> {
        TODO("Not yet implemented")
    }

    override fun getAllMyProfitsPerDay(companyId: Long): Flow<PagingData<PaymentForProviderPerDay>> {
        TODO("Not yet implemented")
    }

    override fun getAllMyPointsPaymentForPoviders(companyId: Long): Flow<PagingData<PaymentForProvidersWithCommandLine>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendPoints(pointsPayment: PointsPaymentDto) = pointPaymentRepository.sendPoints(pointsPayment)
    override fun getAllMyPaymentsEspece(companyId: Long): Flow<PagingData<PaymentForProvidersWithCommandLine>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyProfitByDate(beginDate: String, finalDate: String) = pointPaymentRepository.getMyProfitByDate(beginDate, finalDate)
    override suspend fun getAllMyProfits() = pointPaymentRepository.getAllMyProfits()
    override fun getMyHistoryProfitByDate(
        id: Long,
        beginDate: String,
        finalDate: String
    ): Flow<PagingData<PaymentPerDayWithProvider>> {
        TODO("Not yet implemented")
    }



    override suspend fun getAllMyRating(id: Long, type: AccountType) = ratingRepository.getAllMyRating(id, type)
     override suspend fun doRating(rating : String, image : File?) = ratingRepository.doRating(rating, image)
    override suspend fun enabledToCommentCompany(companyId : Long) = ratingRepository.enabledToCommentCompany(companyId = companyId)
    override suspend fun enabledToCommentUser(userId: Long) = ratingRepository.enabledToCommentUser(userId)
    override suspend fun enabledToCommentArticle(companyId: Long) = ratingRepository.enabledToCommentArticle(companyId)
    override suspend fun makeAsPointSeller(status: Boolean, id: Long) = aymenRepository.makeAsPointSeller(status,id)
    override suspend fun getAllCommandLinesByInvoiceId(invoiceId: Long): Response<List<CommandLineDto>> = commandLineRepository.getAllCommandLinesByInvoiceId(invoiceId)
    override suspend fun SignIn(authenticationRequest: AuthenticationRequest) = signInRepository.SignIn(authenticationRequest)
    override suspend fun SignUp(registerRequest: RegisterRequest) = signInRepository.SignUp(registerRequest)
    override suspend fun refreshToken(token: String) = signInRepository.refreshToken(token)
    override suspend fun getMyUserDetails() = signInRepository.getMyUserDetails()
    override suspend fun updateLocations(latitude: Double, logitude: Double) = signInRepository.updateLocations(latitude, logitude)
    override suspend fun getRandomArticlesByCompanyCategory(categName : String) = articleRepository.getRandomArticlesByCompanyCategory(categName)
    override suspend fun getRandomArticlesByCategory(categoryId: Long, companyId : Long) = articleRepository.getRandomArticlesByCategory(categoryId, companyId)
      override suspend fun getRandomArticlesBySubCategory(
        subcategoryId: Long,
        companyId: Long
    ) = articleRepository.getRandomArticlesBySubCategory(subcategoryId, companyId)

    override fun getAllMyArticles(companyId: Long): Flow<PagingData<ArticleCompany>> {
        TODO("Not yet implemented")
    }

    override fun getRandomArticles(categoryName: CompanyCategory, companyId: Long?): Flow<PagingData<ArticleCompany>> {
        TODO("Not yet implemented")
    }

    override fun getArticleDetails(id: Long): Flow<Resource<ArticleCompany>> {
        TODO("Not yet implemented")
    }

    override fun getAllMyArticleContaining(
        libelle: String,
        searchType: SearchType,
        companyId: Long
    ): Flow<PagingData<ArticleCompanyDto>> {
        TODO("Not yet implemented")
    }

    override fun getAllArticlesByCategor(
        companyId: Long,
        companyCategory: CompanyCategory
    ): Flow<PagingData<Article>> {
        TODO("Not yet implemented")
    }

    override suspend fun getArticleByBarcode(bareCode: String) = articleRepository.getArticleByBarcode(bareCode)
    override fun getAllCompanyArticles(companyId: Long): Flow<PagingData<ArticleWithArticleCompany>> {
        TODO("Not yet implemented")
    }

    override fun getArticlesByCompanyAndCategoryOrSubCategory(
        companyId: Long,
        categoryId: Long,
        subcategoryId: Long
    ): Flow<PagingData<ArticleCompanyDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteArticle(id: Long) = articleRepository.deleteArticle(id)

}