package ru.netology.diplom.repositry

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.dto.Post

interface PostRepository {

    val data: Flow<PagingData<Post>>

    suspend fun authorization(login: String, pass: String)

    suspend fun registration(name: String, login: String, pass: String)

    suspend fun getPosts()

    suspend fun likeById(id: Long)

    suspend fun unlikeById(id: Long)

    suspend fun cancelLike(id: Long)

}