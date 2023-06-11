package ru.netology.diplom.repositry

import androidx.paging.*
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import ru.netology.diplom.api.ApiService
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.dao.PostDao
import ru.netology.diplom.dao.PostRemoteKeyDao
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

        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun registrationWithAvatar(login: String, pass: String, name: String, media: MediaModel) {
        try {
            val part = MultipartBody.Part.createFormData(
                "file", media.file.name, media.file.asRequestBody()
            )

            val response = apiService.registerWithAvatar(
                login.toRequestBody("text/plain".toMediaType()),
                pass.toRequestBody("text/plain".toMediaType()),
                name.toRequestBody("text/plain".toMediaType()),
                part
            )

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val field = response.body() ?: throw HttpException(response)
            field.token?.let { appAuth.setAuth(field.id, it) }
  //          _signUpApp.postValue(body)
   //         _stateSignUp.value = SignUpModelState()
  //          clearAvatar()

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

    override suspend fun removePostById(id: Long) {
        try {
            val postResponce = apiService.deletePostById(id)
            if (!postResponce.isSuccessful) {
                throw  ApiError(postResponce.code(), postResponce.message())
            }
            postDao.removeById(id)
        } catch (_: Exception) {}
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
            val posts = post.copy(attachment = Attachment(media.url, type = mediaModel.type)) //TypeAttachment.IMAGE))
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