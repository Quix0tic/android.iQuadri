package com.bortolan.iquadriv2.API.Spaggiari;

import android.content.Context;

import com.bortolan.iquadriv2.Utils.Methods;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.gms.security.ProviderInstaller;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpiaggiariApiClient {
    public final RESTfulAPIService mService;

    public SpiaggiariApiClient(Context context) {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        File cacheDir = context.getCacheDir();
        Cache cache = new Cache(cacheDir, cacheSize);

        Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            if (Methods.isNetworkAvailable(context)) {
                int maxAge = 60; // read from cache for 1 minute
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        };

        try {
            //Installa il supporto al TSL se non è presente
            ProviderInstaller.installIfNeeded(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .cache(cache)
                .hostnameVerifier((s, sslSession) -> HttpsURLConnection.getDefaultHostnameVerifier().verify("daniele.ml", sslSession))
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .build();

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.daniele.ml/")
                .client(okHttpClient)
                .build();

        // Build the api
        mService = retrofit.create(RESTfulAPIService.class);
    }
}
