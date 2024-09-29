package com.aymen.store.model.entity.realm

import com.aymen.store.model.Enum.PaymentMode
import com.aymen.store.model.Enum.Status
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.LocalDateTime


class Payment : RealmObject {

    @PrimaryKey
    var id : Long ? = null

    var amount: Double? = null

    var delay: String? = null

    var agency: String? = null

    var bankAccount: String? = null

    var number: String? = null

    var transactionId: String? = null

    var status: String? = Status.INWAITING.toString()

    var type: String? = PaymentMode.CASH.toString()

    var invoice: Invoice? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}