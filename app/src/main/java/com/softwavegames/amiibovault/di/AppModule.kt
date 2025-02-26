package com.softwavegames.amiibovault.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import com.softwavegames.amiibovault.data.local.AmiiboDao
import com.softwavegames.amiibovault.data.local.AmiiboDatabase
import com.softwavegames.amiibovault.data.local.ReleaseTypeConverter
import com.softwavegames.amiibovault.data.remote.AmiiboApi
import com.softwavegames.amiibovault.data.repository.AmiiboRepository
import com.softwavegames.amiibovault.domain.analytics.FirebaseEventsLogs
import com.softwavegames.amiibovault.domain.util.Constants.BASE_URL
import com.softwavegames.amiibovault.domain.util.Constants.DB_NAME
import com.softwavegames.amiibovault.domain.util.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.qualifiers.ApplicationContext
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
                .retryOnConnectionFailure(true)
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
        ).addTypeConverter(ReleaseTypeConverter())
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAmiiboDao(
        amiiboDatabase: AmiiboDatabase
    ): AmiiboDao = amiiboDatabase.amiiboDao

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return getApplication(appContext).getSharedPreferences(
            SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): AmiiboApi = retrofit.create(AmiiboApi::class.java)

    @Singleton
    @Provides
    fun providesRepository(apiService: AmiiboApi, amiiboDao: AmiiboDao) =
        AmiiboRepository(apiService, amiiboDao)

    @Singleton
    @Provides
    fun provideFirebaseEventsLogs(@ApplicationContext appContext: Context): FirebaseEventsLogs {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(appContext)
        return FirebaseEventsLogs(firebaseAnalytics)
    }
}