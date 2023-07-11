package ru.netology.diplom.repositry.event

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.diplom.api.ApiService
import ru.netology.diplom.dao.event.EventDao
import ru.netology.diplom.dao.event.EventRemoteKeyDao
import ru.netology.diplom.db.AppDb
import ru.netology.diplom.entity.EventEntity
import ru.netology.diplom.entity.EventRemoteKeyEntity
import ru.netology.diplom.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val apiService: ApiService,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
    private val appDb: AppDb,
    private val eventDao: EventDao,
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): RemoteMediator.MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
               //    val id = eventRemoteKeyDao.max() ?: return RemoteMediator.MediatorResult.Success(true)
                        apiService.getLatestEvent(state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    return RemoteMediator.MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return RemoteMediator.MediatorResult.Success(false)
                    apiService.getEventBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            val eventBody = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )
            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (eventDao.isEmpty()) {
                            eventRemoteKeyDao.insert(
                                listOf(
                                    EventRemoteKeyEntity(
                                        EventRemoteKeyEntity.KeyType.AFTER,
                                        eventBody.first().id
                                    ),
                                    EventRemoteKeyEntity(
                                        EventRemoteKeyEntity.KeyType.BEFORE,
                                        eventBody.last().id
                                    ),
                                )
                            )
                        } else {
                            eventRemoteKeyDao.insert(
                                EventRemoteKeyEntity(
                                    EventRemoteKeyEntity.KeyType.AFTER,
                                    eventBody.first().id
                                ),
                            )
                        }
                    }
                    LoadType.PREPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.AFTER,
                                eventBody.first().id
                            ),
                        )
                    }
                    LoadType.APPEND -> {
                        eventDao.insert(eventBody.map(EventEntity::fromDto))
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.AFTER,
                                eventBody.last().id
                            ),
                        )
                    }
                }
            }
            eventDao.insert(eventBody.map(EventEntity::fromDto))

            return RemoteMediator.MediatorResult.Success(eventBody.isEmpty())
        } catch (e: IOException) {
            return RemoteMediator.MediatorResult.Error(e)
        }
    }
}