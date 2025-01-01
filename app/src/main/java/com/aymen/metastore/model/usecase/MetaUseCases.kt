package com.aymen.metastore.model.usecase

data class MetaUseCases(
    val getPagingCategoryByCompany: GetPagingCategoryByCompany,
    val getCategoryTemp : GetCategoryTemp,
    val getPagingSubCategoryByCompany : GetPagingSubCategoryByCompany,
    val getPagingArticleCompanyByCompany : GetPagingArticleCompanyByCompany,
    val getRandomArticle : GetRandomArticle,
    val getArticleDetails: GetArticleDetails,
    val getAllMyArticleContaining: GetAllMyArticleContaining,
    val getAllMyClient: GetAllMyClient,
    val getAllInvoices: GetAllInvoices,
    val getAllRechargeHistory: GetAllRechargeHistory,
    val getAllInvoicesAsClient: GetAllInvoicesAsClient,
    val getAllInvoicesAsClientAndStatus: GetAllInvoicesAsClientAndStatus,
    val getAllMyInventory : GetAllMyInventory,
    val getAllCompaniesContaining : GetAllCompaniesContaining,
    val getAllMyInvitations : GetAllMyInvitations,
    val getAllMyPaymentsEspeceByDate : GetAllMyPaymentsEspeceByDate,
    val getAllMyPointsPaymentForProvider : GetAllMyPointsPaymentForPoviders,
    val getAllPersonContaining : GetAllPersonContaining,
    val getArticlesForCompanyByCompanyCategory : GetArticlesForCompanyByCompanyCategory,
    val getAllMyProviders: GetAllMyProviders,
    val getAllMyOrdersNotAccepted : GetAllMyOrdersNotAccepted,
    val getPurchaseOrderDetails: GetPurchaseOrderDetails,
    val getAllMyBuyHistory : GetAllMyBuyHistory,
    val getNotAcceptedInvoice: GetNotAcceptedInvoice,
    val getAllMyProfitsPerDay : GetAllMyProfitsPerDay,
    val getMyHistoryProfitByDate : GetMyHistoryProfitByDate,
    val getAllSearchHistory : GetAllSearchHistory,
    val getAllSubCategoryByCategoryId: GetAllSubCategoryByCategoryId,
    val getAllOrdersLineByInvoiceId : GetAllOrdersLineByInvoiceId,
    val getAllCompanyArticles : GetAllCompanyArticles,
    val getAllSubCategoriesByCompanyId : GetAllSubCategoriesByCompanyId,
    val getArticlesByCompanyAndCategoryOrSubCategory : GetArticlesByCompanyAndCategoryOrSubCategory,
    val getAllCommandLineByInvoiceId : GetAllCommandLineByInvoiceId,
    val getMyClientForAutocompleteClient : GetMyClientForAutocompleteClient,
    val getArticleComment : GetArticleComment,
    val getAllWorkers: GetAllWorkers,
    val getPaymentForProviderDetails : GetPaymentForProviderDetails,
    val getRateeRating: GetRateeRating
)
