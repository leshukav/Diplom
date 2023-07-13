package ru.netology.diplom.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.diplom.dao.event.EventDao
import ru.netology.diplom.dao.event.EventRemoteKeyDao
import ru.netology.diplom.dao.job.JobDao
import ru.netology.diplom.dao.post.PostDao
import ru.netology.diplom.dao.post.PostRemoteKeyDao
import ru.netology.diplom.dao.wall.WallDao
import ru.netology.diplom.dao.user.UserDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(
        appDb: AppDb
    ): PostDao = appDb.postDao()

    @Provides
    fun providePostRemoteKeyDao(
        appDb: AppDb
    ): PostRemoteKeyDao = appDb.postRemoteKeyDao()

    @Provides
    fun provideEventRemoteKeyDao(
        appDb: AppDb
    ): EventRemoteKeyDao = appDb.eventRemoteKeyDao()

    @Provides
    fun provideEventDao(
        appDb: AppDb
    ): EventDao = appDb.eventDao()

    @Provides
    fun provideWallDao(
        appDb: AppDb
    ): WallDao = appDb.wallDao()

    @Provides
    fun provideJobDao(
        appDb: AppDb
    ): JobDao = appDb.jobDao()

    @Provides
    fun provideUserDao(
        appDb: AppDb
    ): UserDao = appDb.userDao()
}