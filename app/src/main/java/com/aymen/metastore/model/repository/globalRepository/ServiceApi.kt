package com.aymen.metastore.model.repository.globalRepository

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
import com.aymen.metastore.model.entity.dto.ReglementFoProviderDto
import com.aymen.metastore.model.entity.dto.SubCategoryDto
import com.aymen.metastore.model.entity.dto.UserDto
import com.aymen.metastore.model.entity.dto.WorkerDto
import com.aymen.metastore.model.entity.model.PaginatedResponse
import com.aymen.metastore.util.ARTICLE_BASE_URL
import com.aymen.metastore.util.AUTH_BASE_URL
import com.aymen.metastore.util.CATEGORY_BASE_URL
import com.aymen.metastore.util.CLIENT_BASE_URL
import com.aymen.metastore.util.COMMANDLINE_BASE_URL
import com.aymen.metastore.util.COMPANY_BASE_URL
import com.aymen.metastore.util.INVENTORY_BASE_URL
import com.aymen.metastore.util.INVITATION_BASE_URL
import com.aymen.metastore.util.INVOICE_BASE_URL
import com.aymen.metastore.util.LIKE_BASE_URL
import com.aymen.metastore.util.MESSAGE_BASE_URL
import com.aymen.metastore.util.META_BASE_URL
import com.aymen.metastore.util.ORDER_BASE_URL
import com.aymen.metastore.util.POINT_BASE_URL
import com.aymen.metastore.util.PROVIDER_BASE_URL
import com.aymen.metastore.util.RATE_BASE_URL
import com.aymen.metastore.util.SEARCH_BASE_URL
import com.aymen.metastore.util.SUBCATEGORY_BASE_URL
import com.aymen.metastore.util.UPDATE_IMAGE_URL
import com.aymen.metastore.util.WORKER_BASE_URL
import com.aymen.store.model.Enum.CompanyCategory
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


    @GET("$META_BASE_URL/make_as_point_seller/{companyId}")
    suspend fun makeAsPointSeller(@Path("companyId") id : Long,@Query("status") status : Boolean)
    @GET("$META_BASE_URL/make_as_meta_seller/{companyId}")
    suspend fun makeAsMetaSeller(@Path("companyId") id : Long, @Query("status") status : Boolean)
    @Multipart
    @PUT(UPDATE_IMAGE_URL)
    suspend fun updateImage(@Part image: MultipartBody.Part? = null):Response<Void>
    @GET("$WORKER_BASE_URL/getbycompany/{companyId}")
    suspend fun getAllMyWorker(@Path("companyId") companyId: Long, @Query("page") page : Int , @Query("pageSize") pageSize : Int): PaginatedResponse<WorkerDto>
     @GET("$LIKE_BASE_URL/{articleId}/{isFav}")
    suspend fun likeAnArticle(@Path("articleId") articleId : Long, @Path("isFav") isFav : Boolean):Response<Void>
    @GET("$RATE_BASE_URL/get_rate/{id}")
    suspend fun getRate(@Path("id") id : Long, @Query("type") type : AccountType, @Query("page") page : Int , @Query("pageSize") pageSize : Int): PaginatedResponse<RatingDto>
    @Multipart
    @POST("$RATE_BASE_URL/do_rate")
    suspend fun doRating(@Query("ratingDto") rating : String, @Part image: MultipartBody.Part? = null):Response<RatingDto>
    @GET("$RATE_BASE_URL/enable_to_comment_company/{companyId}")
    suspend fun enabledToCommentCompany(@Path("companyId") companyId: Long):Response<Boolean>
    @GET("$RATE_BASE_URL/enable_to_comment_user/{userId}")
    suspend fun enabledToCommentUser(@Path("userId") userid: Long):Response<Boolean>
    @GET("$RATE_BASE_URL/enable_to_comment_article/{companyId}")
    suspend fun enabledToCommentArticle(@Path("companyId") companyId: Long):Response<Boolean>
    @GET("$INVENTORY_BASE_URL/getbycompany/{companyId}")
    suspend fun getInventory(@Path("companyId") companyId : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<InventoryDto>
    @GET("$INVITATION_BASE_URL/get_invetation/{companyId}")
    suspend fun getAllMyInvetations(@Path("companyId") companyId : Long ,@Query("page") page : Int, @Query("pageSize") pageSize : Int) : PaginatedResponse<InvitationDto>
    @GET("$INVITATION_BASE_URL/send/{id}/{type}")
    suspend fun sendClientRequest(@Path("id") id : Long,@Path("type") type : Type):Response<Void>
    @GET("$INVITATION_BASE_URL/response/{status}/{id}")
    suspend fun requestResponse(@Path("status") status : Status, @Path("id") id : Long) : Response<Void>
    @GET("$INVITATION_BASE_URL/cancel/{id}")
    suspend fun cancelInvitation(@Path("id") id : Long) : Response<Void>
    @GET("$SEARCH_BASE_URL/get_search_history/{id}")
    suspend fun getAllHistory(@Path("id") id : Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int): PaginatedResponse<SearchHistoryDto>
    @GET("$SEARCH_BASE_URL/company/{search}/{searchType}/{searchCategory}")
    suspend fun getAllClientContaining(@Path("search") search : String,@Path("searchType")  searchType: SearchType,
                                       @Path("searchCategory")  searchCategory: SearchCategory):Response<List<CompanyDto>>
    @GET("$SEARCH_BASE_URL/save_history/{category}/{id}")
    suspend fun saveHistory(@Path("category") category : SearchCategory, @Path("id") id : Long):Response<SearchHistoryDto>
    @DELETE("$SEARCH_BASE_URL/delete_history/{id}")
    suspend fun deleteSearch( @Path("id") id : Long):Response<Void>
    ///////////////////////////////////////////////////////////////:command line ////////////////////////////////////////////////////////////////////::
    @POST("$COMMANDLINE_BASE_URL/save/{clientId}")
    suspend fun addInvoice(@Body commandLineDtos : List<CommandLine>,
                           @Path("clientId") clientId : Long,
                           @Query("invoiceCode") invoiceCode : Long,
                           @Query("discount") discount : Double,
                           @Query("clientType") clientType : AccountType,
                           @Query("invoiceMode") invoiceMode: InvoiceMode,
                           @Query("type") type: String
    ):Response<List<CommandLineDto>>
    @GET("$COMMANDLINE_BASE_URL/get_command_line/{companyId}")
    suspend fun getAllCommandLinesByInvoiceId(@Path("companyId") companyId : Long, @Query("invoiceId") invoiceId : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<CommandLineDto>
    //////////////////////////////////////////////////////////////:order/////////////////////////////////////////////////////////////////
    @GET("$ORDER_BASE_URL/get_purchaseorder_line_by_invoice_id/{companyId}")
    suspend fun getAllMyOrdersLinesByInvoiceId(@Path("companyId") companyId : Long , @Query("invoiceId") invoiceId : Long ,@Query("page") page : Int, @Query("pageSize") pageSize : Int ): List<PurchaseOrderLineDto>
    @GET("$ORDER_BASE_URL/get_all_my_orders_not_accepted/{id}")
    suspend fun getAllMyOrdersNotAccepted(@Path("id") id : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int ) : List<PurchaseOrderLineDto>
    @POST("$ORDER_BASE_URL/")
    suspend fun sendOrder(@Body orderList : List<PurchaseOrderLine>):Response<List<PurchaseOrderLineDto>>
    @POST("$ORDER_BASE_URL/test")
    suspend fun test(@Body order : PurchaseOrderLineDto): Response<Void>
    @GET("$ORDER_BASE_URL/{id}/{status}/{isall}")
    suspend fun orderLineResponse(@Path("id") id : Long, @Path("status") status : Status, @Path("isall") isAll: Boolean):Response<Double>
    @GET("$ORDER_BASE_URL/get_all_my_lines/{companyId}")
    suspend fun getAllMyOrdersLine(@Path ("companyId") companyId : Long) : Response<List<PurchaseOrderLineDto>>
    @GET("$ORDER_BASE_URL/get_all_my_orders/{companyId}")
    suspend fun getAllMyOrder(@Path ("companyId") companyId : Long) : Response<List<PurchaseOrderDto>>
    @GET("$ORDER_BASE_URL/get_lines/{orderId}")
    suspend fun getAllMyOrdersLineByOrderId(@Path("orderId") orderId : Long) : Response<List<PurchaseOrderLineDto>>
    @GET("$ORDER_BASE_URL/get_by_order_id/{orderId}")
    suspend fun getOrdersLineDetails(@Path("orderId") orderId : Long ,@Query("page") page : Int, @Query("pageSize") pageSize : Int ): List<PurchaseOrderLineDto>
    /////////////////////////////////////////////////////////////invoice////////////////////////////////////////////////////////////////
    @GET("$INVOICE_BASE_URL/getMyInvoiceAsProvider/{id}")
    suspend fun getAllBuyHistory(@Path("id")id : Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int ) : List<InvoiceDto>
    @GET("$INVOICE_BASE_URL/get_by_payment_paid_status/{id}")
    suspend fun getAllBuyHistoryByPaidStatusAsProvider(@Path("id") id : Long, @Query("status") status: PaymentStatus, @Query("page") page : Int, @Query("pageSize") pageSize : Int ) : List<InvoiceDto>
    @GET("$INVOICE_BASE_URL/get_by_payment_paid_status_as_client/{id}")
    suspend fun getAllBuyHistoryByPaidStatusAsClient(@Path("id") id : Long, @Query("status") status: PaymentStatus, @Query("page") page : Int, @Query("pageSize") pageSize : Int ) : List<InvoiceDto>
    @GET("$INVOICE_BASE_URL/get_all_my_invoices_not_accepted_as_provider/{id}")
    suspend fun getAllBuyHistoryByStatus(@Path("id") id : Long, @Query("status") status: Status, @Query("page") page : Int, @Query("pageSize") pageSize : Int ) : List<InvoiceDto>
    @GET("$INVOICE_BASE_URL/get_all_my_invoices_not_accepted_as_client/{id}")
    suspend fun getAllMyInvoicesNotAccepted(@Path("id") id : Long , @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<InvoiceDto>
    @GET("$INVOICE_BASE_URL/getMyInvoiceAsProvider/{companyId}")
    suspend fun getAllMyInvoicesAsProvider(@Path("companyId") companyId: Long, @Query("status") status: PaymentStatus,@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<InvoiceDto>
    @GET("$INVOICE_BASE_URL/getMyInvoiceAsClient/{companyId}")
    suspend fun getAllMyInvoicesAsClient(@Path("companyId") companyId: Long, @Query("status") status: PaymentStatus,@Query("page") page : Int, @Query("pageSize") pageSize : Int) : PaginatedResponse<InvoiceDto>
    @GET("$INVOICE_BASE_URL/get_by_status_as_client/{id}")
    suspend fun getAllMyInvoicesAsClientAndStatus(@Path("id") id : Long, @Query("status") status: Status,@Query("page") page : Int, @Query("pageSize") pageSize : Int) : List<InvoiceDto>
    @GET("$INVOICE_BASE_URL/getlastinvoice")
    suspend fun getLastInvoiceCode():Response<Long>
    @GET("$INVOICE_BASE_URL/response/{invoiceId}/{status}")
    suspend fun acceptInvoice(@Path("invoiceId") invoiceId : Long, @Path("status") status : Status) : Response<Void>
    ////////////////////////////////////////////////////////////:point//////////////////////////////////////////////////////////:::
    @GET("$POINT_BASE_URL/get_all_my_as_company/{id}")
    suspend fun getAllMyPaymentsEspeceByDate(@Path("id") id : Long,@Query("date") date : String, @Query("findate") findate : String,@Query("page") page : Int, @Query("pageSize") pageSize : Int):List<PaymentForProvidersDto>
    @GET("$POINT_BASE_URL/get_all_my/{id}")
    suspend fun getRechargeHistory(@Path("id") id : Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int ): List<PointsPaymentDto>
    @GET("$POINT_BASE_URL/get_all_my/{companyId}")
    suspend fun getAllMyPointsPayment(@Path("companyId") companyId : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<PointsPaymentDto>
    @GET("$POINT_BASE_URL/get_my_profit_by_date/{beginDate}/{finalDate}")
    suspend fun getMyProfitByDate(@Path("beginDate") beginDate : String ,@Path("finalDate") finalDate : String):Response<String>
    @GET("$POINT_BASE_URL/get_all_my_profits")
    suspend fun getAllMyProfits(): Response<List<PaymentForProviderPerDayDto>>
    @GET("$POINT_BASE_URL/get_all_my_payment/{id}")
    suspend fun getAllProvidersProfit(@Path("id") id : Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int ) : PaginatedResponse<PaymentForProvidersDto>
    @GET("$POINT_BASE_URL/get_all_my_profits/{id}")
    suspend fun getAllProfitPerDay(@Path("id") id : Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<PaymentForProviderPerDayDto>
    @GET("$POINT_BASE_URL/get_all_my_profits_per_day/{id}")
    suspend fun getMyHistoryProfitByDate(@Path("id") id : Long, @Query("beginday") beginDay : String, @Query("finalday") finalDay : String, @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<PaymentForProviderPerDayDto>
    @POST("$POINT_BASE_URL/")
    suspend fun sendPoints(@Body pointsPayment: PointsPaymentDto):Response<Void>
    @POST("$POINT_BASE_URL/send_reglement")
    suspend fun sendReglement(@Body reglement : ReglementFoProviderDto): Response<Void>
    @GET("$POINT_BASE_URL/get_all_my_reglement_by_payment_id/{paymentId}")
    suspend fun getAllReglementHistoryByPaymentId(@Path("paymentId") paymentId : Long , @Query("page") page : Int , @Query("pageSize") pageSize : Int) : PaginatedResponse<ReglementFoProviderDto>
///////////////////////////////////////////////:article //////////////////////////////////////////////////////////////////////:
    @GET("$ARTICLE_BASE_URL/getrandom/{categname}")
    suspend fun getRandomArticlesByCompanyCategory(@Path("categname") categName : String): Response<List<ArticleCompanyDto>>
    @GET("$ARTICLE_BASE_URL/category/{categId}/{companyId}")
    suspend fun getRandomArticlesByCategory(@Path("categId") categoryId : Long, @Path("companyId") companyId : Long): Response<List<ArticleCompanyDto>>
    @GET("$ARTICLE_BASE_URL/subcategory/{subcategId}/{companyId}")
    suspend fun getRandomArticlesBySubCategory(@Path("subcategId") subcategoryId : Long , @Path("companyId") companyId : Long) : Response<List<ArticleCompanyDto>>
    @DELETE("$ARTICLE_BASE_URL/delete/{id}")
    suspend fun deleteArticle(@Path("id") id: Long): Response<Void>
    @Multipart
    @POST("$ARTICLE_BASE_URL/add")
    suspend fun addArticle(
        @Query("article") article : String,
        @Part file : MultipartBody.Part? = null
    ): Response<Void>
    @POST("$ARTICLE_BASE_URL/add/{id}")
    suspend fun addArticleWithoutImage(@Path("id") articleId : Long, @Body article: ArticleCompanyDto):Response<ArticleCompanyDto>
    @GET("$ARTICLE_BASE_URL/get_company_article_by_company_id/{companyId}")
    suspend fun getAllCompanyArticles(@Path("companyId") companyId : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int ) : List<ArticleCompanyDto>
    @GET("$ARTICLE_BASE_URL/get_company_article_by_category_or_subcategory/{companyId}")
    suspend fun companyArticlesByCategoryOrSubCategory(@Path("companyId") companyId : Long, @Query("categoryId") categoryId : Long, @Query("subCategoryId") subcategoryId: Long , @Query("page") page : Int, @Query("pageSize") pageSize : Int ): List<ArticleCompanyDto>
    @GET("$ARTICLE_BASE_URL/get_all_my_article/{companyId}")
    suspend fun getAll(@Path("companyId") companyId : Long?, @Query("offset") offset : Int, @Query("pageSize") pageSize : Int): PaginatedResponse<ArticleCompanyDto>
    @GET("$ARTICLE_BASE_URL/getrandom")
    suspend fun getRandomArticles(@Query("category") category: CompanyCategory, @Query("offset") offset : Int, @Query("pageSize") pageSize : Int): PaginatedResponse<ArticleCompanyDto>
    @GET("$ARTICLE_BASE_URL/my_article/{id}")
    suspend fun getArticleDetails(@Path("id") id : Long) : List<ArticleCompanyDto>
    @GET("$ARTICLE_BASE_URL/get_articles_by_category/{id}")
    suspend fun getAllArticlesByCategor(@Path("id") id: Long ,@Query("page") page : Int, @Query("pageSize") pageSize : Int ): List<ArticleDto>
    @GET("$ARTICLE_BASE_URL/search/{id}")
    suspend fun getAllMyArticleContaining(@Path("id") companyId : Long ,@Query("search") search : String, @Query("searchType") searchType: SearchType,@Query("page") page : Int, @Query("pageSize")pageSize : Int) : List<ArticleCompanyDto>
    @POST("$ARTICLE_BASE_URL/sendComment")
    suspend fun sendComment(@Body comment : CommentDto):Response<Void>
    @GET("$ARTICLE_BASE_URL/{articleId}/{quantity}")
    suspend fun addQuantityArticle(@Path("quantity") quantity : Double, @Path("articleId") articleId : Long ): Response<ArticleCompanyDto>
    @PUT("$ARTICLE_BASE_URL/update")
    suspend fun updateArticle(@Body article : ArticleCompanyDto) : Response<ArticleCompanyDto>
    @GET("$ARTICLE_BASE_URL/search/{search}/{searchType}")
    suspend fun getAllArticlesContaining(@Path("search") search : String, @Path("searchType") searchType: SearchType) : Response<List<ArticleCompanyDto>>
    @GET("$ARTICLE_BASE_URL/get_by_barcode")
    suspend fun getArticleByBarcode(@Query("barcode") barCode : String) : Response<ArticleCompanyDto>
/////////////////////////////////////////////////////////////////::client/////////////////////////////////////////////////////////////:
    @Multipart
    @POST("$CLIENT_BASE_URL/add")
    suspend fun addClient(@Query("company") companyDto:String, @Part file: MultipartBody.Part? = null): Response<ClientProviderRelationDto>
    @POST("$CLIENT_BASE_URL/add_without_image")
    suspend fun addClientWithoutImage(@Query("company") companyDto:String): Response<ClientProviderRelationDto>
    @Multipart
    @PUT("$CLIENT_BASE_URL/update")
    suspend fun updateClient(@Query("company") companyDto:String, @Part file: MultipartBody.Part?): Response<CompanyDto>
    @PUT("$CLIENT_BASE_URL/update_without_image")
    suspend fun updateClientWithoutImage(@Query("company") companyDto:String): Response<CompanyDto>
    @DELETE("$CLIENT_BASE_URL/{relationId}")
    suspend fun deleteClient(@Path("relationId") relationId : Long) : Response<Void>
    @GET("$CLIENT_BASE_URL/get_all_my_client_containing/{companyId}")
    suspend fun getAllMyClientContaining( @Path("companyId") companyId: Long,@Query("searchType") searchType : SearchType,@Query("search") clientName : String,  @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<ClientProviderRelationDto>
    @GET("$CLIENT_BASE_URL/get_all_my/{companyId}")
    suspend fun getAllMyClient(@Path("companyId") companyId: Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<ClientProviderRelationDto>
    @GET("$CLIENT_BASE_URL/get_all_client_person_containing/{companyId}")
    suspend fun getAllClientsPersonContaining(@Path("companyId") companyId: Long,@Query("searchType")searchType : SearchType, @Query("search") libelle : String, @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<UserDto>
////////////////////////////////////////////////////////////////////////:provider///////////////////////////////////////////////////////////////////////////////////////:
    @Multipart
    @POST("$PROVIDER_BASE_URL/add")
    suspend fun addProvider(
        @Query("company") company:String,
        @Part file: MultipartBody.Part? = null): Response<ClientProviderRelationDto>

    @Multipart
    @PUT("$PROVIDER_BASE_URL/update")
    suspend fun updateProvider(
        @Query("company") company:String,
        @Part file: MultipartBody.Part? = null): Response<CompanyDto>

    @PUT("$PROVIDER_BASE_URL/update_without_image")
    suspend fun updateProviderWithoutImage(@Query("company") companyDto:String): Response<CompanyDto>
    @POST("$PROVIDER_BASE_URL/add")
    suspend fun addProviderWithoutImage(@Query("company") company:String) : Response<ClientProviderRelationDto>
    @DELETE("$PROVIDER_BASE_URL/delete/{relationId}")
    suspend fun deleteProvider(@Path("relationId")relationId : Long) : Response<Void>
    @GET("$PROVIDER_BASE_URL/get_all_my/{companyId}")
    suspend fun getAllMyProvider(@Path("companyId") companyId : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int): List<ClientProviderRelationDto>
/////////////////////////////////////////////////////////////////////////////company///////////////////////////////////////////////////////////////////////////////::
@GET("$COMPANY_BASE_URL/me")
suspend fun getMeAsCompany(): Response<CompanyDto>
    @Multipart
    @POST("$COMPANY_BASE_URL/add")
    suspend fun addCompany(
        @Query("company") company: String,
        @Part file: MultipartBody.Part? = null): Response<Void>
    @Multipart
    @PUT("$COMPANY_BASE_URL/update")
    suspend fun updateCompany(
        @Query("company") company: String,
        @Part file: MultipartBody.Part? = null):Response<Void>
    @GET("$COMPANY_BASE_URL/get_my_parent/{companyId}")
    suspend fun getMyParent(@Path("companyId") companyId : Long):Response<CompanyDto>
    @GET("$COMPANY_BASE_URL/update_location/{latitude}/{logitude}")
    suspend fun updateLocations(@Path("latitude") latitude : Double ,@Path("logitude") logitude : Double):Response<Void>
    @GET("$COMPANY_BASE_URL/get_companies_containing/{id}")
    suspend fun getAllCompaniesContaining(@Path("id") id : Long, @Query("search") search : String, @Query("searchType") searchType : SearchType ,@Query("page") page : Int, @Query("pageSize") pageSize : Int ): List<CompanyDto>
////////////////////////////////////////////////////:auth///////////////////////////////////////////////////////////////////////////
    @POST("$AUTH_BASE_URL/authentication")
    suspend fun signIn(@Body authenticationRequest: AuthenticationRequest): Response<AuthenticationResponse>
    @POST("$AUTH_BASE_URL/refresh")
    suspend fun refreshToken(@Body token: String): Response<AuthenticationResponse>
    @GET("$AUTH_BASE_URL/myuser")
    suspend fun getMyUserDetails():Response<UserDto>
    @POST("$AUTH_BASE_URL/register")
    suspend fun signUp(@Body registerRequest: RegisterRequest) : Response<AuthenticationResponse>
///////////////////////////////////////////////////////////////category/////////////////////////////////////////////////////////////

    @Multipart
    @POST("$CATEGORY_BASE_URL/add")
    suspend fun addCategoryApiWithImage(
    @Query("categoryDto") categoryDto:String,
    @Part file: MultipartBody.Part? = null): Response<CategoryDto>
    @Multipart
    @PUT("$CATEGORY_BASE_URL/update")
    suspend fun updateCategory(
    @Query("categoryDto") categoryDto:String,
    @Part file: MultipartBody.Part? = null): Response<CategoryDto>
    @GET("$CATEGORY_BASE_URL/get/{companyId}")
    suspend fun getAllCategoryByCompany( @Path("companyId")companyId : Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int ): PaginatedResponse<CategoryDto>
    @GET("$CATEGORY_BASE_URL/get_all/{companyId}")
    suspend fun getPagingCategoryByCompany(@Path("companyId") companyId : Long,@Query("page") page : Int, @Query("pageSize") pageSize : Int):List<CategoryDto>

    @POST("$CATEGORY_BASE_URL/add_without_image")
    suspend fun addCategoryApiWithoutImage(@Query("categoryDto") categoryDto:String) : Response<CategoryDto>
    @PUT("$CATEGORY_BASE_URL/update_without_image")
    suspend fun updateCategoryWithoutImage(@Query("categoryDto") categoryDto:String) : Response<CategoryDto>
///////////////////////////////////////////////////////////////////subcategory/////////////////////////////////////////////////////////////////////
    @Multipart
    @POST("$SUBCATEGORY_BASE_URL/add")
    suspend fun addSubCategoryWithImage(
    @Query("sousCategory") sousCategory:String,
    @Part file: MultipartBody.Part? = null): Response<SubCategoryDto>
    @POST("$SUBCATEGORY_BASE_URL/add_without_image")
    suspend fun addSubCategoryWithoutImage(@Query("sousCategory") sousCategory:String) : Response<SubCategoryDto>
    @PUT("$SUBCATEGORY_BASE_URL/update_without_image")
    suspend fun updateSubCategoryWithoutImage(@Query("sousCategory") sousCategory:String) : Response<SubCategoryDto>
    @Multipart
    @PUT("$SUBCATEGORY_BASE_URL/update")
    suspend fun updateSubCategory(
        @Query("sousCategory") sousCategory:String,
        @Part file: MultipartBody.Part? = null): Response<SubCategoryDto>
    @GET("$SUBCATEGORY_BASE_URL/getbycompany/{companyId}")
    suspend fun getAllSubCategories(@Path("companyId") companyId : Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int): PaginatedResponse<SubCategoryDto>
    @GET("$SUBCATEGORY_BASE_URL/getbycategory_id/{companyId}")
    suspend fun getAllSubCategoriesByCategoryId(@Path("companyId") companyId : Long, @Query("categoryId") categoryId : Long, @Query("page") page : Int, @Query("pageSize") pageSize : Int): List<SubCategoryDto>
    //////////////////////////////////////////////////////////////////// comment /////////////////////////////////////////////////////////////////
    @GET("$ARTICLE_BASE_URL/get_comments/{articleId}")
    suspend fun getArticleComments(@Path("articleId") articleId : Long , @Query("page") page : Int , @Query("pageSize") pageSize : Int) : PaginatedResponse<CommentDto>





}
