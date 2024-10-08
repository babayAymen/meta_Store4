package com.aymen.store.model.repository.remoteRepository.companyRepository

import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.Parent
import com.aymen.store.model.entity.realm.Provider
import retrofit2.Response
import java.io.File

interface CompanyRepository {

    suspend fun addCompany(company: String, file : File)

    suspend fun getAllMyProvider(companyId: Long): Response<List<Provider>>

    suspend fun getMyParent(companyId: Long): Response<Parent>

    suspend fun getMyCompany(companyId: Long): Response<Company>

    suspend fun getMe(): Response<Company>

    suspend fun getAllCompaniesContaining(search : String): Response<List<Company>>

    suspend fun updateCompany(company: String , file : File)

    suspend fun updateImage(image : File) : Response<Void>

}