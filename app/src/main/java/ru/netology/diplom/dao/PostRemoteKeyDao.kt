package ru.netology.diplom.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.diplom.entity.PostRemoteKeyEntity

@Dao
interface PostRemoteKeyDao {

    @Query("SELECT max(`key`) FROM PostRemoteKeyEntity")
    suspend fun max(): Long?

    @Query("SELECT min(`key`) FROM PostRemoteKeyEntity")
    suspend fun min(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoteKeyEntity: PostRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoteKeyEntity: List<PostRemoteKeyEntity>)

    @Query("DELETE FROM PostRemoteKeyEntity")
    suspend fun clear()

    @Query("DELETE FROM PostRemoteKeyEntity")
    suspend fun removeAll()

}