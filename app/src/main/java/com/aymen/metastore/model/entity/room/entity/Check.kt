package com.aymen.metastore.model.entity.room.entity

import io.realm.kotlin.types.annotations.PrimaryKey

data class Check(
    @PrimaryKey
    val id :Long? = null,

    val number: String? = null,

    val amount: Double? = null,

    val agency: String? = null,

    val delay: String? = null,

    val bankAccount: String? = null,

    val invoiceId: Long? = null,

    val createdDate : String = "",

    val lastModifiedDate : String = "",
)
