package com.popiin

import android.content.Context
import com.popiin.exceptions.NoConnectivityException
import com.popiin.util.CheckNetwork
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import java.lang.Exception

class NetworkConnectionInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        if (!CheckNetwork.isInternetAvailable(context)) {
            throw NoConnectivityException() // ❌ No retry if no internet
        }

        val request = chain.request()
        var response: Response? = null
        var retryCount = 0
        val maxRetries = 3
        var delay = 2000L // Start with 2s delay (for exponential backoff)

        while (retryCount < maxRetries) {
            try {
                response = chain.proceed(request)

                // ✅ Stop retrying if it's a successful response (200-299)
                if (response.isSuccessful) return response

                // ✅ Stop retrying on permanent errors (400-499) except for rate limit
                if (response.code in 400..499) {
                    val errorBody = response.body?.string()
                    if (errorBody?.contains("Too Many Attempts.") == true) {
                        // 🛑 Stop retrying if server says "Too Many Attempts."
                        return response
                    }
                    return response
                }

                // ✅ Stop retrying on server errors (500-599) except for rate limits
                if (response.code in 500..599) {
                    return response
                }
            } catch (e: Exception) {
                retryCount++
                if (retryCount >= maxRetries) throw e // ❌ Stop after max retries

                // ⏳ Use exponential backoff: 2s -> 4s -> 8s
                Thread.sleep(delay)
                delay *= 2
            }
        }

        return response ?: throw NoConnectivityException()
    }
}
