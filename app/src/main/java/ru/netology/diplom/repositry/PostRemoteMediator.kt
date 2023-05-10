package ru.netology.diplom.repositry

import androidx.paging.*
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.diplom.api.ApiService
import ru.netology.diplom.dao.PostDao
import ru.netology.diplom.dao.PostRemoteKeyDao
import ru.netology.diplom.db.AppDb
import ru.netology.diplom.entity.PostEntity
import ru.netology.diplom.entity.PostRemoteKeyEntity
import ru.netology.diplom.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val responce = when (loadType) {
                LoadType.REFRESH -> {
                    val id = postRemoteKeyDao.max()
                    if (id == 0L || id == null) {
                        apiService.getLatestPosts(state.config.pageSize)
                    } else {
                        apiService.getBeforePosts(id, state.config.pageSize)

                    }
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBeforePosts(id, state.config.pageSize)
                }
            }

            if (!responce.isSuccessful) {
                throw HttpException(responce)
            }
            val body = responce.body() ?: throw ApiError(
                responce.code(),
                responce.message()
            )
            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        // postDao.insert(body.map(PostEntity::fromDto))
                        postRemoteKeyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.AFTER,
                                    body.first().id
                                ),
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id
                                ),
                            )
                        )
                    }
                    LoadType.PREPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                body.first().id
                            ),
                        )
                    }
                    LoadType.APPEND -> {
                        postDao.insert(body.map(PostEntity::fromDto))
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                body.last().id
                            ),
                        )
                    }
                }
            }
            postDao.insert(body.map(PostEntity::fromDto))

            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}