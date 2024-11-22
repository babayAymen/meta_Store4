package com.aymen.store.model.repository.globalRepository

import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.dto.ArticleDto
import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.dto.CommentDto
import com.aymen.metastore.model.entity.dto.PaymentForProviderPerDayDto
import com.aymen.metastore.model.entity.dto.PaymentForProvidersDto
import com.aymen.metastore.model.entity.dto.PointsPaymentDto
import com.aymen.metastore.model.entity.dto.RatingDto
import com.aymen.metastore.model.entity.dto.SearchHistoryDto
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type
import com.aymen.metastore.model.entity.dto.AuthenticationRequest
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.entity.dto.RegisterRequest
import com.aymen.metastore.model.entity.dto.CommandLineDto
import com.aymen.metastore.model.entity.dto.ConversationDto
import com.aymen.metastore.model.entity.dto.PurchaseOrderLineDto
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.metastore.model.entity.dto.CategoryDto
import com.aymen.metastore.model.entity.dto.CompanyDto
import com.aymen.metastore.model.entity.dto.InventoryDto
import com.aymen.metastore.model.entity.dto.InvitationDto
import com.aymen.metastore.model.entity.dto.InvoiceDto
import com.aymen.metastore.model.entity.dto.MessageDto
import com.aymen.metastore.model.entity.dto.PurchaseOrderDto
import com.aymen.metastore.model.entity.dto.SubCategoryDto
import com.aymen.metastore.model.entity.dto.UserDto
import com.aymen.metastore.model.entity.dto.WorkerDto
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
    @GET("werehouse/article/getrandom/{categname}")
    suspend fun getRandomArticlesByCompanyCategory(@Path("categname") categName : String): Response<List<ArticleCompanyDto>>
    @GET("werehouse/article/category/{categId}/{companyId}")
    suspend fun getRandomArticlesByCategory(@Path("categId") categoryId : Long, @Path("companyId") companyId : Long): Response<List<ArticleCompanyDto>>
    @GET("werehouse/article/subcategory/{subcategId}/{companyId}")
    suspend fun getRandomArticlesBySubCategory(@Path("subcategId") subcategoryId : Long , @Path("companyId") companyId : Long) : Response<List<ArticleCompanyDto>>
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

    @GET("werehouse/subcategory/{categoryId}/{companyId}")
    suspend fun getAllSubCategoryByCategory(@Path("categoryId") categoryId : Long, @Path("companyId") companyId : Long) : Response<List<SubCategoryDto>>

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
    @GET("werehouse/point/get_my_profit_by_date/{beginDate}/{finalDate}")
    suspend fun getMyProfitByDate(@Path("beginDate") beginDate : String ,@Path("finalDate") finalDate : String):Response<String>
    @GET("werehouse/point/get_all_my_profits")
    suspend fun getAllMyProfits(): Response<List<PaymentForProviderPerDayDto>>
    @GET("werehouse/point/get_all_my_profits_per_day/{beginday}/{finalday}")
    suspend fun getMyHistoryProfitByDate(@Path("beginday") beginDay : String, @Path("finalday") finalDay : String):Response<List<PaymentForProviderPerDayDto>>
    @GET("werehouse/worker/getbycompany/{companyId}")
    suspend fun getAllMyWorker(@Path("companyId") companyId: Long): Response<List<WorkerDto>>
    @GET("werehouse/invoice/getlastinvoice")
    suspend fun getLastInvoiceCode():Response<Long>
    @POST("werehouse/commandline/save/{invoiceCode}/{clientId}/{discount}/{clientType}/{invoiceMode}")
    suspend fun addInvoice(@Body commandLineDtos : List<CommandLine>,
                           @Path("clientId") clientId : Long,
                           @Path("invoiceCode") invoiceCode : Long,
                           @Path("discount") discount : Double,
                           @Path("clientType") clientType : AccountType,
                           @Path("invoiceMode") invoiceMode: InvoiceMode
                           ):Response<Void>
    @GET("werehouse/invoice/get_all_my_invoices_not_accepted/{id}/{status}")
    suspend fun getAllMyInvoicesNotAccepted(@Path("id") id : Long ,@Path("status") status: Status):Response<List<InvoiceDto>>
    @GET("werehouse/invoice/response/{invoiceId}/{status}")
    suspend fun acceptInvoice(@Path("invoiceId") invoiceId : Long, @Path("status") status : Status) : Response<Void>
    @GET("werehouse/commandline/getcommandline/{invoiceId}")
    suspend fun getAllCommandLinesByInvoiceId(@Path("invoiceId") invoiceId : Long):Response<List<CommandLineDto>>
    @GET("werehouse/client/get_all_my_containing/{clientName}/{companyId}")
    suspend fun getAllMyClientContaining(@Path("clientName") clientName : String, @Path("companyId") companyId: Long):Response<List<ClientProviderRelationDto>>
    @GET("werehouse/message/getconversation/{id}/{type}")
    suspend fun getConversationByCaleeId(@Path("id") id : Long,@Path("type") type : MessageType): Response<ConversationDto>
    @POST("werehouse/message/send")
    suspend fun sendMessage( @Body conversation : ConversationDto):Response<Void>
    @POST("werehouse/order/")
    suspend fun sendOrder(@Body orderList : List<PurchaseOrderLine>):Response<Void>
    @POST("werehouse/order/test")
    suspend fun test(@Body order : PurchaseOrderLineDto): Response<Void>
    @GET("werehouse/order/{id}/{status}/{isall}")
    suspend fun orderLineResponse(@Path("id") id : Long, @Path("status") status : Status, @Path("isall") isAll: Boolean):Response<Double>
    @GET("werehouse/order/get_all_my_lines/{companyId}")
    suspend fun getAllMyOrdersLine(@Path ("companyId") companyId : Long) : Response<List<PurchaseOrderLineDto>>
    @GET("werehouse/order/get_all_by_invoice/{invoiceId}")
    suspend fun getAllOrdersLineByInvoiceId(@Path("invoiceId") invoiceId: Long):Response<List<PurchaseOrderLineDto>>
    @GET("werehouse/order/get_all_my_orders/{companyId}")
    suspend fun getAllMyOrder(@Path ("companyId") companyId : Long) : Response<List<PurchaseOrderDto>>
    @GET("werehouse/order/get_lines/{orderId}")
    suspend fun getAllMyOrdersLineByOrderId(@Path("orderId") orderId : Long) : Response<List<PurchaseOrderLineDto>>
    @GET("werehouse/invetation/response/{status}/{id}")
    suspend fun RequestResponse(@Path("status") status : Status,@Path("id") id : Long) : Response<Void>
    @GET("werehouse/invetation/cancel/{id}")
    suspend fun cancelInvitation(@Path("id") id : Long) : Response<Void>
    @GET("werehouse/search/user/{search}/{searchType}/{searchCategory}")
    suspend fun getAllUsersContaining(@Path ("search") search : String,@Path("searchType")  searchType: SearchType,
                                      @Path("searchCategory")  searchCategory: SearchCategory):Response<List<UserDto>>
    @GET("werehouse/like/{articleId}/{isFav}")
    suspend fun likeAnArticle(@Path("articleId") articleId : Long, @Path("isFav") isFav : Boolean):Response<Void>
    @GET("werehouse/invetation/send/{id}/{type}")
    suspend fun sendClientRequest(@Path("id") id : Long,@Path("type") type : Type):Response<Void>
    @GET("werehouse/search/company/{search}/{searchType}/{searchCategory}")
    suspend fun getAllClientContaining(@Path("search") search : String,@Path("searchType")  searchType: SearchType,
                                       @Path("searchCategory")  searchCategory: SearchCategory):Response<List<CompanyDto>>
    @GET("werehouse/search/save_history/{category}/{id}")
    suspend fun saveHistory(@Path("category") category : SearchCategory, @Path("id") id : Long):Response<Void>
    @GET("werehouse/search/get_search_history")
    suspend fun getAllHistory():Response<List<SearchHistoryDto>>
    @GET("werehouse/company/me")
    suspend fun getMeAsCompany(): Response<CompanyDto>
    @POST("werehouse/article/sendComment/{articleId}")
    suspend fun sendComment(@Body comment : String,@Path("articleId") articleId : Long):Response<Void>
    @GET("werehouse/article/get_comments/{articleId}")
    suspend fun getComments(@Path("articleId") articleId : Long):Response<List<CommentDto>>
    @GET("werehouse/article/{articleId}/{quantity}")
    suspend fun addQuantityArticle(@Path("quantity") quantity : Double, @Path("articleId") articleId : Long ): Response<Void>
    @POST("werehouse/point/")
    suspend fun sendPoints(@Body pointsPayment: PointsPaymentDto):Response<Void>
    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body token: String): Response<AuthenticationResponse>
    @GET("api/auth/myuser")
    suspend fun getMyUserDetails():Response<UserDto>
    @GET("werehouse/company/update_location/{latitude}/{logitude}")
    suspend fun updateLocations(@Path("latitude") latitude : Double ,@Path("logitude") logitude : Double):Response<Void>
    @GET("werehouse/rate/get_rate/{id}/{type}")
    suspend fun getRate(@Path("id") id : Long, @Path("type") type : AccountType):Response<List<RatingDto>>
    @Multipart
    @POST("werehouse/rate/do_rate")
    suspend fun doRating(@Query("ratingDto") rating : String, @Part image: MultipartBody.Part? = null):Response<Void>
    @GET("werehouse/rate/enable_to_comment_company/{companyId}")
    suspend fun enabledToCommentCompany(@Path("companyId") companyId: Long):Response<Boolean>
    @GET("werehouse/rate/enable_to_comment_user/{userId}")
    suspend fun enabledToCommentUser(@Path("userId") userid: Long):Response<Boolean>
    @GET("werehouse/rate/enable_to_comment_article/{companyId}")
    suspend fun enabledToCommentArticle(@Path("companyId") companyId: Long):Response<Boolean>

    @GET("werehouse/article/search/{search}/{searchType}")
    suspend fun getAllArticlesContaining(@Path("search") search : String, @Path("searchType") searchType: SearchType) : Response<List<ArticleCompanyDto>>
