package com.bortolan.iquadriv2.Interfaces.models

data class LoginResponse(
        val ident: String,
        val firstName: String,
        val lastName: String,
        val token: String,
        val release: String,
        val expire: String
)