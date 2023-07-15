package ru.netology.diplom.repositry.wall

import androidx.lifecycle.LiveData
import ru.netology.diplom.dto.Wall

interface WallRepository {

    val wallData: LiveData<List<Wall>>

    suspend fun getMyWall()

    suspend fun getWallByAuthorId(id: Long)

    suspend fun removeWallPostDao(id: Long)

    suspend fun likeByIdWall(id: Long)

    suspend fun unlikeByIdWall(id: Long)

}