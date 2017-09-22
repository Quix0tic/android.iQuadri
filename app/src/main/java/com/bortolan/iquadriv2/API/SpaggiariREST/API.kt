package com.bortolan.iquadriv2.API.SpaggiariREST

import com.bortolan.iquadriv2.API.SpaggiariREST.models.Grade
import com.bortolan.iquadriv2.API.SpaggiariREST.models.LoginRequest
import com.bortolan.iquadriv2.API.SpaggiariREST.models.LoginResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface API {
    @POST("rest/v1/auth/login")
    fun doLogin(@Body loginRequest: LoginRequest): Observable<LoginResponse>

    @GET("rest/v1/students/{studentId}/grades")
    fun getGrades(@Path(value = "studentId") paramString2: String): Observable<List<Grade>>

}