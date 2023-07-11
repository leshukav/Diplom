package ru.netology.diplom.repositry.event

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.dto.*
import ru.netology.diplom.model.MediaModel

interface EventRepository {
    val data: Flow<PagingData<Event>>

    val userData: LiveData<User>

    suspend fun getEvent()

    suspend fun likeById(id: Long)

    suspend fun unlikeById(id: Long)

    suspend fun cancelLike(id: Long)

    suspend fun removeEventById(id: Long)

    suspend fun save(event: EventCreate)

    suspend fun saveWithAttachment(event: EventCreate, media: MediaModel)

    suspend fun upload(upload: MediaModel): Media

}