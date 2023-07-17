package ru.netology.diplom.repositry.wall

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.diplom.api.ApiService
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.dao.wall.WallDao
import ru.netology.diplom.db.AppDb
import ru.netology.diplom.dto.Wall
import ru.netology.diplom.entity.WallEntity
import ru.netology.diplom.entity.toDto
import ru.netology.diplom.error.ApiError
import ru.netology.diplom.error.NetworkError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallRepositoryImpl @Inject constructor(
    private val wallDao: WallDao,
    private val apiService: ApiService,
    private val appAuth: AppAuth,
    private val appDb: AppDb,
) : WallRepository {

    override val wallData: LiveData<List<Wall>> = wallDao.get()
        .map(List<WallEntity>::toDto)

    override suspend fun getMyWall() {
        try {
            val wallResponse = apiService.getMyWall()
            if (!wallResponse.isSuccessful) {
                throw ApiError(wallResponse.code(), wallResponse.message())
            }
            val walls = wallResponse.body().orEmpty()
            wallDao.removeAll()
            wallDao.insert(walls.map(WallEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun getWallByAuthorId(id: Long) {
        try {
            val wallResponse = apiService.getWallById(id)
            if (!wallResponse.isSuccessful) {
                throw ApiError(wallResponse.code(), wallResponse.message())
            }
            val walls = wallResponse.body().orEmpty()
            wallDao.removeAll()
            wallDao.insert(walls.map(WallEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun removeWallPostDao(id: Long) {
        try {
            wallDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun removeDb() {
        try {
            wallDao.removeAll()
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun likeByIdWall(id: Long) {
        try {
            wallDao.likeById(id, appAuth.getAuthId())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun unlikeByIdWall(id: Long) {
        try {
            wallDao.unLikeById(id, appAuth.getAuthId())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

}