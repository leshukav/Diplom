package ru.netology.diplom.repositry.post

import androidx.paging.*
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import ru.netology.diplom.api.ApiService
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.dao.post.PostDao
import ru.netology.diplom.dao.post.PostRemoteKeyDao
import ru.netology.diplom.db.AppDb
import ru.netology.diplom.dto.*
import ru.netology.diplom.entity.PostEntity
import ru.netology.diplom.error.ApiError
import ru.netology.diplom.error.NetworkError
import ru.netology.diplom.model.MediaModel
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
            appDb = appDb,
        )
    ).flow
        .map { it.map(PostEntity::toDto) }

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

        try {
            val postResponse = apiService.likePostById(id)
            if (!postResponse.isSuccessful) {
                throw ApiError(postResponse.code(), postResponse.message())
            }
            postDao.likeById(id, appAuth.getAuthId())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun unlikeById(id: Long) {
        try {
            val postResponse = apiService.unlikePostById(id)
            if (!postResponse.isSuccessful) {
                throw ApiError(postResponse.code(), postResponse.message())
            }
            postDao.unLikeById(id, appAuth.getAuthId())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun cancelLike(id: Long) {
        postDao.unLikeById(id, appAuth.getAuthId())
    }

    override suspend fun removePostById(id: Long) {
        try {
            val postResponce = apiService.deletePostById(id)
            if (!postResponce.isSuccessful) {
                throw ApiError(postResponce.code(), postResponce.message())
            }
            postDao.removeById(id)
        } catch (_: Exception) {
        }
    }

    override suspend fun save(post: PostCreate) {
        try {
            val postResponse = apiService.savePost(post)
            if (!postResponse.isSuccessful) {
                throw ApiError(postResponse.code(), postResponse.message())
            }
            val body = postResponse.body() ?: throw HttpException(postResponse)

            postDao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun saveWithAttachment(post: PostCreate, mediaModel: MediaModel) {
        try {
            val media = upload(mediaModel)
            val posts = post.copy(
                attachment = Attachment(
                    media.url,
                    type = mediaModel.type
                )
            )
            val response = apiService.savePost(posts)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun upload(upload: MediaModel): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file?.name, upload.file?.asRequestBody() ?: throw NetworkError
            )

            val response = apiService.uploadMedia(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

}