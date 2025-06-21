package com.popiin

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class APIClientMap {
    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient().newBuilder()
            httpClient.connectTimeout(10000, TimeUnit.SECONDS)
                .readTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(10000, TimeUnit.SECONDS)
            httpClient.addInterceptor(interceptor)
            val gson = GsonBuilder()
                .setLenient()
                .create()
            retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()
            return retrofit
        }

    val placeSearchClient: Retrofit?
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient().newBuilder()
            httpClient.connectTimeout(10000, TimeUnit.SECONDS)
                .readTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(10000, TimeUnit.SECONDS)
            httpClient.addInterceptor(interceptor)
            val gson = GsonBuilder()
                .setLenient()
                .create()
            retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/textsearch/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()
            return retrofit
        }

    val directionsClient: Retrofit?
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient().newBuilder()
            httpClient.connectTimeout(10000, TimeUnit.SECONDS)
                .readTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(10000, TimeUnit.SECONDS)
            httpClient.addInterceptor(interceptor)
            val gson = GsonBuilder()
                .setLenient()
                .create()
            retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()
            return retrofit
        }

    val placeDetailsClient: Retrofit?
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient().newBuilder()
            httpClient.connectTimeout(10000, TimeUnit.SECONDS)
                .readTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(10000, TimeUnit.SECONDS)
            httpClient.addInterceptor(interceptor)
            val gson = GsonBuilder()
                .setLenient()
                .create()
            retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/details/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()
            return retrofit
        }


}