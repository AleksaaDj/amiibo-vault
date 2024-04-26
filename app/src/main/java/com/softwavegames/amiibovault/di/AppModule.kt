package com.softwavegames.amiibovault.di

import android.app.Application
import androidx.room.Room
import com.softwavegames.amiibovault.Constants.BASE_URL
import com.softwavegames.amiibovault.Constants.DB_NAME
import com.softwavegames.amiibovault.data.local.AmiiboDao
import com.softwavegames.amiibovault.data.local.AmiiboDatabase
import com.softwavegames.amiibovault.data.local.AmiiboTypeConverter
import com.softwavegames.amiibovault.data.remote.AmiiboApi
import com.softwavegames.amiibovault.data.repository.AmiiboRepository
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
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        )
        .build()

    @Provides
    @Singleton
    fun provideAmiiboDatabase(
        application: Application
    ): AmiiboDatabase {
        return Room.databaseBuilder(
            context = application,
            klass = AmiiboDatabase::class.java,
            name = DB_NAME
        ).addTypeConverter(AmiiboTypeConverter())
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAmiiboDao(
        amiiboDatabase: AmiiboDatabase
    ): AmiiboDao = amiiboDatabase.amiiboDao

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): AmiiboApi = retrofit.create(AmiiboApi::class.java)

    @Singleton
    @Provides
    fun providesRepository(apiService: AmiiboApi, amiiboDao: AmiiboDao) =
        AmiiboRepository(apiService, amiiboDao)
}