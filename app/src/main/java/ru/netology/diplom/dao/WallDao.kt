package ru.netology.diplom.dao

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
}