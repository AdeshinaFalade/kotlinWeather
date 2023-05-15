package com.example.weatherapp


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    fun providesWeatherService(client: OkHttpClient): WeatherService {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(WeatherService::class.java)
    }

    @Provides
    fun providesRemoteWeatherDataSource(
        weatherService: WeatherService
    ): RemoteWeatherDataSource {
        return RemoteWeatherDataSource(weatherService)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient{
        var client = OkHttpClient.Builder()
        return client.connectTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
}