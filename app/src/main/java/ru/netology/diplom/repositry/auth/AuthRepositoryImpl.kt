package ru.netology.diplom.repositry.auth

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import ru.netology.diplom.api.ApiService
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.error.ApiError
import ru.netology.diplom.error.NetworkError
import ru.netology.diplom.model.MediaModel
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val appAuth: AppAuth,
) : AuthRepository {
    override suspend fun authorization(login: String, password: String) {
        try {
            val response = apiService.authUser(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val field = response.body() ?: throw HttpException(response)
            field.token?.let { appAuth.setAuth(field.id, it) }
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun registration(login: String, password: String, name: String) {
        try {
            val response = apiService.registrationUser(login, password, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val field = response.body() ?: throw HttpException(response)
            field.token?.let { appAuth.setAuth(field.id, it) }

        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun registrationWithAvatar(
        login: String,
        password: String,
        name: String,
        media: MediaModel
    ) {
        try {
            val part = MultipartBody.Part.createFormData(
                "file", media.file.name, media.file.asRequestBody()
            )

            val response = apiService.registerWithAvatar(
                login.toRequestBody("text/plain".toMediaType()),
                password.toRequestBody("text/plain".toMediaType()),
                name.toRequestBody("text/plain".toMediaType()),
                part
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val field = response.body() ?: throw HttpException(response)
            field.token?.let { appAuth.setAuth(field.id, it) }
        } catch (e: IOException) {
            throw NetworkError
        }
    }
}