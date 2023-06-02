package ru.netology.diplom.repositry

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.dto.Media
import ru.netology.diplom.dto.Post
import ru.netology.diplom.dto.PostCreate
import ru.netology.diplom.model.MediaModel

interface PostRepository {

    val data: Flow<PagingData<Post>>

    suspend fun authorization(login: String, pass: String)

    suspend fun registration(login: String, pass: String,name: String)

    suspend fun registrationWithAvatar(login: String, pass: String, name: String, media: MediaModel)

    suspend fun getPosts()

    suspend fun likeById(id: Long)

    suspend fun unlikeById(id: Long)

    suspend fun cancelLike(id: Long)

    suspend fun save(post: PostCreate)

    suspend fun saveWithAttachment(post: PostCreate, media: MediaModel)

    suspend fun upload(upload: MediaModel): Media

}