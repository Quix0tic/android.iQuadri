package com.bortolan.iquadriv2.API.SpaggiariREST.models

data class LoginResponse(
        val ident: String,
        val firstName: String,
        val lastName: String,
        val token: String,
        val release: String,
        val expire: String
)