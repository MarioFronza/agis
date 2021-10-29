package com.mariofronza.agis.models

data class UserRequest(
    val username: String = "Test",
    val email: String,
    val password: String,
    val role: String = "STUDENT",
    val sigaCredential: SigaCredential,
)

data class SigaCredential(
    val cpf: String,
    val password: String
)
