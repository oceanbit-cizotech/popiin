package com.popiin

import android.content.Context
import com.google.gson.GsonBuilder
import com.popiin.util.NativeLib
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.cert.X509Certificate
import java.security.NoSuchAlgorithmException
import java.security.KeyManagementException
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

class ApiClient(private val context: Context) {
    companion object {
        val BASE_URL = NativeLib.getBaseUrl(BuildConfig.DEBUG)
    }

    private var retrofit: Retrofit? = null

    val client: Retrofit?
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
               /* .addInterceptor(Interceptor { chain ->
                    val originalRequest = chain.request()
                    val request = originalRequest.newBuilder()
                        .method(originalRequest.method, originalRequest.body)
                        .build()
                    chain.proceed(request)
                })*/
                .addInterceptor(NetworkConnectionInterceptor(context)) // Network connectivity check
                .addInterceptor(interceptor)
                .sslSocketFactory(createSSLSocketFactory(), createTrustManager())  // Apply custom SSL Socket Factory and TrustManager
                .hostnameVerifier(HostnameVerifier { _, _ -> true })  // Accept any hostname for testing purposes
                .build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build()
            }
            return retrofit
        }

    // Create a custom SSLSocketFactory that ignores SSL validation (for testing)
    private fun createSSLSocketFactory(): SSLSocketFactory {
        return try {
            val trustAllCertificates = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCertificates, java.security.SecureRandom())

            sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Error creating SSL context", e)
        } catch (e: KeyManagementException) {
            throw RuntimeException("Error initializing SSL context", e)
        }
    }

    // Create a custom TrustManager that trusts all certificates (for testing)
    private fun createTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    }
}
