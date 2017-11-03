package com.bortolan.iquadriv2.API.SpaggiariREST

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.bortolan.iquadriv2.Interfaces.models.LoginRequest
import com.bortolan.iquadriv2.Interfaces.models.LoginResponse
import com.google.android.gms.security.ProviderInstaller
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class APIClient {
    companion object {
        var dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())

        fun create(context: Context): API {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)


            val loginInterceptor = Interceptor { chain: Interceptor.Chain ->
                val original = chain.request()

                //EXPIRED TOKEN && NOT LOGGIN IN
                if (original.url().toString() != "https://web.spaggiari.eu/rest/v1/auth/login" && sharedPref.getLong("spaggiari-expireDate", 0L) < System.currentTimeMillis()) {
                    Log.d("LOGIN INTERCEPTOR", "TOKEN EXPIRED, REQUESTING NEW TOKEN")

                    val loginRes = chain.proceed(original.newBuilder()
                            .url("https://web.spaggiari.eu/rest/v1/auth/login")
                            .method("POST",
                                    RequestBody.create(
                                            MediaType.parse("application/json"),
                                            LoginRequest(sharedPref.getString("spaggiari-pass", ""), sharedPref.getString("spaggiari-user", "")).toString() //properly override to provide a json-like string
                                    )
                            )
                            .header("User-Agent", "zorro/1.0")
                            .header("Z-Dev-Apikey", "+zorro+")
                            .build())

                    if (loginRes.isSuccessful) {
                        val loginResponse = Gson().fromJson(loginRes.body()?.string(), LoginResponse::class.java)

                        Log.d("LOGIN INTERCEPTOR", "UPDATE TOKEN: " + loginResponse.token)

                        sharedPref.edit()
                                .putString("spaggiari-token", loginResponse.token)
                                .putLong("spaggiari-expireDate", APIClient.dateFormat.parse(loginResponse.expire).time)
                                .putBoolean("spaggiari-logged", true)
                                .apply()
                        chain.proceed(original)
                    } else {
                        Log.d("LOGIN INTERCEPTOR", loginRes.body().toString())
                        sharedPref.edit().putBoolean("spaggiari-logged", false).apply()
                        chain.proceed(original)
                    }

                } else {
                    chain.proceed(original)
                }

            }
            try {
                //Installa il supporto al TSL se non è presente
                ProviderInstaller.installIfNeeded(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val zorro = Interceptor { chain ->
                val original = chain.request()

                val request = original.newBuilder()
                        .header("User-Agent", "zorro/1.0")
                        .header("Z-Dev-Apikey", "+zorro+")
                        .header("Z-Auth-Token", sharedPref.getString("spaggiari-token", ""))
                        .method(original.method(), original.body())
                        .build()

                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                    .addInterceptor(zorro)
                    .addInterceptor(loginInterceptor)
                    .build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://web.spaggiari.eu/")
                    .client(client)
                    .build()

            return retrofit.create(API::class.java)
        }
    }
}