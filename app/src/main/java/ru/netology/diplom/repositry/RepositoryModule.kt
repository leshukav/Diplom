package ru.netology.diplom.repositry

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.diplom.repositry.auth.AuthRepository
import ru.netology.diplom.repositry.auth.AuthRepositoryImpl
import ru.netology.diplom.repositry.event.EventRepository
import ru.netology.diplom.repositry.event.EventRepositoryImpl
import ru.netology.diplom.repositry.post.PostRepository
import ru.netology.diplom.repositry.post.PostRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository


    @Singleton
    @Binds
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Singleton
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}