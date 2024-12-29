package com.aymen.metastore.dependencyInjection

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.room.Room
import com.aymen.metastore.accounttypedtodatastore
import com.aymen.metastore.companydtodatastore
import com.aymen.metastore.datastore
import com.aymen.metastore.model.Location.DefaultLocationClient
import com.aymen.metastore.model.Location.LocationClient
import com.aymen.metastore.model.ViewModelRunTracker
import com.aymen.metastore.model.entity.dao.CompanyDao
import com.aymen.metastore.model.entity.dao.PurchaseOrderDao
import com.aymen.metastore.model.entity.dao.PurchaseOrderLineDao
import com.aymen.metastore.model.entity.dao.UserDao
import com.aymen.metastore.model.repository.remoteRepository.aymenRepository.AymenRepository
import com.aymen.metastore.model.repository.remoteRepository.aymenRepository.AymenRepositoryImpl
import com.aymen.metastore.model.repository.remoteRepository.ratingRepository.RatingRepository
import com.aymen.metastore.model.repository.remoteRepository.ratingRepository.RatingRepositoryImpl
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.repository.remoteRepository.CommandLineRepository.CommandLineRepository
import com.aymen.metastore.model.repository.remoteRepository.CommandLineRepository.CommandLineRepositoryImpl
import com.aymen.metastore.model.usecase.GetPagingCategoryByCompany
import com.aymen.metastore.model.usecase.GetPagingSubCategoryByCompany
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.userdtodatastore
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.metastore.model.repository.ViewModel.ClientViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.model.repository.ViewModel.InventoryViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.metastore.model.repository.ViewModel.SignInViewModel
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import com.aymen.store.model.repository.globalRepository.GlobalRepositoryImpl
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepository
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.invetationRepository.InvetationRepository
import com.aymen.store.model.repository.remoteRepository.shoppingRepository.ShoppingRepository
import com.aymen.store.model.repository.remoteRepository.shoppingRepository.ShoppingRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import com.aymen.metastore.model.repository.remoteRepository.articleRepository.ArticleRepositoryImpl
import com.aymen.metastore.model.usecase.GetArticleDetails
import com.aymen.metastore.model.usecase.GetPagingArticleCompanyByCompany
import com.aymen.metastore.model.usecase.GetRandomArticle
import com.aymen.store.model.repository.remoteRepository.categoryRepository.CategoryRepository
import com.aymen.store.model.repository.remoteRepository.categoryRepository.CategoryRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepository
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepository
import com.aymen.metastore.model.repository.remoteRepository.companyRepository.CompanyRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.inventoryRepository.InventoryRepository
import com.aymen.store.model.repository.remoteRepository.inventoryRepository.InventoryRepositoryImpl
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import com.aymen.metastore.model.repository.remoteRepository.invoiceRepository.InvoiceRepositoryImpl
import com.aymen.metastore.model.repository.remoteRepository.messageRepository.MessageRepository
import com.aymen.metastore.model.usecase.GetAllCompaniesContaining
import com.aymen.metastore.model.usecase.GetAllConversation
import com.aymen.metastore.model.usecase.GetAllInvoices
import com.aymen.metastore.model.usecase.GetAllInvoicesAsClient
import com.aymen.metastore.model.usecase.GetAllInvoicesAsClientAndStatus
import com.aymen.metastore.model.usecase.GetAllMessagesByConversation
import com.aymen.metastore.model.usecase.GetAllMyArticleContaining
import com.aymen.metastore.model.usecase.GetAllMyClient
import com.aymen.metastore.model.usecase.GetAllMyClientContaining
import com.aymen.metastore.model.usecase.GetAllMyInventory
import com.aymen.metastore.model.usecase.GetAllMyInvitations
import com.aymen.metastore.model.usecase.GetAllMyPaymentsEspece
import com.aymen.metastore.model.usecase.GetAllMyPaymentsEspeceByDate
import com.aymen.metastore.model.usecase.GetAllMyPointsPaymentForPoviders
import com.aymen.metastore.model.usecase.GetAllMyProviders
import com.aymen.metastore.model.usecase.GetAllPersonContaining
import com.aymen.metastore.model.usecase.GetAllRechargeHistory
import com.aymen.metastore.model.usecase.GetArticlesForCompanyByCompanyCategory
import com.aymen.store.model.repository.remoteRepository.messageRepository.MessageRepositoryImpl
import com.aymen.metastore.model.repository.remoteRepository.orderRepository.OrderRepository
import com.aymen.metastore.model.repository.remoteRepository.orderRepository.OrderRepositoryImpl
import com.aymen.metastore.model.usecase.GetAllMyBuyHistory
import com.aymen.metastore.model.usecase.GetAllMyOrdersNotAccepted
import com.aymen.metastore.model.usecase.GetAllMyProfitsPerDay
import com.aymen.metastore.model.usecase.GetAllOrdersLineByInvoiceId
import com.aymen.metastore.model.usecase.GetAllSearchHistory
import com.aymen.metastore.model.usecase.GetAllSubCategoryByCategoryId
import com.aymen.metastore.model.usecase.GetInCompleteInvoice
import com.aymen.metastore.model.usecase.GetMyHistoryProfitByDate
import com.aymen.metastore.model.usecase.GetNotAcceptedInvoice
import com.aymen.metastore.model.usecase.GetNotPaidInvoice
import com.aymen.metastore.model.usecase.GetPaidInvoice
import com.aymen.metastore.model.usecase.GetPurchaseOrderDetails
import com.aymen.metastore.util.BarcodeScanner
import com.aymen.store.dependencyInjection.TokenSerializer
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepository
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.providerRepository.ProviderRepository
import com.aymen.store.model.repository.remoteRepository.providerRepository.ProviderRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.signInRepository.SignInRepository
import com.aymen.store.model.repository.remoteRepository.signInRepository.SignInRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.subCategoryRepository.SubCategoryRepository
import com.aymen.metastore.model.repository.remoteRepository.subCategoryRepository.SubCategoryRepositoryImpl
import com.aymen.metastore.model.usecase.GetAllCommandLineByInvoiceId
import com.aymen.metastore.model.usecase.GetAllCompanyArticles
import com.aymen.metastore.model.usecase.GetAllSubCategoriesByCompanyId
import com.aymen.metastore.model.usecase.GetAllWorkers
import com.aymen.metastore.model.usecase.GetArticleComment
import com.aymen.metastore.model.usecase.GetArticlesByCompanyAndCategoryOrSubCategory
import com.aymen.metastore.model.usecase.GetCategoryTemp
import com.aymen.metastore.model.usecase.GetMyClientForAutocompleteClient
import com.aymen.metastore.model.usecase.GetPaymentForProviderDetails
import com.aymen.metastore.model.webSocket.ChatClient
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.DATABASE_NAME
import com.aymen.store.model.repository.remoteRepository.invetationRepository.InvetationRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.workerRepository.WorkerRepository
import com.aymen.store.model.repository.remoteRepository.workerRepository.WorkerRepositoryImpl
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class MetaStoreModule {

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .setQueryCallback({ sqlQuery, bindArgs ->
            Log.d("RoomQuery", "SQL: $sqlQuery, Args: $bindArgs")
        }, Executors.newSingleThreadExecutor())
            .build()

    }
    @Provides
    @Singleton
    fun provideChatClient(): ChatClient {
        return ChatClient()
    }
    @Provides
    @Singleton
    fun provideMetaUseCases(categoryRepository: CategoryRepository, subCategoryRepository: SubCategoryRepository, articleRepository: ArticleRepository,
                            clientRepository: ClientRepository, companyRepository: CompanyRepository, messageRepository: MessageRepository,
                            invoiceRepository: InvoiceRepository, pointPaymentRepository: PointPaymentRepository,inventoryRepository: InventoryRepository,
                            invetationRepository: InvetationRepository, orderRepository: OrderRepository, paymentRepository: PaymentRepository,
                            workerRepository : WorkerRepository): MetaUseCases{
        return MetaUseCases(
            getPagingCategoryByCompany = GetPagingCategoryByCompany(repository = categoryRepository),
            getPagingSubCategoryByCompany = GetPagingSubCategoryByCompany(repository = subCategoryRepository),
            getPagingArticleCompanyByCompany = GetPagingArticleCompanyByCompany(repository = articleRepository),
            getRandomArticle = GetRandomArticle(repository = articleRepository),
            getArticleDetails = GetArticleDetails(repository = articleRepository),
            getAllMyArticleContaining = GetAllMyArticleContaining(repository = articleRepository),
            getAllMyClient = GetAllMyClient(repository = clientRepository),
            getAllMyClientContaining = GetAllMyClientContaining(repository = companyRepository),
            getAllMessagesByConversation = GetAllMessagesByConversation(repository = messageRepository),
            getAllConversation = GetAllConversation(repository = messageRepository),
            getAllInvoices = GetAllInvoices(repository = invoiceRepository),
            getAllRechargeHistory = GetAllRechargeHistory(repository = pointPaymentRepository),
            getAllInvoicesAsClient = GetAllInvoicesAsClient(repository = invoiceRepository),
            getAllInvoicesAsClientAndStatus = GetAllInvoicesAsClientAndStatus(repository = invoiceRepository),
            getAllMyInventory = GetAllMyInventory(repository = inventoryRepository),
            getAllCompaniesContaining = GetAllCompaniesContaining(repository = companyRepository),
            getAllMyInvitations = GetAllMyInvitations(repository = invetationRepository),
            getAllMyPaymentsEspece = GetAllMyPaymentsEspece(repository = pointPaymentRepository),
            getAllMyPaymentsEspeceByDate = GetAllMyPaymentsEspeceByDate(repository = pointPaymentRepository),
            getAllMyPointsPaymentForProvider = GetAllMyPointsPaymentForPoviders(repository = pointPaymentRepository),
            getAllPersonContaining = GetAllPersonContaining(repository = clientRepository),
            getArticlesForCompanyByCompanyCategory = GetArticlesForCompanyByCompanyCategory(repository = articleRepository),
            getAllMyProviders = GetAllMyProviders(repository = companyRepository),
            getAllMyOrdersNotAccepted = GetAllMyOrdersNotAccepted(repository = orderRepository),
            getPurchaseOrderDetails = GetPurchaseOrderDetails(repository = orderRepository),
            getAllMyBuyHistory = GetAllMyBuyHistory(repository = paymentRepository),
            getPaidInvoice = GetPaidInvoice(repository = paymentRepository),
            getNotPaidInvoice = GetNotPaidInvoice(repository = paymentRepository),
            getNotAcceptedInvoice = GetNotAcceptedInvoice(repository = paymentRepository),
            getInCompleteInvoice = GetInCompleteInvoice(repository = paymentRepository),
            getAllMyProfitsPerDay = GetAllMyProfitsPerDay(repository = pointPaymentRepository),
            getMyHistoryProfitByDate = GetMyHistoryProfitByDate(repository = pointPaymentRepository),
            getAllSearchHistory = GetAllSearchHistory(repository = clientRepository),
            getAllSubCategoryByCategoryId = GetAllSubCategoryByCategoryId(repository = subCategoryRepository),
            getAllOrdersLineByInvoiceId = GetAllOrdersLineByInvoiceId(repository = orderRepository),
            getAllCompanyArticles = GetAllCompanyArticles(repository = articleRepository),
            getAllSubCategoriesByCompanyId = GetAllSubCategoriesByCompanyId(repository = subCategoryRepository),
            getArticlesByCompanyAndCategoryOrSubCategory = GetArticlesByCompanyAndCategoryOrSubCategory(repository = articleRepository),
            getAllCommandLineByInvoiceId = GetAllCommandLineByInvoiceId(repository = invoiceRepository),
            getMyClientForAutocompleteClient = GetMyClientForAutocompleteClient(repository = clientRepository),
            getCategoryTemp = GetCategoryTemp(repository = categoryRepository),
            getArticleComment = GetArticleComment(repository = articleRepository),
            getAllWorkers = GetAllWorkers(repository = workerRepository),
            getPaymentForProviderDetails = GetPaymentForProviderDetails(repository = pointPaymentRepository)

        )
    }

    @Provides
    @Singleton
    fun priverPurchaseDao(appDatabase: AppDatabase): PurchaseOrderDao {
        return appDatabase.purchaseOrderDao()
    }
    @Provides
    @Singleton
    fun priverPurchaseLineDao(appDatabase: AppDatabase): PurchaseOrderLineDao {
        return appDatabase.purchaseOrderLineDao()
    }
    @Provides
    @Singleton
    fun priverCompanyDao(appDatabase: AppDatabase): CompanyDao {
        return appDatabase.companyDao()
    }
    @Provides
    @Singleton
    fun priverUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideAppViewModel(repository: GlobalRepository,
                            dataStore: DataStore<AuthenticationResponse>,
                            companyDataStore : DataStore<Company>,
                            userDataStore: DataStore<User>,
                            sharedViewModel: SharedViewModel,
                            context: Context,
                            accountTypeDataStore: DataStore<AccountType>,
                            viewModelRunTracker: ViewModelRunTracker
                            )
    : AppViewModel {
        return AppViewModel(repository,dataStore, companyDataStore, userDataStore, sharedViewModel, context, accountTypeDataStore, viewModelRunTracker)
    }

    @Provides
    @Singleton
    fun providerCategoryViewModel(repository: GlobalRepository,room : AppDatabase,sharedViewModel: SharedViewModel, useCases: MetaUseCases, context: Context,
                                  api : ServiceApi):CategoryViewModel{
        return CategoryViewModel(repository,room, sharedViewModel, useCases, context, api)
    }

    @Provides
    @Singleton
    fun providArticleViewModel(repository: GlobalRepository, sharedViewModel: SharedViewModel, room : AppDatabase, useCases: MetaUseCases,
                               appViewModel: AppViewModel, context: Context): ArticleViewModel {
        return ArticleViewModel(repository,sharedViewModel, room,useCases, appViewModel, context)
    }

    @Provides
    @Singleton
    fun provideShoppingViewModel(repository: GlobalRepository, room: AppDatabase, sharedViewModel: SharedViewModel, appViewModel: AppViewModel,
                                 context : Context, useCases: MetaUseCases):ShoppingViewModel{
        return ShoppingViewModel(repository,room, sharedViewModel, appViewModel, context,useCases)
    }

    @Provides
    @Singleton
    fun provideCompanyViewModel(globalRepository: GlobalRepository, room : AppDatabase,
                                companyDataStore: DataStore<Company>, appViewModel : AppViewModel, sharedViewModel: SharedViewModel, useCases: MetaUseCases, ): CompanyViewModel {
        return CompanyViewModel(globalRepository,room,companyDataStore, appViewModel, sharedViewModel, useCases)
    }

    @Provides
    @Singleton
    fun providerClientViewModel(repository: GlobalRepository, room : AppDatabase, sharedViewModel: SharedViewModel, useCases: MetaUseCases, context: Context): ClientViewModel {
        return ClientViewModel(repository, room, sharedViewModel, useCases, context)
    }
    @Provides
    @Singleton
    fun provideInventoryViewModel( room : AppDatabase, useCases: MetaUseCases, sharedViewModel: SharedViewModel): InventoryViewModel {
        return InventoryViewModel(room, useCases, sharedViewModel)
    }

    @Provides
    @Singleton
    fun providerInvoiceViewModel(repository: GlobalRepository, room : AppDatabase, sharedViewModel: SharedViewModel, useCases: MetaUseCases, barcodeScanner: BarcodeScanner, context: Context): InvoiceViewModel {
        return InvoiceViewModel(repository, room, sharedViewModel, useCases, barcodeScanner, context)
    }
    @Provides
    @Singleton
    fun provideSignInViewModel(repository: GlobalRepository, dataStore: DataStore<AuthenticationResponse>, appViewModel: AppViewModel, companyDataStore: DataStore<Company>, userDataStore: DataStore<User>,
                               sharedViewModel: SharedViewModel, accountTypeDataStore: DataStore<AccountType>,
                               room: AppDatabase, tokenManager: TokenManager
                               ): SignInViewModel {
        return SignInViewModel(repository, dataStore, appViewModel, companyDataStore, userDataStore, sharedViewModel, accountTypeDataStore, room, tokenManager)
    }

    @Provides
    @Singleton
    fun provideSharedViewModel(
        authDataStore : DataStore<AuthenticationResponse>,
        companyDtoDataStore: DataStore<Company>,
        userDtoDataStore: DataStore<User>,
        room: AppDatabase,
        context: Context,
        accountTypeDataStore: DataStore<AccountType>,
        tokenManager: TokenManager
                               ):SharedViewModel{
        return SharedViewModel(authDataStore, companyDtoDataStore, userDtoDataStore, room, context, accountTypeDataStore,tokenManager)
    }

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        networkInterceptor: NetworkInterceptor,
        accountTypeInterceptor: AccountTypeInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(networkInterceptor)
            .addInterceptor(accountTypeInterceptor)
            .build()
    }
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideServiceApi(retrofit: Retrofit): ServiceApi {
        return retrofit.create(ServiceApi::class.java)
    }


    @Provides
    @Singleton
    fun provideStompClient(): HttpClient {
        return HttpClient(OkHttp) {

            install(ContentNegotiation) {
                json()
            }
            install(WebSockets)
        }
    }

    @Provides
    @Singleton
    fun provideInventoryRepository(api : ServiceApi, room: AppDatabase): InventoryRepository{
        return InventoryRepositoryImpl(api, room)
    }

    @Provides
    @Singleton
    fun provideClientRepository(api: ServiceApi, room : AppDatabase):ClientRepository{
        return ClientRepositoryImpl(api, room)
    }
    @Provides
    @Singleton
    fun provideRepository(
        signInRepository: SignInRepository,
        articleRepository: ArticleRepository,
        subCategoryRepository: SubCategoryRepository,
        categoryRepository: CategoryRepository,
        companyRepository: CompanyRepository,
        inventoryRepository: InventoryRepository,
        clientRepository: ClientRepository,
        providerRepository: ProviderRepository,
        paymentRepository: PaymentRepository,
        orderRepository: OrderRepository,
        workerRepository: WorkerRepository,
        invoiceRepository: InvoiceRepository,
        messageRepository: MessageRepository,
        shoppingRepository: ShoppingRepository,
        invetationRepository: InvetationRepository,
        pointPaymentRepository: PointPaymentRepository,
        ratingRepository: RatingRepository,
        aymenRepository: AymenRepository,
        commandLineRepository: CommandLineRepository,
    ): GlobalRepository {
        return GlobalRepositoryImpl(
            signInRepository,
            articleRepository,subCategoryRepository,
            categoryRepository, companyRepository,
              inventoryRepository,
            clientRepository, providerRepository,
            paymentRepository, orderRepository,
            workerRepository, invoiceRepository,
            messageRepository, shoppingRepository,
            invetationRepository, pointPaymentRepository,
            ratingRepository, aymenRepository,
            commandLineRepository
        )
    }
    @Provides
    @Singleton
    fun provideArticleRepository(
        serviceApi: ServiceApi,
        room: AppDatabase,
        sharedViewModel: SharedViewModel
    ): ArticleRepository {
        return  ArticleRepositoryImpl(serviceApi,sharedViewModel,room)
    }
    @Provides
    @Singleton
    fun provideCategoryRepository(
        serviceApi: ServiceApi,
        sharedViewModel: SharedViewModel,
        room: AppDatabase
    ): CategoryRepository {
        return  CategoryRepositoryImpl(serviceApi, sharedViewModel = sharedViewModel , room)
    }
    @Provides
    @Singleton
    fun provideCompanyRepository(
        serviceApi: ServiceApi,
        room : AppDatabase
    ): CompanyRepository {
        return CompanyRepositoryImpl(serviceApi, room)
    }
    @Provides
    @Singleton
    fun provideSignInRepository(
        serviceApi: ServiceApi
    ): SignInRepository {
        return  SignInRepositoryImpl(serviceApi)
    }
    @Provides
    @Singleton
    fun provideSubCategoryRepository(
        serviceApi: ServiceApi,
        sharedViewModel: SharedViewModel,
        room: AppDatabase
    ): SubCategoryRepository {
        return  SubCategoryRepositoryImpl(serviceApi, sharedViewModel, room)
    }

    @Provides
    @Singleton
    fun provideProviderRepository(
        serviceApi: ServiceApi
    ): ProviderRepository{
        return ProviderRepositoryImpl(serviceApi)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(
        serviceApi: ServiceApi,
        room : AppDatabase
    ): PaymentRepository{
        return PaymentRepositoryImpl(serviceApi, room)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(
        serviceApi: ServiceApi,
        room : AppDatabase
    ): OrderRepository {
        return OrderRepositoryImpl(serviceApi, room)
    }

    @Provides
    @Singleton
    fun provideWorkerRepository(
        serviceApi: ServiceApi,
        room: AppDatabase
    ): WorkerRepository{
        return WorkerRepositoryImpl(serviceApi, room)
    }

    @Provides
    @Singleton
    fun provideInvoiceRepository(
        serviceApi: ServiceApi,
        room: AppDatabase
    ): InvoiceRepository {
        return InvoiceRepositoryImpl(serviceApi, room)
    }

    @Provides
    @Singleton
    fun provideMessageRepository(
        serviceApi: ServiceApi,
        room: AppDatabase
    ): MessageRepository {
        return MessageRepositoryImpl(serviceApi, room)
    }

    @Provides
    @Singleton
    fun provideShoppingRepository(
        serviceApi: ServiceApi
    ): ShoppingRepository {
        return ShoppingRepositoryImpl(serviceApi)
    }

    @Provides
    @Singleton
    fun provideInvetationRepository(
        serviceApi: ServiceApi,
        room: AppDatabase
    ): InvetationRepository {
        return InvetationRepositoryImpl(serviceApi, room)
    }

    @Provides
    @Singleton
    fun providePointsPaymentRepository(
        serviceApi: ServiceApi,
        room: AppDatabase
    ): PointPaymentRepository{
        return PointPaymentRepositoryImpl(serviceApi, room)
    }

    @Provides
    @Singleton
    fun provideRatingRepository(
        serviceApi: ServiceApi
    ): RatingRepository{
        return RatingRepositoryImpl(serviceApi)
    }

    @Provides
    @Singleton
    fun provideAymenRepository(
        serviceApi: ServiceApi
    ): AymenRepository{
        return AymenRepositoryImpl(serviceApi)
    }

    @Provides
    @Singleton
    fun provideCommandLineRepository(
        serviceApi: ServiceApi
    ): CommandLineRepository{
        return CommandLineRepositoryImpl(serviceApi)
    }

    @Provides
    @Singleton
    fun provideTokenSerializer(): TokenSerializer {
        return TokenSerializer
    }
//    @Provides
//    @Singleton
//    fun provideCompanySerializer(): CompanySerializer {
//        return CompanySerializer
//    }
    @Provides
    @Singleton
    fun providerCompanyDtoSerializer(): CompanyDtoSerializer{
        return CompanyDtoSerializer
    }
    @Provides
    @Singleton
    fun provideUserDtoSerializer(): UserDtoSerializer{
        return UserDtoSerializer
    }
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<AuthenticationResponse> {
        return context.datastore
    }


    @Provides
    @Singleton
    fun provideCompanyDtoDataStore(@ApplicationContext context: Context): DataStore<Company>{
        return context.companydtodatastore
    }

    @Provides
    @Singleton
    fun provideUserDtoDataStore(@ApplicationContext context: Context): DataStore<User>{
        return context.userdtodatastore
    }

    @Provides
    @Singleton
    fun provideAccountTypeDataStore(@ApplicationContext context: Context): DataStore<AccountType>{
        return context.accounttypedtodatastore
    }

    @Provides
    @Singleton
    fun provideLocationClient(
        @ApplicationContext context: Context
    ): LocationClient {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        return DefaultLocationClient(context, fusedLocationProviderClient)
    }

    @Provides
    @Singleton
    fun provideBarcodeScanner(@ApplicationContext context: Context): BarcodeScanner{
        return BarcodeScanner(context)
    }

}