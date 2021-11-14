package com.mariofronza.agis.models

data class SessionResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String
)
