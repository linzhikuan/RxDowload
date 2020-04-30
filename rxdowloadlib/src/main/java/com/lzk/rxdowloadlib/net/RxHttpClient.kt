package com.lzk.rxdowloadlib.net

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.lzk.rxdowloadlib.utils.SingletonHolderNoParms
import io.reactivex.*
import okhttp3.*
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

class RxHttpClient {

    fun downLoadRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(getSSLFactory())
            .hostnameVerifier { _, _ -> true }
//            .addInterceptor(loggingInterceptor)
            .connectTimeout(1000.toLong(), TimeUnit.SECONDS)
            .readTimeout(1000.toLong(), TimeUnit.SECONDS)
            .writeTimeout(1000.toLong(), TimeUnit.SECONDS)
            .dns(XDns(1000.toLong()))
            .build()
        return build {
            baseUrl("http://47.114.45.178:8086")
            addConverterFactory(GsonConverterFactory.create())
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            client(okHttpClient)
        }
    }


    private inline fun build(block: Retrofit.Builder.() -> Unit): Retrofit {
        return Retrofit.Builder().apply(block).build()
    }


    companion object : SingletonHolderNoParms<RxHttpClient>(::RxHttpClient)
}


/**订阅线程切换设置 */
fun <T> switchThread(): FlowableTransformer<T, T> {
    return FlowableTransformer { upstream ->
        upstream.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}


fun <T> createDowload(service: Class<T>): T {
    return RxHttpClient.getInstance().downLoadRetrofit().create(service)
}


private fun getSSLFactory(): SSLSocketFactory {
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(
            chain: Array<java.security.cert.X509Certificate>,
            authType: String
        ) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(
            chain: Array<java.security.cert.X509Certificate>,
            authType: String
        ) {
        }
    })

    // Install the all-trusting trust manager
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, java.security.SecureRandom())
    // Create an ssl socket factory with our all-trusting manager
    return sslContext.socketFactory
}