package ru.netology.diplom.dao.wall

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.diplom.entity.WallEntity

@Dao
interface WallDao {

    @Query("SELECT * FROM WallEntity ORDER BY id DESC")
    fun get(): LiveData<List<WallEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wall: WallEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(walls: List<WallEntity>)

    @Query("DELETE FROM WallEntity")
    suspend fun removeAll()

    @Query("DELETE FROM WallEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("SELECT * FROM WallEntity WHERE id = :id")
    suspend fun getWall(id: Long): WallEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(wall: WallEntity)

    suspend fun likeById(id: Long, userId: Long) {
        val wall = getWall(id)
        val likeUser = wall.likeOwnerIds.toMutableList()
        likeUser.add(userId)
        save(wall.copy(likeOwnerIds = likeUser))
    }

    suspend fun unLikeById(id: Long, userId: Long) {
        val wall = getWall(id)
        val likeUser = wall.likeOwnerIds.toMutableList()
        likeUser.remove(userId)
        save(wall.copy(likeOwnerIds = likeUser))
    }
}