package co.aos.network.di

import android.content.Context
import co.aos.network.ApiManager
import co.aos.network.check.NetworkStatusManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * API 연동을 위한 Retrofit 모듈
 * */
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    /** 주입을 위한 retrofit 객체 생성 */
    @Provides
    @Singleton
    fun provideRetrofit(retrofitBuilder: Retrofit.Builder, client: OkHttpClient.Builder): Retrofit {
        return retrofitBuilder.apply {
            client(client.build())
        }.build()
    }

    /** retrofit 관련 builder */
    @Provides
    @Singleton
    fun provideRetrofitBuilder(client: OkHttpClient.Builder): Retrofit.Builder {
        return Retrofit.Builder().apply {
            ApiManager.baseUrl?.let { baseUrl(it) }
            client(client.build())
            addConverterFactory(GsonConverterFactory.create())
        }
    }

    /** okhttp client 객체 생성 */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient.Builder {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder().apply {
            val timeOut = ApiManager.timeOutMilliSec
            connectTimeout(timeOut, TimeUnit.MILLISECONDS) // 연결을 시도할 때의 최대 대기 시간 설정
            readTimeout(timeOut, TimeUnit.SECONDS) // 서버로부터 응답을 기다리는 최대 시간 설정
            writeTimeout(timeOut, TimeUnit.SECONDS) // 서버에 데이터를 전송할 때의 최대 시간 설정

            // 네트워크 요청을 처리하는 스레드 관리
            val dispatcher = Dispatcher()
            dispatcher(dispatcher)
            cache(null) // 캐시는 사용하지 않도록 설정

            // 서버 쿠키 관련
            cookieJar(ApiManager.cookie)

            // logging
            addInterceptor(loggingInterceptor)
        }
    }

    // NetworkManager 의존성 제공
    @Provides
    @Singleton
    fun provideNetworkManager(
        @ApplicationContext context: Context
    ): NetworkStatusManager {
        return NetworkStatusManager(context)
    }
}