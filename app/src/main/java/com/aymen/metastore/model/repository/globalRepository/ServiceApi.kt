package com.aymen.store.model.repository.globalRepository

import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.metastore.model.entity.Dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.Dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.Dto.PaymentForProviderPerDayDto
import com.aymen.metastore.model.entity.Dto.PaymentForProvidersDto
import com.aymen.metastore.model.entity.Dto.PointsPaymentDto
import com.aymen.metastore.model.entity.Dto.RatingDto
import com.aymen.metastore.model.entity.Dto.SearchHistoryDto
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.metastore.model.entity.realm.Rating
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type
import com.aymen.store.model.entity.dto.AuthenticationRequest
import com.aymen.store.model.entity.dto.AuthenticationResponse
import com.aymen.store.model.entity.dto.RegisterRequest
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Category
import com.aymen.store.model.entity.dto.CommandLineDto
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.dto.ConversationDto
import com.aymen.store.model.entity.dto.PurchaseOrderLineDto
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
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.entity.dto.CategoryDto
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.InventoryDto
import com.aymen.store.model.entity.dto.InvitationDto
import com.aymen.store.model.entity.dto.InvoiceDto
import com.aymen.store.model.entity.dto.MessageDto
import com.aymen.store.model.entity.dto.PurchaseOrderDto
import com.aymen.store.model.entity.dto.SubCategoryDto
import com.aymen.store.model.entity.dto.UserDto
import com.aymen.store.model.entity.dto.WorkerDto
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
    @GET("werehouse/article/getAllMyArticle/{companyId}/{offset}/{pageSize}")
    suspend fun getAl(@Path("companyId") companyId : Long, @Path("offset") offset : Int, @Path("pageSize") pageSize : Int): Response<List<ArticleCompanyDto>>
    @GET("werehouse/article/getrandom")
    suspend fun getRandomArticles(): Response<List<ArticleCompanyDto>>
    @GET("werehouse/article/getrandom")
    suspend fun getRandomArticless(): Response<List<ArticleCompany>>
    @GET("werehouse/article/getrandom/{categname}")
    suspend fun getRandomArticlesByCompanyCategory(@Path("categname") categName : String): Response<List<ArticleCompanyDto>>
    @GET("werehouse/article/getrandom/{categname}")
    suspend fun getRandomArticlesByCompanyCategoryy(@Path("categname") categName : String): Response<List<ArticleCompany>>
    @GET("werehouse/article/category/{categId}/{companyId}")
    suspend fun getRandomArticlesByCategory(@Path("categId") categoryId : Long, @Path("companyId") companyId : Long): Response<List<ArticleCompanyDto>>
    @GET("werehouse/article/category/{categId}/{companyId}")
    suspend fun getRandomArticlesByCategoryy(@Path("categId") categoryId : Long, @Path("companyId") companyId : Long): Response<List<ArticleCompany>>
    @GET("werehouse/article/subcategory/{subcategId}/{companyId}")
    suspend fun getRandomArticlesBySubCategory(@Path("subcategId") subcategoryId : Long , @Path("companyId") companyId : Long) : Response<List<ArticleCompanyDto>>
    @GET("werehouse/article/subcategory/{subcategId}/{companyId}")
    suspend fun getRandomArticlesBySubCategoryy(@Path("subcategId") subcategoryId : Long , @Path("companyId") companyId : Long) : Response<List<ArticleCompany>>
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
    suspend fun getMyParent(@Path("companyId") companyId : Long):Response<CompanyDto>
    @GET("werehouse/company/get_my_parent/{companyId}")
    suspend fun getMyParentt(@Path("companyId") companyId : Long):Response<Parent>
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
    suspend fun getAllCategoryByCompany(@Path("myCompanyId") myCompanyId : Long, @Path("companyId")companyId : Long): Response<List<CategoryDto>>
    @GET("werehouse/category/getbycompany/{myCompanyId}/{companyId}")
    suspend fun getAllCategoryByCompanyy(@Path("myCompanyId") myCompanyId : Long, @Path("companyId")companyId : Long): Response<List<Category>>

    @GET("werehouse/subcategory/{categoryId}/{companyId}")
    suspend fun getAllSubCategoryByCategory(@Path("categoryId") categoryId : Long, @Path("companyId") companyId : Long) : Response<List<SubCategoryDto>>
    @GET("werehouse/subcategory/{categoryId}/{companyId}")
    suspend fun getAllSubCategoryByCategoryy(@Path("categoryId") categoryId : Long, @Path("companyId") companyId : Long) : Response<List<SubCategory>>

    @GET("werehouse/subcategory/getbycompany/{companyId}")
    suspend fun getAllSubCategories(@Path("companyId") companyId : Long): Response<List<SubCategoryDto>>
    @GET("werehouse/subcategory/getbycompany/{companyId}")
    suspend fun getAllSubCategoriess(@Path("companyId") companyId : Long): Response<List<SubCategory>>

    @GET("werehouse/provider/get_all_my/{companyId}")
    suspend fun getAllMyProvider(@Path("companyId") companyId : Long): Response<List<ClientProviderRelationDto>>
    @GET("werehouse/provider/get_all_my/{companyId}")
    suspend fun getAllMyProviderr(@Path("companyId") companyId : Long): Response<List<Provider>>

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
    suspend fun getInventory(@Path("companyId") companyId : Long): Response<List<InventoryDto>>
    @GET("werehouse/inventory/getbycompany/{companyId}")
    suspend fun getInventoryy(@Path("companyId") companyId : Long): Response<List<Inventory>>

    @GET("werehouse/client/get_all_my/{companyId}")
    suspend fun getAllMyClient(@Path("companyId") companyId: Long): Response<List<ClientProviderRelationDto>>
    @GET("werehouse/client/get_all_my/{companyId}")
    suspend fun getAllMyClientt(@Path("companyId") companyId: Long): Response<List<ClientProviderRelation>>

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
    suspend fun getAllMyPaymentsEspeceByDate(@Path("date") date : String, @Path("findate") findate : String):Response<List<PaymentForProvidersDto>>
    @GET("werehouse/point/get_all_my_as_company/{date}/{findate}")
    suspend fun getAllMyPaymentsEspeceByDatee(@Path("date") date : String, @Path("findate") findate : String):Response<List<PaymentForProviders>>
    @GET("werehouse/point/get_all_my_payment/{companyId}")
    suspend fun getAllMyPaymentsEspece(@Path("companyId") companyId: Long): Response<List<PaymentForProvidersDto>>
    @GET("werehouse/point/get_all_my_payment/{companyId}")
    suspend fun getAllMyPaymentsEspecee(@Path("companyId") companyId: Long): Response<List<PaymentForProviders>>
    @GET("werehouse/point/get_my_profit_by_date/{beginDate}/{finalDate}")
    suspend fun getMyProfitByDate(@Path("beginDate") beginDate : String ,@Path("finalDate") finalDate : String):Response<String>
    @GET("werehouse/point/get_all_my_profits")
    suspend fun getAllMyProfits(): Response<List<PaymentForProviderPerDayDto>>
    @GET("werehouse/point/get_all_my_profits")
    suspend fun getAllMyProfitss(): Response<List<PaymentForProviderPerDay>>
    @GET("werehouse/point/get_all_my_profits_per_day/{beginday}/{finalday}")
    suspend fun getMyHistoryProfitByDate(@Path("beginday") beginDay : String, @Path("finalday") finalDay : String):Response<List<PaymentForProviderPerDayDto>>
    @GET("werehouse/point/get_all_my_profits_per_day/{beginday}/{finalday}")
    suspend fun getMyHistoryProfitByDatee(@Path("beginday") beginDay : String, @Path("finalday") finalDay : String):Response<List<PaymentForProviderPerDay>>
    @GET("werehouse/worker/getbycompany/{companyId}")
    suspend fun getAllMyWorker(@Path("companyId") companyId: Long): Response<List<WorkerDto>>
    @GET("werehouse/worker/getbycompany/{companyId}")
    suspend fun getAllMyWorkerr(@Path("companyId") companyId: Long): Response<List<Worker>>
    @GET("werehouse/invoice/getMyInvoiceAsProvider/{companyId}")
    suspend fun getAllMyInvoicesAsProvider(@Path("companyId") companyId: Long): Response<List<InvoiceDto>>
    @GET("werehouse/invoice/getMyInvoiceAsProvider/{companyId}")
    suspend fun getAllMyInvoicesAsProviderr(@Path("companyId") companyId: Long): Response<List<Invoice>>
    @GET("werehouse/invoice/getMyInvoiceAsClient/{companyId}")
    suspend fun getAllMyInvoicesAsClient(@Path("companyId") companyId: Long) : Response<List<InvoiceDto>>
    @GET("werehouse/invoice/getMyInvoiceAsClient/{companyId}")
    suspend fun getAllMyInvoicesAsClientt(@Path("companyId") companyId: Long) : Response<List<Invoice>>
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
    @GET("werehouse/invoice/get_all_my_invoices_not_accepted")
    suspend fun getAllMyInvoicesNotAccepted():Response<List<InvoiceDto>>
    @GET("werehouse/invoice/get_all_my_invoices_not_accepted")
    suspend fun getAllMyInvoicesNotAcceptedd():Response<List<Invoice>>
    @GET("werehouse/invoice/response/{invoiceId}/{status}")
    suspend fun acceptInvoice(@Path("invoiceId") invoiceId : Long, @Path("status") status : Status) : Response<Void>
    @GET("werehouse/invoice/get_by_status/{companyId}/{status}")
    suspend fun getAllMyInvoicesAsProviderAndStatus(@Path("companyId") companyId : Long, @Path("status") status: PaymentStatus) : Response<List<InvoiceDto>>
    @GET("werehouse/invoice/get_by_status/{companyId}/{status}")
    suspend fun getAllMyInvoicesAsProviderAndStatuss(@Path("companyId") companyId : Long, @Path("status") status: PaymentStatus) : Response<List<Invoice>>
    @GET("werehouse/invoice/get_all_my_invoices_notaccepted/{companyId}")
    suspend fun getAllMyPaymentNotAccepted(@Path("companyId") companyId : Long): Response<List<InvoiceDto>>
    @GET("werehouse/invoice/get_all_my_invoices_notaccepted/{companyId}")
    suspend fun getAllMyPaymentNotAcceptedd(@Path("companyId") companyId : Long): Response<List<Invoice>>
    @GET("werehouse/commandline/getcommandline/{invoiceId}")
    suspend fun getAllCommandLinesByInvoiceId(@Path("invoiceId") invoiceId : Long):Response<List<CommandLineDto>>
    @GET("werehouse/client/get_all_my_containing/{clientName}/{companyId}")
    suspend fun getAllMyClientContaining(@Path("clientName") clientName : String, @Path("companyId") companyId: Long):Response<List<ClientProviderRelationDto>>
    @GET("werehouse/client/get_all_my_containing/{clientName}/{companyId}")
    suspend fun getAllMyClientContainingg(@Path("clientName") clientName : String, @Path("companyId") companyId: Long):Response<List<ClientProviderRelation>>
