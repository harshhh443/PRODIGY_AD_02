package com.epicdevler.ad.prodigytasks.util.di

import android.content.Context
import androidx.room.Room
import com.epicdevler.ad.prodigytasks.data.repos.TasksRepository
import com.epicdevler.ad.prodigytasks.data.source.MainDataSourceImpl
import com.epicdevler.ad.prodigytasks.data.source.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object SingletonModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context


    @Singleton
    @Provides
    fun provideDB(
        context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "tododb.db"
    ).build()

    @Provides
    @Singleton
    fun provideTasksRepo(
        db: AppDatabase
    ): TasksRepository = TasksRepository(MainDataSourceImpl(db.todoDao()))
}