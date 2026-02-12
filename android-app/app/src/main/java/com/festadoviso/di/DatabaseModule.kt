package com.festadoviso.di

import android.content.Context
import com.festadoviso.data.local.AppDatabase
import com.festadoviso.data.local.dao.AdminDao
import com.festadoviso.data.local.dao.FolhaDao
import com.festadoviso.data.local.dao.RegistoDao
import com.festadoviso.data.local.dao.VencedorDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para fornecer instâncias da base de dados e DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideFolhaDao(database: AppDatabase): FolhaDao {
        return database.folhaDao()
    }

    @Provides
    fun provideRegistoDao(database: AppDatabase): RegistoDao {
        return database.registoDao()
    }

    @Provides
    fun provideVencedorDao(database: AppDatabase): VencedorDao {
        return database.vencedorDao()
    }

    @Provides
    fun provideAdminDao(database: AppDatabase): AdminDao {
        return database.adminDao()
    }
}