//suspend fun doRating(
//    @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>,
//    @Part file: MultipartBody.Part? = null
//): Response<Void>
////////////////////////// pagination ///////////////////////////////////////////////////////
    @GET("werehouse/aymen/make_as_point_seller/{status}/{companyId}")
    suspend fun makeAsPointSeller(@Path("status") status : Boolean,@Path("companyId") id : Long)

    @GET("werehouse/category/get_all/{companyId}")
    suspend fun getPagingCategoryByCompany(@Path("companyId") companyId : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int):List<CategoryDto>

    @GET("werehouse/article/getAllMyArticle/{companyId}/{offset}/{pageSize}")
    suspend fun getAll(@Path("companyId") companyId : Long?, @Path("offset") offset : Int, @Path("pageSize") pageSize : Int): List<ArticleCompanyDto>

    @GET("werehouse/article/getrandom")
    suspend fun getRandomArticles( @Query("offset") offset : Int, @Query("pageSize") pageSize : Int): List<ArticleCompanyDto>

    @GET("werehouse/subcategory/getbycompany/{companyId}")
    suspend fun getAllSubCategories(@Path("companyId") companyId : Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<SubCategoryDto>

    @GET("werehouse/article/my_article/{id}")
    suspend fun getArticleDetails(@Path("id") id : Long) : List<ArticleCompanyDto>

    @GET("werehouse/article/search/{search}/{searchType}")
    suspend fun getAllMyArticleContaining(@Path("search") search : String, @Path("searchType") searchType: SearchType,@Query("page") page : Int, @Query("pageSize")pageSize : Int) : List<ArticleCompanyDto>

    @GET("werehouse/category/get/{companyId}")
    suspend fun getAllCategoryByCompany( @Path("companyId")companyId : Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int ): List<CategoryDto>

    @GET("werehouse/client/get_all_my/{companyId}")
    suspend fun getAllMyClient(@Path("companyId") companyId: Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<ClientProviderRelationDto>

    @GET("werehouse/client/get_all_my_client_containing/{companyId}")
    suspend fun getAllMyClientsContaining(@Path("companyId") companyId: Long,@Query("searchType")searchType : SearchType, @Query("search") libelle : String, @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<ClientProviderRelationDto>

    @GET("werehouse/message/get_conversation")
    suspend fun getAllMyConversations(@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<ConversationDto>

    @GET("werehouse/message/getmessage/{id}/{type}")
    suspend fun getAllMessageByCaleeId(@Path("id") id : Long, @Path("type") type: AccountType, @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<MessageDto>

    @GET("werehouse/message/get_message/{conversationId}")
    suspend fun getAllMyMessageByConversationId(@Path("conversationId") conversationId : Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<MessageDto>

    @GET("werehouse/invoice/getMyInvoiceAsProvider/{companyId}")
    suspend fun getAllMyInvoicesAsProvider(@Path("companyId") companyId: Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<InvoiceDto>

    @GET("werehouse/invoice/getMyInvoiceAsClient/{companyId}")
    suspend fun getAllMyInvoicesAsClient(@Path("companyId") companyId: Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int) : List<InvoiceDto>

    @GET("werehouse/invoice/get_by_status/{companyId}")
    suspend fun getAllMyInvoicesAsProviderAndStatus(@Path("companyId") companyId : Long, @Query("status") status: Status,@Query("page") page : Int, @Query("pageSize") pageSize : Int) : List<InvoiceDto>

    @GET("werehouse/inventory/getbycompany/{companyId}")
    suspend fun getInventory(@Path("companyId") companyId : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<InventoryDto>

    @GET("werehouse/invetation/get_invetation")
    suspend fun getAllMyInvetations(@Query("page") page : Int, @Query("pageSize") pageSize : Int) : List<InvitationDto>

    @GET("werehouse/point/get_all_my_payment/{companyId}")
    suspend fun getAllMyPaymentsEspece(@Path("companyId") companyId: Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<PaymentForProvidersDto>

    @GET("werehouse/point/get_all_my_as_company/{id}")
    suspend fun getAllMyPaymentsEspeceByDate(@Path("id") id : Long,@Query("date") date : String, @Query("findate") findate : String,@Query("page") page : Int, @Query("pageSize") pageSize : Int):List<PaymentForProvidersDto>

    @GET("werehouse/point/get_all_my/{companyId}")
    suspend fun getAllMyPointsPayment(@Path("companyId") companyId : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<PointsPaymentDto>

    @GET("werehouse/invoice/get_all_my_invoices_notaccepted/{companyId}")
    suspend fun getAllMyPaymentNotAccepted(@Path("companyId") companyId : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<InvoiceDto>

    @GET("werehouse/company/get_companies_containing/{id}")
    suspend fun getAllCompaniesContaining(@Path("id") id : Long, @Query("search") search : String, @Query("searchType") searchType : SearchType ,@Query("page") page : Int, @Query("pageSize") pageSize : Int ): List<ClientProviderRelationDto>

    @GET("werehouse/article/get_articles_by_category/{id}")
    suspend fun getAllArticlesByCategor(@Path("id") id: Long ,@Query("page") page : Int, @Query("pageSize") pageSize : Int ): List<ArticleDto>

    @GET("werehouse/provider/get_all_my/{companyId}")
    suspend fun getAllMyProvider(@Path("companyId") companyId : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<ClientProviderRelationDto>






}
