package ru.netology.diplom.repositry

import android.util.Log
import androidx.paging.*
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import ru.netology.diplom.api.ApiService
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.dao.PostDao
import ru.netology.diplom.dao.PostRemoteKeyDao
import ru.netology.diplom.db.AppDb
import ru.netology.diplom.entity.PostEntity
import ru.netology.diplom.error.ApiError
import ru.netology.diplom.error.NetworkError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    private val appAuth: AppAuth,
    private val appDb: AppDb,
    private val postRemoteKeyDao: PostRemoteKeyDao,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data = Pager(
    config = PagingConfig(10, enablePlaceholders = false),
    pagingSourceFactory = { postDao.getPaging() },
    remoteMediator = PostRemoteMediator(
    apiService = apiService,
    postDao = postDao,
    postRemoteKeyDao = postRemoteKeyDao,
    appDb = appDb
    )
    ).flow
    .map { it.map(PostEntity::toDto) }


    override suspend fun authorization(login: String, pass: String) {
        try {
            val response = apiService.authUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val field = response.body() ?: throw HttpException(response)
            field.token?.let { appAuth.setAuth(field.id, it) }
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun registration(login: String, pass: String, name: String) {
        try {
            val response = apiService.registrationUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val field = response.body() ?: throw HttpException(response)
            field.token?.let { appAuth.setAuth(field.id, it) }
            Log.d("MyTag", "Token -> ${field.token}")
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun getPosts() {
        try {
            val response = apiService.getAllPosts()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val posts = response.body().orEmpty()
            postDao.insert(posts.map(PostEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun likeById(id: Long) {
        postDao.likeById(id)
        try {
            val postResponse = apiService.likePostById(id)
            if (!postResponse.isSuccessful) {
                throw ApiError(postResponse.code(), postResponse.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun unlikeById(id: Long) {
        postDao.likeById(id)
        try {
            val postResponse = apiService.unlikePostById(id)
            if (!postResponse.isSuccessful) {
                throw ApiError(postResponse.code(), postResponse.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun cancelLike(id: Long) {
        postDao.likeById(id)
    }
}