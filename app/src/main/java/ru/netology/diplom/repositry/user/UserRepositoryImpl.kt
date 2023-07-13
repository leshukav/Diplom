package ru.netology.diplom.repositry.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.diplom.api.ApiService
import ru.netology.diplom.dao.user.UserDao
import ru.netology.diplom.dto.User
import ru.netology.diplom.entity.UserEntity
import ru.netology.diplom.entity.toDto
import ru.netology.diplom.error.ApiError
import ru.netology.diplom.error.NetworkError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService,
): UserRepository {

    override val userData: LiveData<List<User>> = userDao.get()
        .map(List<UserEntity>::toDto)

    override suspend fun getUsers() {
        try {
            val response = apiService.getUsers()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val users = response.body().orEmpty()
            userDao.insert(users.map(UserEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        }
    }
}