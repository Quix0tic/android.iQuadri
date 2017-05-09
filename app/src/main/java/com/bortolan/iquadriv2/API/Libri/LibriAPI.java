package com.bortolan.iquadriv2.API.Libri;

import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.gms.security.ProviderInstaller;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.schedulers.Schedulers;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LibriAPI {
    public final LibriRestAPI mService;

    public LibriAPI(Context context) {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context.getSharedPreferences("CookiesLibriAPI", Context.MODE_PRIVATE)));
        try {
            //Installa il supporto al TSL se non è presente
            ProviderInstaller.installIfNeeded(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.bortolan.ml/")
                .client(okHttpClient)
                .build();

        // Build the api
        mService = retrofit.create(LibriRestAPI.class);
    }
}
