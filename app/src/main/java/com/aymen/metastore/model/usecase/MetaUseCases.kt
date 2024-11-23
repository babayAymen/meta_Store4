package com.aymen.metastore.model.usecase

data class MetaUseCases(
    val getPagingCategoryByCompany: GetPagingCategoryByCompany,
    val getPagingSubCategoryByCompany : GetPagingSubCategoryByCompany,
    val getPagingArticleCompanyByCompany : GetPagingArticleCompanyByCompany,
    val getRandomArticle : GetRandomArticle,
    val getArticleDetails: GetArticleDetails,
    val getAllMyArticleContaining: GetAllMyArticleContaining,
    val getAllMyClient: GetAllMyClient,
    val getAllMyClientContaining : GetAllMyClientContaining,
    val getAllMessagesByConversation: GetAllMessagesByConversation,
    val getAllConversation: GetAllConversation,
    val getAllInvoices: GetAllInvoices,
    val getAllRechargeHistory: GetAllRechargeHistory,
    val getAllInvoicesAsClient: GetAllInvoicesAsClient,
    val getAllInvoicesAsClientAndStatus: GetAllInvoicesAsClientAndStatus,
    val getAllMyInventory : GetAllMyInventory,
    val getAllCompaniesContaining : GetAllCompaniesContaining,
    val getAllMyInvitations : GetAllMyInvitations,
    val getAllMyPaymentsEspece : GetAllMyPaymentsEspece,
    val getAllMyPaymentsEspeceByDate : GetAllMyPaymentsEspeceByDate,
    val getAllMyPointsPayment : GetAllMyPointsPayment,
    val getAllPersonContaining : GetAllPersonContaining,
    val getArticlesForCompanyByCompanyCategory : GetArticlesForCompanyByCompanyCategory,
    val getAllMyProviders: GetAllMyProviders,
    val getAllMyOrdersNotAccepted : GetAllMyOrdersNotAccepted,
    val getPurchaseOrderDetails: GetPurchaseOrderDetails
//    val getAllMyPaymentFromInvoice : GetAllMyPaymentFromInvoice,

)
