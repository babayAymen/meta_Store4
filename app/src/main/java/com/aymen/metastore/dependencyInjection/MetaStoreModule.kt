package com.aymen.store.dependencyInjection

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aymen.metastore.companydtodatastore
import com.aymen.metastore.datastore
//import com.aymen.metastore.datastore1
import com.aymen.metastore.dependencyInjection.AccountTypeInterceptor
import com.aymen.metastore.dependencyInjection.CompanyDtoSerializer
import com.aymen.metastore.dependencyInjection.UserDtoSerializer
import com.aymen.metastore.model.Location.DefaultLocationClient
import com.aymen.metastore.model.Location.LocationClient
import com.aymen.metastore.model.entity.Dao.CompanyDao
import com.aymen.metastore.model.entity.Dao.PurchaseOrderDao
import com.aymen.metastore.model.entity.Dao.PurchaseOrderLineDao
import com.aymen.metastore.model.entity.Dao.UserDao
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.metastore.model.entity.realm.Rating
import com.aymen.metastore.model.repository.remoteRepository.aymenRepository.AymenRepository
import com.aymen.metastore.model.repository.remoteRepository.aymenRepository.AymenRepositoryImpl
import com.aymen.metastore.model.repository.remoteRepository.ratingRepository.RatingRepository
import com.aymen.metastore.model.repository.remoteRepository.ratingRepository.RatingRepositoryImpl
//import com.aymen.metastore.userdatastore
import com.aymen.store.model.entity.dto.AuthenticationResponse
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Category
import com.aymen.store.model.entity.realm.Client
import com.aymen.store.model.entity.realm.ClientProviderRelation
import com.aymen.store.model.entity.realm.CommandLine
import com.aymen.store.model.entity.realm.Comment
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.Conversation
import com.aymen.store.model.entity.realm.Delivery
import com.aymen.store.model.entity.realm.Inventory
import com.aymen.store.model.entity.realm.Invetation
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.entity.realm.Like
import com.aymen.store.model.entity.realm.Message
import com.aymen.store.model.entity.realm.OrderDelivery
import com.aymen.store.model.entity.realm.Parent
import com.aymen.store.model.entity.realm.Payment
import com.aymen.store.model.entity.realm.PointsPayment
import com.aymen.store.model.entity.realm.Provider
import com.aymen.store.model.entity.realm.PurchaseOrder
import com.aymen.store.model.entity.realm.PurchaseOrderLine
import com.aymen.store.model.entity.realm.RandomArticle
import com.aymen.store.model.entity.realm.SearchHistory
import com.aymen.store.model.entity.realm.SubArticle
import com.aymen.store.model.entity.realm.SubCategory
import com.aymen.metastore.model.entity.realm.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.repository.remoteRepository.CommandLineRepository.CommandLineRepository
import com.aymen.metastore.model.repository.remoteRepository.CommandLineRepository.CommandLineRepositoryImpl
import com.aymen.metastore.model.usecase.GetPagingCategoryByCompany
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.userdtodatastore
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto
import com.aymen.store.model.entity.realm.Vacation
import com.aymen.store.model.entity.realm.Worker
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.store.model.repository.ViewModel.ClientViewModel
import com.aymen.store.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.model.repository.ViewModel.InventoryViewModel
import com.aymen.store.model.repository.ViewModel.InvoiceViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.store.model.repository.ViewModel.SignInViewModel
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import com.aymen.store.model.repository.globalRepository.GlobalRepositoryImpl
import com.aymen.store.model.repository.globalRepository.ServiceApi
//import com.aymen.store.model.repository.localRepository.RoomDataBase
import com.aymen.store.model.repository.realmRepository.RealmRepository
import com.aymen.store.model.repository.realmRepository.RealmRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepository
import com.aymen.store.model.repository.remoteRepository.PointsPaymentRepository.PointPaymentRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.invetationRepository.InvetationRepository
import com.aymen.store.model.repository.remoteRepository.shoppingRepository.ShoppingRepository
import com.aymen.store.model.repository.remoteRepository.shoppingRepository.ShoppingRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.categoryRepository.CategoryRepository
import com.aymen.store.model.repository.remoteRepository.categoryRepository.CategoryRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepository
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepository
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.inventoryRepository.InventoryRepository
import com.aymen.store.model.repository.remoteRepository.inventoryRepository.InventoryRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.invoiceRepository.InvoiceRepository
import com.aymen.store.model.repository.remoteRepository.invoiceRepository.InvoiceRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.messageRepository.MessageRepository
import com.aymen.store.model.repository.remoteRepository.messageRepository.MessageRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.orderRepository.OrderRepository
import com.aymen.store.model.repository.remoteRepository.orderRepository.OrderRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepository
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.providerRepository.ProviderRepository
import com.aymen.store.model.repository.remoteRepository.providerRepository.ProviderRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.signInRepository.SignInRepository
import com.aymen.store.model.repository.remoteRepository.signInRepository.SignInRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.subCategoryRepository.SubCategoryRepository
import com.aymen.store.model.repository.remoteRepository.subCategoryRepository.SubCategoryRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.invetationRepository.InvetationRepositoryImpl
import com.aymen.store.model.repository.remoteRepository.workerRepository.WorkerRepository
import com.aymen.store.model.repository.remoteRepository.workerRepository.WorkerRepositoryImpl
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import kotlin.math.exp

