package com.aymen.store.model.repository.globalRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.RateType
import com.aymen.metastore.model.Enum.SearchPaymentEnum
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.dto.PointsPaymentDto
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
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.entity.dto.CashDto
import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.dto.RegisterRequest
import com.aymen.metastore.model.entity.dto.CommandLineDto
import com.aymen.metastore.model.entity.dto.CommentDto
import com.aymen.metastore.model.entity.dto.CompanyDto
import com.aymen.metastore.model.entity.dto.InvitationDto
import com.aymen.metastore.model.entity.dto.PurchaseOrderLineDto
import com.aymen.metastore.model.entity.dto.RatingDto
import com.aymen.metastore.model.entity.dto.ReglementFoProviderDto
import com.aymen.metastore.model.entity.dto.SearchHistoryDto
import com.aymen.metastore.model.entity.dto.SubCategoryDto
import com.aymen.metastore.model.entity.dto.TokenDto
import com.aymen.metastore.model.entity.dto.UserDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.entity.model.ClientProviderRelation
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.model.Invitation
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.Payment
import com.aymen.metastore.model.entity.model.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.model.PointsPayment
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.entity.model.Rating
import com.aymen.metastore.model.entity.model.SearchHistory
import com.aymen.metastore.model.entity.model.SubArticleModel
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.entity.model.Worker
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyOrUser
import com.aymen.metastore.model.entity.roomRelation.InventoryWithArticle
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import com.aymen.metastore.model.repository.remoteRepository.DeliveryRepository.DeliveryRepository
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepository
import com.aymen.store.model.repository.remoteRepository.invetationRepository.InvetationRepository
import com.aymen.store.model.repository.remoteRepository.shoppingRepository.ShoppingRepository
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import com.aymen.store.model.repository.remoteRepository.categoryRepository.CategoryRepository
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepository
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepository
import com.aymen.store.model.repository.remoteRepository.inventoryRepository.InventoryRepository
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
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
    private val shoppingRepository: ShoppingRepository,
    private val invetationRepository: InvetationRepository,
    private val pointPaymentRepository: PointPaymentRepository,
    private val ratingRepository: RatingRepository,
    private val aymenRepository: AymenRepository,
    private val commandLineRepository: CommandLineRepository,
    private val deliveryRepository: DeliveryRepository

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
//    override fun getArticleComments(articleId: Long): Flow<PagingData<Rating>> {
//        TODO()
//    }
    override suspend fun addQuantityArticle(quantity: Double, articleId: Long) = articleRepository.addQuantityArticle(quantity, articleId)
    override suspend fun updateArticle(article: ArticleCompanyDto) = articleRepository.updateArticle(article)
    override suspend fun addSubArticle(
        subArticle: List<SubArticleModel>
    ) = articleRepository.addSubArticle(subArticle)

    override fun getArticlesChilds(parentId: Long): Flow<PagingData<SubArticleModel>> {
        TODO("Not yet implemented")
    }

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
    override fun getAllMyProvider(companyId: Long, isAll: Boolean, search: String?): Flow<PagingData<ClientProviderRelation>> {
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


    override suspend fun updateCompany(company: String, file: File) = companyRepository.updateCompany(company,file)
    override suspend fun updateImage(image: File) = companyRepository.updateImage(image)
    override suspend fun checkRelation(id: Long, accountType: AccountType): Response<List<InvitationDto>> = companyRepository.checkRelation(
        id,
        accountType
    )

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
    override suspend fun sendClientRequest(id: Long, type: Type, isDeleted: Boolean) = clientRepository.sendClientRequest(
        id,
        type,
        isDeleted
    )
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

//    override fun getPaidInvoice(id: Long, isProvider: Boolean, paymentStatus: PaymentStatus): Flow<PagingData<Invoice>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getNotPaidInvoice(
//        id: Long,
//        isProvider: Boolean
//    ): Flow<PagingData<InvoiceWithClientPersonProvider>> {
//        TODO("Not yet implemented")
//    }

//    override fun getInCompleteInvoice(id: Long, isProvider: Boolean): Flow<PagingData<InvoiceWithClientPersonProvider>> {
//        TODO("Not yet implemented")
//    }

    override fun getNotAcceptedInvoice(
        id: Long,
        isProvider: Boolean,
        status: Status
    ): Flow<PagingData<Invoice>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendRaglement(
        companyId: Long,
        cashDto: CashDto
    ) = paymentRepository.sendRaglement(companyId,cashDto)

    override fun getPaymentHystoricByInvoiceId(invoiceId: Long): Flow<PagingData<Payment>> {
        TODO("Not yet implemented")
    }

    override fun getAllMyOrdersNotAccepted(id: Long): Flow<PagingData<PurchaseOrder>> {
        TODO("Not yet implemented")
    }

    override fun getPurchaqseOrderDetails(orderId: Long): Flow<PagingData<PurchaseOrderLine>> {
        TODO("Not yet implemented")
    }


    override suspend fun getAllMyOrdersLines(companyId: Long) = orderRepository.getAllMyOrdersLines((companyId))
    override suspend fun getAllMyOrdersLinesByOrderId(orderId: Long) = orderRepository.getAllMyOrdersLinesByOrderId(orderId)
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
    ): Flow<PagingData<CommandLine>> {
        TODO("Not yet implemented")
    }


    override suspend fun getLastInvoiceCode(asProvider: Boolean) = invoiceRepository.getLastInvoiceCode(asProvider)
    override suspend fun addInvoice(
        commandLineDtos: List<CommandLine>,
        clientId: Long, invoiceCode: Long,
        discount: Double, clientType: AccountType,
        invoiceMode: InvoiceMode,
        asProvider: Boolean
    ): Response<List<CommandLineDto>> = invoiceRepository.addInvoice(
        commandLineDtos,
        clientId,
        invoiceCode,
        discount,
        clientType,
        invoiceMode,
        asProvider
    )
    override suspend fun getAllMyInvoicesAsClientAndStatus(id : Long , status : Status) = invoiceRepository.getAllMyInvoicesAsClientAndStatus(id , status)
    override suspend fun accepteInvoice(invoiceId: Long, status: Status) = invoiceRepository.accepteInvoice(invoiceId, status)
     override suspend fun getAllMyPaymentNotAccepted(companyId: Long) = invoiceRepository.getAllMyPaymentNotAccepted(companyId)
    override fun searchInvoice(
        type: SearchPaymentEnum,
        text: String,
        companyId: Long
    ): Flow<PagingData<Invoice>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteInvoiceById(invoiceId: Long) = invoiceRepository.deleteInvoiceById(invoiceId)
    override suspend fun acceptInvoiceAsDelivery(orderId: Long): Response<Boolean> = invoiceRepository.acceptInvoiceAsDelivery(orderId)
    override suspend fun submitOrderDelivered(orderId: Long, code: String): Response<Boolean> = invoiceRepository.submitOrderDelivered(orderId, code)
    override suspend fun userRejectOrder(orderId: Long) = invoiceRepository.userRejectOrder(orderId)

    override fun getAllOrdersNotDelivered(id: Long): Flow<PagingData<PurchaseOrder>> {
        TODO("Not yet implemented")
    }


    override suspend fun sendOrder(orderList: List<PurchaseOrderLine>) = orderRepository.sendOrder(orderList)
    override suspend fun test(order: PurchaseOrderLineDto) = shoppingRepository.test(order)
    override suspend fun orderLineResponse(status: Status, ids : List<Long>): Response<Double> = shoppingRepository.orderLineResponse(status,ids)
    override fun getAllMyInvetations(companyId: Long): Flow<PagingData<Invitation>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMyOrders(companyId: Long) = orderRepository.getAllMyOrders(companyId)
    override fun getAllOrdersLineByInvoiceId(
        companyId: Long,
        invoiceId: Long
    ): Flow<PagingData<PurchaseOrderLine>> {
        TODO("Not yet implemented")
    }

    override fun getAllMyWorker(companyId: Long): Flow<PagingData<Worker>> {
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
    override suspend fun sendReglement(reglement: ReglementFoProviderDto) = pointPaymentRepository.sendReglement(reglement)

    override suspend fun getMyProfitByDate(beginDate: String, finalDate: String) = pointPaymentRepository.getMyProfitByDate(beginDate, finalDate)
    override suspend fun getAllMyProfits() = pointPaymentRepository.getAllMyProfits()
    override fun getMyHistoryProfitByDate(
        id: Long,
        beginDate: String,
        finalDate: String
    ): Flow<PagingData<PaymentPerDayWithProvider>> {
        TODO("Not yet implemented")
    }

    override fun getPaymentForProviderDetails(paymentId: Long) = pointPaymentRepository.getPaymentForProviderDetails(paymentId)


    override fun getAllMyRating(id: Long, type: RateType) : Flow<PagingData<Rating>>{
        TODO()
    }
     override suspend fun doRating(rating: String, image: File?): Response<RatingDto> = ratingRepository.doRating(rating, image)
    override suspend fun enabledToCommentCompany(companyId : Long) = ratingRepository.enabledToCommentCompany(companyId = companyId)
    override suspend fun enabledToCommentUser(userId: Long) = ratingRepository.enabledToCommentUser(userId)
    override suspend fun enabledToCommentArticle(companyId: Long) = ratingRepository.enabledToCommentArticle(companyId)
    override suspend fun makeAsPointSeller(status: Boolean, id: Long) = aymenRepository.makeAsPointSeller(status,id)
    override suspend fun makeAsMetaSeller(status: Boolean, id: Long) = aymenRepository.makeAsMetaSeller(status, id)

    override suspend fun getAllCommandLinesByInvoiceId(invoiceId: Long): Response<List<CommandLineDto>> = commandLineRepository.getAllCommandLinesByInvoiceId(invoiceId)
    override suspend fun addAsDelivery(userId: Long): Response<AccountType> = deliveryRepository.addAsDelivery(userId)
    override fun getInvoicesIdelevered(): Flow<PagingData<PurchaseOrder>> {
        TODO("Not yet implemented")
    }

    override suspend fun SignIn(authenticationRequest: AuthenticationRequest) = signInRepository.SignIn(authenticationRequest)
    override suspend fun SignUp(registerRequest: RegisterRequest) = signInRepository.SignUp(registerRequest)
    override suspend fun refreshToken(token: String) = signInRepository.refreshToken(token)
    override suspend fun sendMyDeviceToken(token: TokenDto) = signInRepository.sendMyDeviceToken(token)

    override suspend fun getMyUserDetails() = signInRepository.getMyUserDetails()
    override suspend fun updateLocations(latitude: Double, logitude: Double) = signInRepository.updateLocations(latitude, logitude)
    override suspend fun sendVerificationCodeViaEmail(
        username: String,
        email: String
    ) = signInRepository.sendVerificationCodeViaEmail(username , email)

    override suspend fun verificationCode(
        username: String,
        email: String,
        code: String
    ) = signInRepository.verificationCode(username , email , code )

    override suspend fun changePassword(
        username: String,
        email: String,
        password: String
    ): Response<AuthenticationResponse> = signInRepository.changePassword(username, email , password)

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
        companyId: Long,
        asProvider: Boolean
    ): Flow<PagingData<ArticleCompany>> {
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
    ): Flow<PagingData<ArticleCompany>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteArticle(id: Long) = articleRepository.deleteArticle(id)

}