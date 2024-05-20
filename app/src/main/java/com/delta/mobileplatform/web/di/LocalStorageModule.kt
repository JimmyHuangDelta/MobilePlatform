package com.delta.mobileplatform.web.di

import android.content.Context
import androidx.room.Room
import com.delta.mobileplatform.web.controller.localStorage.DBRepositoryImpl
import com.delta.mobileplatform.web.controller.localStorage.DbRepository
import com.delta.mobileplatform.web.controller.localStorage.DeltaLocalDB
import com.delta.mobileplatform.web.controller.localStorage.LocalStorageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object LocalStorageModule {
    private var database: DeltaLocalDB? = null
    private var localStorageDao: LocalStorageDao? = null
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): DeltaLocalDB {
        if (database == null) {
            database = Room.databaseBuilder(
                appContext,
                DeltaLocalDB::class.java,
                "local_storage"
            ).build()
        }
        return database!!
    }

    @Provides
    fun provideLocalStorageDao(database: DeltaLocalDB): LocalStorageDao {
        return database.localStorageDao()
    }

    @Provides
    fun provideDbRepository(localStorageDao: LocalStorageDao): DbRepository {
        return DBRepositoryImpl(localStorageDao)
    }
}