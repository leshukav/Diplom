package ru.netology.diplom.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.diplom.dao.event.EventDao
import ru.netology.diplom.dao.event.EventRemoteKeyDao
import ru.netology.diplom.dao.job.JobDao
import ru.netology.diplom.dao.post.PostDao
import ru.netology.diplom.dao.post.PostRemoteKeyDao
import ru.netology.diplom.dao.wall.WallDao
import ru.netology.diplom.dao.user.UserDao
import ru.netology.diplom.entity.*
import ru.netology.diplom.entity.EventEntity
import ru.netology.diplom.entity.EventRemoteKeyEntity
import ru.netology.diplom.utils.ListConverter

@TypeConverters(ListConverter::class)
@Database(entities = [PostEntity::class, EventEntity::class, PostRemoteKeyEntity::class, EventRemoteKeyEntity::class, WallEntity::class, JobEntity::class, UserEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun wallDao(): WallDao
    abstract fun jobDao(): JobDao
    abstract fun userDao(): UserDao
}