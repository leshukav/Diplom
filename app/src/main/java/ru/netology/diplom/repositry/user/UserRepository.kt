package ru.netology.diplom.repositry.user

import androidx.lifecycle.LiveData
import ru.netology.diplom.dto.User

interface UserRepository {

    val userData: LiveData<List<User>>

    suspend fun getUsers()
}