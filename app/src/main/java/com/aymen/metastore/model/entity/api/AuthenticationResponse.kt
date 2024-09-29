package com.aymen.store.model.entity.api

import kotlinx.serialization.Serializer

data class AuthenticationResponse(
    val token : String = ""
)
