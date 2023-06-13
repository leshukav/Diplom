package ru.netology.diplom.repositry

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.dto.*
import ru.netology.diplom.model.MediaModel

interface PostRepository {

    val data: Flow<PagingData<Post>>

    val wallData: LiveData<List<Wall>>

    val userData: LiveData<User>

    suspend fun authorization(login: String, pass: String)

    suspend fun registration(login: String, pass: String,name: String)

    suspend fun registrationWithAvatar(login: String, pass: String, name: String, media: MediaModel)

    suspend fun getPosts()

    suspend fun likeById(id: Long)

    suspend fun unlikeById(id: Long)

    suspend fun cancelLike(id: Long)

    suspend fun removePostById(id: Long)

    suspend fun removeWallPostDao(id: Long)

    suspend fun save(post: PostCreate)

    suspend fun saveWithAttachment(post: PostCreate, media: MediaModel)

    suspend fun upload(upload: MediaModel): Media

    suspend fun getWallByAuthorId(id: Long)

    suspend fun getUserById(id: Long)

}