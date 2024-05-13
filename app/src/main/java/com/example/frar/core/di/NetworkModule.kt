package com.example.frar.core.di

import com.example.frar.data.network.ImageUploadApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Retrofit.Builder {
        val BASE_URL = "http://192.168.29.231:5000"
//        val BASE_URL ="http://127.0.0.1:5000/"
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
    }

    @Singleton
    @Provides
    fun providesUploadImageApi(retrofitBuilder:Retrofit.Builder):ImageUploadApi{
        return retrofitBuilder.build().create(ImageUploadApi::class.java)
    }
}