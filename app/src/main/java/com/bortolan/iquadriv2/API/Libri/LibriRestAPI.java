package com.bortolan.iquadriv2.API.Libri;

import android.support.annotation.NonNull;

import com.bortolan.iquadriv2.Interfaces.Libri.Announcement;
import com.bortolan.iquadriv2.Interfaces.Libri.AnnouncementResponse;
import com.bortolan.iquadriv2.Interfaces.Libri.UserResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LibriRestAPI {

    @GET("/announcements/{city}")
    Observable<List<Announcement>> getAnnouncements(@Path("city") String city);

    @POST("login")
    @FormUrlEncoded
    Observable<UserResponse> postLogin(
            @NonNull @Field("phone") String phone,
            @NonNull @Field("password") String password);

    @POST("signup")
    @FormUrlEncoded
    Observable<UserResponse> postSignup(
            @NonNull @Field("phone") String phone,
            @NonNull @Field("password") String password,
            @NonNull @Field("city") String city,
            @NonNull @Field("name") String name);

    @POST("announcements")
    @FormUrlEncoded
    Observable<AnnouncementResponse> postAnnouncement(
            @NonNull @Field("title") String title,
            @NonNull @Field("isbn") String isbn,
            @NonNull @Field("subject") String subject,
            @NonNull @Field("edition") String edition,
            @NonNull @Field("grade") String grade,
            @NonNull @Field("notes") String notes,
            @NonNull @Field("price") int price);
}
