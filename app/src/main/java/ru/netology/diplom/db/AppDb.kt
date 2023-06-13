package ru.netology.diplom.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.diplom.dao.EventDao
import ru.netology.diplom.dao.PostDao
import ru.netology.diplom.dao.PostRemoteKeyDao
import ru.netology.diplom.dao.WallDao
import ru.netology.diplom.entity.EventEntity
import ru.netology.diplom.entity.PostEntity
import ru.netology.diplom.entity.PostRemoteKeyEntity
import ru.netology.diplom.entity.WallEntity
import ru.netology.diplom.utils.ListConverter

@TypeConverters(ListConverter::class)
@Database(entities = [PostEntity::class, EventEntity::class,WallEntity::class ,PostRemoteKeyEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun wallDao(): WallDao
}