package com.bortolan.iquadriv2.API.Spaggiari;

import android.support.annotation.NonNull;

import com.bortolan.iquadriv2.Interfaces.Login;
import com.bortolan.iquadriv2.Interfaces.MarkSubject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RESTfulAPIService {

    @POST("login")
    @FormUrlEncoded
    Observable<Login> postLogin(
            @NonNull @Field("login") String login,
            @NonNull @Field("password") String password,
            @NonNull @Field("key") String key);

    @GET("marks")
    Observable<List<MarkSubject>> getMarks();
}
