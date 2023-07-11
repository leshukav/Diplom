package ru.netology.diplom.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.diplom.dao.event.EventDao
import ru.netology.diplom.dao.event.EventRemoteKeyDao
import ru.netology.diplom.dao.post.PostDao
import ru.netology.diplom.dao.post.PostRemoteKeyDao
import ru.netology.diplom.dao.post.WallDao
import ru.netology.diplom.entity.*
import ru.netology.diplom.utils.ListConverter

@TypeConverters(ListConverter::class)
@Database(entities = [PostEntity::class, EventEntity::class, PostRemoteKeyEntity::class, EventRemoteKeyEntity::class, WallEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun wallDao(): WallDao
}