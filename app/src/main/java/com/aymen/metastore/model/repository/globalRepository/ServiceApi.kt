package com.aymen.store.model.repository.globalRepository

import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.metastore.model.entity.api.PaymentForProviderPerDayDto
import com.aymen.metastore.model.entity.api.PointsPaymentDto
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.metastore.model.entity.realm.Rating
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type
import com.aymen.store.model.entity.api.AuthenticationRequest
import com.aymen.store.model.entity.api.AuthenticationResponse
import com.aymen.store.model.entity.api.RegisterRequest
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Category
import com.aymen.store.model.entity.api.CommandLineDto
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.api.ConversationDto
import com.aymen.store.model.entity.api.PurchaseOrderLineDto
import com.aymen.store.model.entity.realm.ClientProviderRelation
import com.aymen.store.model.entity.realm.Comment
import com.aymen.store.model.entity.realm.Message
import com.aymen.store.model.entity.realm.Inventory
import com.aymen.store.model.entity.realm.Invetation
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.entity.realm.Parent
import com.aymen.store.model.entity.realm.Payment
import com.aymen.store.model.entity.realm.PointsPayment
import com.aymen.store.model.entity.realm.Provider
import com.aymen.store.model.entity.realm.PurchaseOrder
import com.aymen.store.model.entity.realm.SearchHistory
import com.aymen.store.model.entity.realm.SubCategory
import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.entity.api.PurchaseOrderDto
import com.aymen.store.model.entity.realm.CommandLine
import com.aymen.store.model.entity.realm.Worker
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiceApi {
    @GET("werehouse/article/getAllMyArticle/{companyId}/{offset}/{pageSize}")
    suspend fun getAll(@Path("companyId") companyId : Long, @Path("offset") offset : Int, @Path("pageSize") pageSize : Int): Response<List<ArticleCompany>>
    @GET("werehouse/article/getrandom")
    suspend fun getRandomArticles(): Response<List<ArticleCompany>>
    @GET("werehouse/article/getrandom/{categname}")
    suspend fun getRandomArticlesByCompanyCategory(@Path("categname") categName : String): Response<List<ArticleCompany>>
    @DELETE("werehouse/article/delete/{id}")
    suspend fun deleteArticle(@Path("id") id: String): Response<Void>
    @Multipart
    @POST("werehouse/company/add")
    suspend fun addCompany(
        @Query("company") company: String,
        @Part file: MultipartBody.Part? = null): Response<Void>
    @Multipart
    @PUT("werehouse/company/update")
    suspend fun updateCompany(
        @Query("company") company: String,
        @Part file: MultipartBody.Part? = null):Response<Void>
    @Multipart
    @PUT("werehouse/image/update")
    suspend fun updateImage(@Part image: MultipartBody.Part? = null):Response<Void>
    @GET("werehouse/company/get_my_parent/{companyId}")
    suspend fun getMyParent(@Path("companyId") companyId : Long):Response<Parent>
    @GET("werehouse/company/mycompany/{companyId}")
    suspend fun getMyCompany(@Path("companyId") companyId : Long):Response<Company>
    @Multipart
    @POST("werehouse/article/add")
    suspend fun addArticle(
        @Query("article") article : String,
        @Part file : MultipartBody.Part? = null
    ): Response<Void>
    @POST("werehouse/article/add/{id}")
    suspend fun addArticleWithoutImage(@Path("id") articleId : Long, @Query("article") article: String):Response<Void>

    @POST("api/auth/authentication")
    suspend fun SignIn(@Body authenticationRequest: AuthenticationRequest): Response<AuthenticationResponse>

    @POST("api/auth/register")
    suspend fun SignUp(@Body registerRequest: RegisterRequest) : Response<AuthenticationResponse>

    @GET("werehouse/category/getbycompany/{myCompanyId}/{companyId}")
    suspend fun getAllCategoryByCompany(@Path("myCompanyId") myCompanyId : Long, @Path("companyId")companyId : Long): Response<List<Category>>

    @GET("werehouse/subcategory/{categoryId}/{companyId}")
    suspend fun getAllSubCategoryByCategory(@Path("categoryId") categoryId : Long, @Path("companyId") companyId : Long) : Response<List<SubCategory>>

    @GET("werehouse/subcategory/getbycompany/{companyId}")
    suspend fun getAllSubCategories(@Path("companyId") companyId : Long): Response<List<SubCategory>>

    @GET("werehouse/provider/get_all_my/{companyId}")
    suspend fun getAllMyProvider(@Path("companyId") companyId : Long): Response<List<Provider>>

    @Multipart
    @POST("werehouse/category/add")
    suspend fun addCategoryApiWithImage(
        @Query("categoryDto") categoryDto:String,
        @Part file: MultipartBody.Part? = null): Response<Void>

    @POST("werehouse/category/add")
    suspend fun addCategoryApiWithoutImage(@Query("categoryDto") categoryDto:String)

    @Multipart
    @POST("werehouse/subcategory/add")
    suspend fun addSubCategoryWithImage(
        @Query("sousCategory") sousCategory:String,
        @Part file: MultipartBody.Part? = null): Response<Void>

    @POST("werehouse/subcategory/add")
    suspend fun addSubCategoryWithoutImage(@Query("sousCategory") sousCategory:String)

    @GET("werehouse/inventory/getbycompany/{companyId}")
    suspend fun getInventory(@Path("companyId") companyId : Long): Response<List<Inventory>>

    @GET("werehouse/client/get_all_my/{companyId}")
    suspend fun getAllMyClient(@Path("companyId") companyId: Long): Response<List<ClientProviderRelation>>

    @Multipart
    @POST("werehouse/client/add")
    suspend fun addClient(
        @Query("company") companyDto:String,
        @Part file: MultipartBody.Part? = null): Response<Void>

    @POST("werehouse/client/add")
    suspend fun addClientWithoutImage(@Query("company") companyDto:String)
   @Multipart
    @POST("werehouse/provider/add")
    suspend fun addProvider(
        @Query("company") company:String,
        @Part file: MultipartBody.Part? = null): Response<Void>
    @POST("werehouse/provider/add")
    suspend fun addProviderWithoutImage(@Query("company") company:String)
    @GET("werehouse/payment/get_all_my_as_company")
    suspend fun getAllMyPayments() : Response<List<Payment>>
    @GET("werehouse/point/get_all_my_as_company/{date}/{findate}")
    suspend fun getAllMyPaymentsEspeceByDate(@Path("date") date : String, @Path("findate") findate : String):Response<List<PaymentForProviders>>
    @GET("werehouse/point/get_all_my_payment/{companyId}")
    suspend fun getAllMyPaymentsEspece(@Path("companyId") companyId: Long): Response<List<PaymentForProviders>>
    @GET("werehouse/point/get_my_profit_by_date/{beginDate}/{finalDate}")
    suspend fun getMyProfitByDate(@Path("beginDate") beginDate : String ,@Path("finalDate") finalDate : String):Response<String>
    @GET("werehouse/point/get_all_my_profits")
    suspend fun getAllMyProfits(): Response<List<PaymentForProviderPerDay>>
    @GET("werehouse/point/get_all_my_profits_per_day/{beginday}/{finalday}")
    suspend fun getMyHistoryProfitByDate(@Path("beginday") beginDay : String, @Path("finalday") finalDay : String):Response<List<PaymentForProviderPerDay>>
    @GET("werehouse/worker/getbycompany/{companyId}")
    suspend fun getAllMyWorker(@Path("companyId") companyId: Long): Response<List<Worker>>
    @GET("werehouse/invoice/getMyInvoiceAsProvider/{companyId}")
    suspend fun getAllMyInvoicesAsProvider(@Path("companyId") companyId: Long): Response<List<Invoice>>
    @GET("werehouse/invoice/getMyInvoiceAsClient/{companyId}")
    suspend fun getAllMyInvoicesAsClient(@Path("companyId") companyId: Long) : Response<List<Invoice>>
    @GET("werehouse/invoice/getlastinvoice")
    suspend fun getLastInvoiceCode():Response<Long>
    @POST("werehouse/commandline/save/{invoiceCode}/{clientId}/{discount}/{clientType}/{invoiceMode}")
    suspend fun addInvoice(@Body commandLineDtos : List<CommandLineDto>,
                           @Path("clientId") clientId : Long,
                           @Path("invoiceCode") invoiceCode : Long,
                           @Path("discount") discount : Double,
                           @Path("clientType") clientType : AccountType,
                           @Path("invoiceMode") invoiceMode: InvoiceMode
                           ):Response<Void>
    @GET("werehouse/commandline/getcommandline/{invoiceId}")
    suspend fun getAllCommandLinesByInvoiceId(@Path("invoiceId") invoiceId : Long):Response<List<CommandLine>>
    @GET("werehouse/client/get_all_my_containing/{clientName}/{companyId}")
    suspend fun getAllMyClientContaining(@Path("clientName") clientName : String, @Path("companyId") companyId: Long):Response<List<ClientProviderRelation>>
//    @GET("werehouse/article/{articleLibel}")
//    suspend fun getAllMyArticleContaining(@Path("articleLibel") articleLibel: String) :Response<List<Article>>nn
    @GET("werehouse/message/get_conversation")
    suspend fun getAllMyConversations():Response<List<ConversationDto>>
    @GET("werehouse/message/get_message/{conversationId}")
    suspend fun getAllMyMessageByConversationId(@Path("conversationId") conversationId : Long):Response<List<Message>>
    @GET("werehouse/message/getconversation/{id}/{type}")
    suspend fun getConversationByCaleeId(@Path("id") id : Long,@Path("type") type : MessageType): Response<com.aymen.store.model.entity.realm.Conversation>
    @GET("werehouse/message/getmessage/{id}/{type}")
    suspend fun getAllMessageByCaleeId(@Path("id") id : Long, @Path("type") type: AccountType): Response<List<Message>>
    @POST("werehouse/message/send")
    suspend fun sendMessage( @Body conversation : ConversationDto):Response<Void>
    @POST("werehouse/order/")
    suspend fun sendOrder(@Body orderList : List<PurchaseOrderLineDto>):Response<Void>
    @POST("werehouse/order/test")
    suspend fun test(@Body order : PurchaseOrderLineDto): Response<Void>
    @GET("werehouse/order/{id}/{status}/{isall}")
    suspend fun orderLineResponse(@Path("id") id : Long, @Path("status") status : String, @Path("isall") isAll: Boolean):Response<Void>
    @GET("werehouse/order/get_all_my_lines/{companyId}")
    suspend fun getAllMyOrdersLine(@Path ("companyId") companyId : Long) : Response<List<PurchaseOrderLineDto>>
    @GET("werehouse/order/get_all_by_invoice/{invoiceId}")
    suspend fun getAllOrdersLineByInvoiceId(@Path("invoiceId") invoiceId: Long):Response<List<PurchaseOrderLineDto>>
    @GET("werehouse/order/get_all_my_orders/{companyId}")
    suspend fun getAllMyOrder(@Path ("companyId") companyId : Long) : Response<List<PurchaseOrder>>
    @GET("werehouse/order/get_lines/{orderId}")
    suspend fun getAllMyOrdersLineByOrderId(@Path("orderId") orderId : Long) : Response<List<PurchaseOrderLineDto>>
    @GET("werehouse/invetation/get_invetation")
    suspend fun getAllMyInvetations() : Response<List<Invetation>>
    @GET("werehouse/invetation/response/{status}/{id}")
    suspend fun RequestResponse(@Path("status") status : Status,@Path("id") id : Long) : Response<Void>
    @GET("werehouse/invetation/cancel/{id}")
    suspend fun cancelInvitation(@Path("id") id : Long) : Response<Void>
    @GET("werehouse/company/get_companies_containing/{search}")
    suspend fun getAllCompaniesContaining(@Path("search") search : String): Response<List<Company>>
    @GET("werehouse/search/user/{search}/{searchType}/{searchCategory}")
    suspend fun getAllUsersContaining(@Path ("search") search : String,@Path("searchType")  searchType: SearchType,@Path("searchCategory")  searchCategory: SearchCategory):Response<List<User>>
    @GET("werehouse/article/search/{search}/{searchType}")
    suspend fun getAllArticlesContaining(@Path("search") search : String, @Path("searchType") searchType: SearchType) : Response<List<ArticleCompany>>
    @GET("werehouse/like/{articleId}/{isFav}")
    suspend fun likeAnArticle(@Path("articleId") articleId : Long, @Path("isFav") isFav : Boolean):Response<Void>
    @GET("werehouse/invetation/send/{id}/{type}")
    suspend fun sendClientRequest(@Path("id") id : Long,@Path("type") type : Type):Response<Void>
    @GET("werehouse/search/company/{search}/{searchType}/{searchCategory}")
    suspend fun getAllClientContaining(@Path("search") search : String,@Path("searchType")  searchType: SearchType,@Path("searchCategory")  searchCategory: SearchCategory):Response<List<Company>>
    @GET("werehouse/search/save_history/{category}/{id}")
    suspend fun saveHistory(@Path("category") category : SearchCategory, @Path("id") id : Long):Response<Void>
    @GET("werehouse/search/get_search_history")
    suspend fun getAllHistory():Response<List<SearchHistory>>
    @GET("werehouse/company/me")
    suspend fun getMe(): Response<Company>
    @POST("werehouse/article/sendComment/{articleId}")
    suspend fun sendComment(@Body comment : String,@Path("articleId") articleId : Long):Response<Void>
    @GET("werehouse/article/get_comments/{articleId}")
    suspend fun getComments(@Path("articleId") articleId : Long):Response<List<Comment>>
    @GET("werehouse/article/get_articles_by_category")
    suspend fun getAllArticlesByCategory():Response<List<Article>>
    @GET("werehouse/article/{articleId}/{quantity}")
    suspend fun addQuantityArticle(@Path("quantity") quantity : Double, @Path("articleId") articleId : Long ): Response<Void>
    @POST("werehouse/point/")
    suspend fun sendPoints(@Body pointsPayment: PointsPaymentDto):Response<Void>
    @GET("werehouse/point/get_all_my/{companyId}")
    suspend fun getAllMyPointsPayment(@Path("companyId") companyId : Long):Response<List<PointsPayment>>
    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body token: String): Response<AuthenticationResponse>
    @GET("api/auth/myuser")
    suspend fun getMyUserDetails():Response<User>
    @GET("werehouse/company/update_location/{latitude}/{logitude}")
    suspend fun updateLocations(@Path("latitude") latitude : Double ,@Path("logitude") logitude : Double):Response<Void>
    @GET("werehouse/rate/get_rate/{id}/{type}")
    suspend fun getRate(@Path("id") id : Long, @Path("type") type : AccountType):Response<List<Rating>>
    @Multipart
    @POST("werehouse/rate/do_rate")
    suspend fun doRating(@Query("ratingDto") rating : String, @Part image: MultipartBody.Part? = null):Response<Void>
//suspend fun doRating(
//    @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>,
//    @Part file: MultipartBody.Part? = null
//): Response<Void>

    @GET("werehouse/aymen/make_as_point_seller/{status}/{companyId}")
    suspend fun makeAsPointSeller(@Path("status") status : Boolean,@Path("companyId") id : Long)

}