const val BASE_URL = "http://192.168.1.4:8080/"
//const val BASE_URL = "http://192.168.134.119:8080/"
private const val DATABASE_NAME = "meta_stoèère_data_base"

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
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideMetaUseCases(categoryRepository: CategoryRepository): MetaUseCases{
        return MetaUseCases(
            getPagingCategoryByCompany = GetPagingCategoryByCompany(repository = categoryRepository)
        )
    }

    @Provides
    @Singleton
    fun priverPurchaseDao(appDatabase: AppDatabase): PurchaseOrderDao{
        return appDatabase.purchaseOrderDao()
    }
    @Provides
    @Singleton
    fun priverPurchaseLineDao(appDatabase: AppDatabase): PurchaseOrderLineDao{
        return appDatabase.purchaseOrderLineDao()
    }
    @Provides
    @Singleton
    fun priverCompanyDao(appDatabase: AppDatabase): CompanyDao{
        return appDatabase.companyDao()
    }
    @Provides
    @Singleton
    fun priverUserDao(appDatabase: AppDatabase): UserDao{
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideRealmDataBase(): Realm {
        return   Realm.open(
            configuration = RealmConfiguration.Builder(
                schema = setOf(
                    Article::class,
                    Category::class,
                    Client::class,
                    CommandLine::class,
                    Company::class,
                    Conversation::class,
                    Delivery::class,
                    Inventory::class,
                    Invoice::class,
                    Invetation::class,
                    Message::class,
                    OrderDelivery::class,
                    Parent::class,
                    Payment::class,
                    Provider::class,
                    PurchaseOrder::class,
                    PurchaseOrderLine::class,
                    RandomArticle::class,
                    SubArticle::class,
                    SubCategory::class,
                    User::class,
                    Vacation::class,
                    Worker::class,
                    ClientProviderRelation::class,
                    Comment::class,
                    Like::class,
                    PointsPayment::class,
                    SearchHistory::class,
                    Rating::class,
                    PaymentForProviders::class,
                    PaymentForProviderPerDay::class,
                    ArticleCompany::class
                )
            )
                .schemaVersion(2)
                .build()
        )
    }


    @Provides
    @Singleton
    fun provideAppViewModel(repository: GlobalRepository,
                            dataStore: DataStore<AuthenticationResponse>,
                            companyDataStore : DataStore<CompanyDto>,
                            userDataStore: DataStore<UserDto>,
                            room : AppDatabase,
                            sharedViewModel: SharedViewModel,
                            context: Context,
                            )
    :AppViewModel{
        return AppViewModel(repository,dataStore, companyDataStore, userDataStore,room, sharedViewModel, context)
    }

    @Provides
    @Singleton
    fun providerCategoryViewModel(repository: GlobalRepository,room : AppDatabase,sharedViewModel: SharedViewModel, useCases: MetaUseCases):CategoryViewModel{
        return CategoryViewModel(repository,room, sharedViewModel, useCases)
    }

    @Provides
    @Singleton
    fun providArticleViewModel(repository: GlobalRepository, sharedViewModel: SharedViewModel, room : AppDatabase):ArticleViewModel{
        return ArticleViewModel(repository,sharedViewModel, room)
    }

    @Provides
    @Singleton
    fun provideShoppingViewModel(repository: GlobalRepository, room: AppDatabase, sharedViewModel: SharedViewModel, appViewModel: AppViewModel, context : Context):ShoppingViewModel{
        return ShoppingViewModel(repository,room, sharedViewModel, appViewModel, context)
    }

    @Provides
    @Singleton
    fun provideCompanyViewModel(globalRepository: GlobalRepository,room : AppDatabase,
                                companyDataStore: DataStore<CompanyDto>, appViewModel : AppViewModel, sharedViewModel: SharedViewModel):CompanyViewModel{
        return CompanyViewModel(globalRepository,room,companyDataStore, appViewModel, sharedViewModel)
    }

    @Provides
    @Singleton
    fun providerClientViewModel(repository: GlobalRepository, room : AppDatabase, sharedViewModel: SharedViewModel):ClientViewModel{
        return ClientViewModel(repository, room, sharedViewModel)
    }
    @Provides
    @Singleton
    fun provideInventoryViewModel(repository: GlobalRepository, room : AppDatabase):InventoryViewModel{
        return InventoryViewModel(repository, room)
    }

    @Provides
    @Singleton
    fun providerInvoiceViewModel(repository: GlobalRepository, room : AppDatabase, sharedViewModel: SharedViewModel):InvoiceViewModel{
        return InvoiceViewModel(repository, room, sharedViewModel)
    }
    @Provides
    @Singleton
    fun provideSignInViewModel(repository: GlobalRepository, dataStore: DataStore<AuthenticationResponse>, appViewModel: AppViewModel): SignInViewModel {
        return SignInViewModel(repository, dataStore, appViewModel)
    }

    @Provides
    @Singleton
    fun provideSharedViewModel(
        authDataStore : DataStore<AuthenticationResponse>,
        companyDtoDataStore: DataStore<CompanyDto>,
        userDtoDataStore: DataStore<UserDto>,
        room: AppDatabase,
        context: Context
                               ):SharedViewModel{
        return SharedViewModel(authDataStore, companyDtoDataStore, userDtoDataStore, room, context)
    }

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }

        @SuppressLint("SuspiciousIndentation")
    @Provides
    @Singleton
    fun provideServiceApi(
         dataStore: DataStore<AuthenticationResponse>,
         sharedViewModel: SharedViewModel
    ): ServiceApi {
        val scope = CoroutineScope(Dispatchers.IO)
        var token = ""
            scope.launch {
                try {
                    dataStore.data
                        .catch { exception ->
                            Log.e("getTokenError", "Error getting token: ${exception.message}")
                        }
                        .collect { authenticationResponse ->
                            Log.e("accountTypeDataStore","account token is : $authenticationResponse")
                            token = authenticationResponse.token
                        }
                } catch (e: Exception) {
                    Log.e("getTokenError", "Error getting token: ${e.message}")
                }
        }
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        chain.proceed(chain.request().newBuilder().also {
                            it.addHeader(
                                "Authorization", "Bearer $token"
                            )
                        }
                            .build())
                    }
                    .addInterceptor(AccountTypeInterceptor(sharedViewModel)) // Custom interceptor for Account-Type
                    .build()
            ).build()
        return retrofit.create(ServiceApi::class.java)
    }
    @Provides
    @Singleton
    fun provideRealmRepository(realm: Realm):RealmRepository{
        return RealmRepositoryImpl(realm)
    }
    @Provides
    @Singleton
    fun provideInventoryRepository(api : ServiceApi): InventoryRepository{
        return InventoryRepositoryImpl(api)
    }
    @Provides
    @Singleton
    fun provideClientRepository(api: ServiceApi):ClientRepository{
        return ClientRepositoryImpl(api)
    }
    @Provides
    @Singleton
    fun provideRepository(
        signInRepository: SignInRepository,
        articleRepository: ArticleRepository,
        subCategoryRepository: SubCategoryRepository,
        categoryRepository: CategoryRepository,
        companyRepository: CompanyRepository,
        realmRepository: RealmRepository,
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
        serviceApi: ServiceApi
    ): ArticleRepository {
        return  ArticleRepositoryImpl(serviceApi)
    }
    @Provides
    @Singleton
    fun provideCategoryRepository(
        serviceApi: ServiceApi
    ): CategoryRepository {
        return  CategoryRepositoryImpl(serviceApi)
    }
    @Provides
    @Singleton
    fun provideCompanyRepository(
        serviceApi: ServiceApi
    ): CompanyRepository {
        return CompanyRepositoryImpl(serviceApi)
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
        serviceApi: ServiceApi
    ): SubCategoryRepository {
        return  SubCategoryRepositoryImpl(serviceApi)
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
        serviceApi: ServiceApi
    ): PaymentRepository{
        return PaymentRepositoryImpl(serviceApi)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(
        serviceApi: ServiceApi
    ): OrderRepository{
        return OrderRepositoryImpl(serviceApi)
    }

    @Provides
    @Singleton
    fun provideWorkerRepository(
        serviceApi: ServiceApi
    ): WorkerRepository{
        return WorkerRepositoryImpl(serviceApi)
    }

    @Provides
    @Singleton
    fun provideInvoiceRepository(
        serviceApi: ServiceApi
    ):InvoiceRepository{
        return InvoiceRepositoryImpl(serviceApi)
    }

    @Provides
    @Singleton
    fun provideMessageRepository(
        serviceApi: ServiceApi
    ):MessageRepository{
        return MessageRepositoryImpl(serviceApi)
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
        serviceApi: ServiceApi
    ): InvetationRepository {
        return InvetationRepositoryImpl(serviceApi)
    }

    @Provides
    @Singleton
    fun providePointsPaymentRepository(
        serviceApi: ServiceApi
    ): PointPaymentRepository{
        return PointPaymentRepositoryImpl(serviceApi)
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
//    @Provides
//    @Singleton
//    fun provideDatastore1(@ApplicationContext context: Context): DataStore<Company> {
//        return context.datastore1
//    }
//
//    @Provides
//    @Singleton
//    fun provideUserDataStore(@ApplicationContext context: Context): DataStore<User>{
//        return context.userdatastore
//    }

    @Provides
    @Singleton
    fun provideCompanyDtoDataStore(@ApplicationContext context: Context): DataStore<CompanyDto>{
        return context.companydtodatastore
    }

    @Provides
    @Singleton
    fun provideUserDtoDataStore(@ApplicationContext context: Context): DataStore<UserDto>{
        return context.userdtodatastore
    }

    @Provides
    @Singleton
    fun provideLocationClient(
        @ApplicationContext context: Context
    ): LocationClient {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        return DefaultLocationClient(context, fusedLocationProviderClient)
    }

}