package com.aymen.store.model.repository.globalRepository

import com.aymen.metastore.model.repository.remoteRepository.CommandLineRepository.CommandLineRepository
import com.aymen.metastore.model.repository.remoteRepository.aymenRepository.AymenRepository
import com.aymen.metastore.model.repository.remoteRepository.ratingRepository.RatingRepository
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
import com.aymen.store.model.repository.remoteRepository.orderRepository.OrderRepository
import com.aymen.store.model.repository.remoteRepository.paymentRepository.PaymentRepository
import com.aymen.store.model.repository.remoteRepository.providerRepository.ProviderRepository
import com.aymen.store.model.repository.remoteRepository.signInRepository.SignInRepository
import com.aymen.store.model.repository.remoteRepository.subCategoryRepository.SubCategoryRepository
import com.aymen.store.model.repository.remoteRepository.workerRepository.WorkerRepository

interface GlobalRepository :
    SignInRepository,
    ArticleRepository, CategoryRepository,
    SubCategoryRepository, CompanyRepository,
    InventoryRepository,
    ClientRepository, ProviderRepository,
        PaymentRepository, OrderRepository,
        WorkerRepository, InvoiceRepository,
    MessageRepository, ShoppingRepository,
    InvetationRepository, PointPaymentRepository,
        RatingRepository, AymenRepository,
        CommandLineRepository
{
}