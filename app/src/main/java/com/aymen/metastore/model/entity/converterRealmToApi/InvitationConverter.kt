package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.room.Invitation
import com.aymen.store.model.entity.dto.InvitationDto

fun mapInvitationToRoomInvitation(invitation: InvitationDto): Invitation {
    return Invitation(
        id = invitation.id,
        clientId = invitation.client?.id,
        companySenderId = invitation.companySender?.id,
        companyReceiverId = invitation.companyReceiver?.id,
        workerId = invitation.worker?.id,
        salary = invitation.salary,
        jobtitle = invitation.jobtitle,
        department = invitation.department,
        totdayvacation = invitation.totdayvacation,
        statusvacation = invitation.statusvacation,
        status = invitation.status,
        type = invitation.type

    )
}