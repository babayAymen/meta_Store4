package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.Invitation
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type

data class InvitationDto(

    val id : Long? = null,
    var client : UserDto? = null,
    var companySender : CompanyDto? = null,
    var companyReceiver : CompanyDto? = null,
    var salary : Double? = null,
    var jobtitle : String? = null,
    var department : String? = null,
    var totdayvacation : Long? = null,
    var statusvacation : Boolean? = null,
    var status : Status? = null,
    var type : Type? = null
){
    fun toInvitation() : Invitation {

        return Invitation(
            id = id,
            clientId = client?.id,
            companySenderId = companySender?.id,
            companyReceiverId = companyReceiver?.id,
            salary = salary,
            jobtitle = jobtitle,
            department = department,
            totdayvacation = totdayvacation,
            statusvacation = statusvacation,
            status = status,
            type = type
        )
    }
    fun toInvitationModel() : com.aymen.metastore.model.entity.model.Invitation{
        return com.aymen.metastore.model.entity.model.Invitation(
            id = id,
            client = client?.toUserModel(),
            companySender = companySender?.toCompanyModel(),
            companyReceiver = companyReceiver?.toCompanyModel(),
            salary = salary,
            jobtitle = jobtitle,
            department = department,
            totdayvacation = totdayvacation,
            statusvacation = statusvacation,
            status = status,
            type = type
        )
    }
}
