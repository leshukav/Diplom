package ru.netology.diplom.dao.event

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.diplom.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getEvent(): Flow<List<EventEntity>>

    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getEventPaging(): PagingSource<Int, EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: List<EventEntity>)

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM EventEntity")
    suspend fun removeAll()

    @Query("SELECT COUNT(*) == 0 FROM EventEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT * FROM EventEntity WHERE id = :id")
    suspend fun getEvent(id: Long): EventEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: EventEntity)

    suspend fun likeById(id: Long, userId: Long) {
        val event = getEvent(id)
        val likeUser = event.likeOwnerIds.toMutableList()
        likeUser.add(userId)
        save(event.copy(likeOwnerIds = likeUser))
    }

    suspend fun unLikeById(id: Long, userId: Long) {
        val event = getEvent(id)
        val likeUser = event.likeOwnerIds.toMutableList()
        likeUser.remove(userId)
        save(event.copy(likeOwnerIds = likeUser))
    }
}