//    @GET("werehouse/article/{articleLibel}")
//    suspend fun getAllMyArticleContaining(@Path("articleLibel") articleLibel: String) :Response<List<com.aymen.metastore.model.entity.room.Article>>nn
    @GET("werehouse/message/get_conversation")
    suspend fun getAllMyConversations():Response<List<ConversationDto>>
    @GET("werehouse/message/get_message/{conversationId}")
    suspend fun getAllMyMessageByConversationId(@Path("conversationId") conversationId : Long):Response<List<MessageDto>>
    @GET("werehouse/message/get_message/{conversationId}")
    suspend fun getAllMyMessageByConversationIdd(@Path("conversationId") conversationId : Long):Response<List<Message>>
    @GET("werehouse/message/getconversation/{id}/{type}")
    suspend fun getConversationByCaleeId(@Path("id") id : Long,@Path("type") type : MessageType): Response<com.aymen.store.model.entity.realm.Conversation>
    @GET("werehouse/message/getmessage/{id}/{type}")
    suspend fun getAllMessageByCaleeId(@Path("id") id : Long, @Path("type") type: AccountType): Response<List<MessageDto>>
    @GET("werehouse/message/getmessage/{id}/{type}")
    suspend fun getAllMessageByCaleeIdd(@Path("id") id : Long, @Path("type") type: AccountType): Response<List<Message>>
    @POST("werehouse/message/send")
    suspend fun sendMessage( @Body conversation : ConversationDto):Response<Void>
    @POST("werehouse/order/")
    suspend fun sendOrder(@Body orderList : List<PurchaseOrderLineDto>):Response<Void>
    @POST("werehouse/order/test")
    suspend fun test(@Body order : PurchaseOrderLineDto): Response<Void>
    @GET("werehouse/order/{id}/{status}/{isall}")
    suspend fun orderLineResponse(@Path("id") id : Long, @Path("status") status : String, @Path("isall") isAll: Boolean):Response<Double>
    @GET("werehouse/order/get_all_my_lines/{companyId}")
    suspend fun getAllMyOrdersLine(@Path ("companyId") companyId : Long) : Response<List<PurchaseOrderLineDto>>
    @GET("werehouse/order/get_all_by_invoice/{invoiceId}")
    suspend fun getAllOrdersLineByInvoiceId(@Path("invoiceId") invoiceId: Long):Response<List<PurchaseOrderLineDto>>
    @GET("werehouse/order/get_all_my_orders/{companyId}")
    suspend fun getAllMyOrder(@Path ("companyId") companyId : Long) : Response<List<PurchaseOrderDto>>
    @GET("werehouse/order/get_all_my_orders/{companyId}")
    suspend fun getAllMyOrderr(@Path ("companyId") companyId : Long) : Response<List<PurchaseOrder>>
    @GET("werehouse/order/get_lines/{orderId}")
    suspend fun getAllMyOrdersLineByOrderId(@Path("orderId") orderId : Long) : Response<List<PurchaseOrderLineDto>>
    @GET("werehouse/invetation/get_invetation")
    suspend fun getAllMyInvetations() : Response<List<InvitationDto>>
    @GET("werehouse/invetation/get_invetation")
    suspend fun getAllMyInvetationss() : Response<List<Invetation>>
    @GET("werehouse/invetation/response/{status}/{id}")
    suspend fun RequestResponse(@Path("status") status : Status,@Path("id") id : Long) : Response<Void>
    @GET("werehouse/invetation/cancel/{id}")
    suspend fun cancelInvitation(@Path("id") id : Long) : Response<Void>
    @GET("werehouse/company/get_companies_containing/{search}")
    suspend fun getAllCompaniesContaining(@Path("search") search : String): Response<List<CompanyDto>>
    @GET("werehouse/company/get_companies_containing/{search}")
    suspend fun getAllCompaniesContainingg(@Path("search") search : String): Response<List<Company>>
    @GET("werehouse/search/user/{search}/{searchType}/{searchCategory}")
    suspend fun getAllUsersContaining(@Path ("search") search : String,@Path("searchType")  searchType: SearchType,
                                      @Path("searchCategory")  searchCategory: SearchCategory):Response<List<UserDto>>
    @GET("werehouse/search/user/{search}/{searchType}/{searchCategory}")
    suspend fun getAllUsersContainingg(@Path ("search") search : String,@Path("searchType")  searchType: SearchType,
                                      @Path("searchCategory")  searchCategory: SearchCategory):Response<List<User>>
    @GET("werehouse/article/search/{search}/{searchType}")
    suspend fun getAllArticlesContaining(@Path("search") search : String, @Path("searchType") searchType: SearchType) : Response<List<ArticleCompanyDto>>
    @GET("werehouse/article/search/{search}/{searchType}")
    suspend fun getAllArticlesContainingg(@Path("search") search : String, @Path("searchType") searchType: SearchType) : Response<List<ArticleCompany>>
    @GET("werehouse/like/{articleId}/{isFav}")
    suspend fun likeAnArticle(@Path("articleId") articleId : Long, @Path("isFav") isFav : Boolean):Response<Void>
    @GET("werehouse/invetation/send/{id}/{type}")
    suspend fun sendClientRequest(@Path("id") id : Long,@Path("type") type : Type):Response<Void>
    @GET("werehouse/search/company/{search}/{searchType}/{searchCategory}")
    suspend fun getAllClientContaining(@Path("search") search : String,@Path("searchType")  searchType: SearchType,
                                       @Path("searchCategory")  searchCategory: SearchCategory):Response<List<CompanyDto>>
    @GET("werehouse/search/company/{search}/{searchType}/{searchCategory}")
    suspend fun getAllClientContainingg(@Path("search") search : String,@Path("searchType")  searchType: SearchType,
                                       @Path("searchCategory")  searchCategory: SearchCategory):Response<List<Company>>
    @GET("werehouse/search/save_history/{category}/{id}")
    suspend fun saveHistory(@Path("category") category : SearchCategory, @Path("id") id : Long):Response<Void>
    @GET("werehouse/search/get_search_history")
    suspend fun getAllHistory():Response<List<SearchHistoryDto>>
    @GET("werehouse/search/get_search_history")
    suspend fun getAllHistoryy():Response<List<SearchHistory>>
    @GET("werehouse/company/me")
    suspend fun getMe(): Response<Company>
    @POST("werehouse/article/sendComment/{articleId}")
    suspend fun sendComment(@Body comment : String,@Path("articleId") articleId : Long):Response<Void>
    @GET("werehouse/article/get_comments/{articleId}")
    suspend fun getComments(@Path("articleId") articleId : Long):Response<List<Comment>>
    @GET("werehouse/article/get_articles_by_category")
    suspend fun getAllArticlesByCategory():Response<List<Article>>
    @GET("werehouse/article/get_articles_by_category")
    suspend fun getAllArticlesByCategor():Response<List<com.aymen.metastore.model.entity.room.Article>>
    @GET("werehouse/article/{articleId}/{quantity}")
    suspend fun addQuantityArticle(@Path("quantity") quantity : Double, @Path("articleId") articleId : Long ): Response<Void>
    @POST("werehouse/point/")
    suspend fun sendPoints(@Body pointsPayment: PointsPaymentDto):Response<Void>
    @GET("werehouse/point/get_all_my/{companyId}")
    suspend fun getAllMyPointsPayment(@Path("companyId") companyId : Long):Response<List<PointsPaymentDto>>
    @GET("werehouse/point/get_all_my/{companyId}")
    suspend fun getAllMyPointsPaymentt(@Path("companyId") companyId : Long):Response<List<PointsPayment>>
    @POST("dto/auth/refresh")
    suspend fun refreshToken(@Body token: String): Response<AuthenticationResponse>
    @GET("dto/auth/myuser")
    suspend fun getMyUserDetails():Response<User>
    @GET("werehouse/company/update_location/{latitude}/{logitude}")
    suspend fun updateLocations(@Path("latitude") latitude : Double ,@Path("logitude") logitude : Double):Response<Void>
    @GET("werehouse/rate/get_rate/{id}/{type}")
    suspend fun getRate(@Path("id") id : Long, @Path("type") type : AccountType):Response<List<RatingDto>>
    @GET("werehouse/rate/get_rate/{id}/{type}")
    suspend fun getRatee(@Path("id") id : Long, @Path("type") type : AccountType):Response<List<Rating>>
    @Multipart
    @POST("werehouse/rate/do_rate")
    suspend fun doRating(@Query("ratingDto") rating : String, @Part image: MultipartBody.Part? = null):Response<Void>
    @GET("werehouse/rate/enable_to_comment_company/{companyId}")
    suspend fun enabledToCommentCompany(@Path("companyId") companyId: Long):Response<Boolean>
    @GET("werehouse/rate/enable_to_comment_user/{userId}")
    suspend fun enabledToCommentUser(@Path("userId") userid: Long):Response<Boolean>
    @GET("werehouse/rate/enable_to_comment_article/{companyId}")
    suspend fun enabledToCommentArticle(@Path("companyId") companyId: Long):Response<Boolean>
//suspend fun doRating(
//    @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>,
//    @Part file: MultipartBody.Part? = null
//): Response<Void>

    @GET("werehouse/aymen/make_as_point_seller/{status}/{companyId}")
    suspend fun makeAsPointSeller(@Path("status") status : Boolean,@Path("companyId") id : Long)

}