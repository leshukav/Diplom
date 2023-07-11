package ru.netology.diplom.repositry.auth

import ru.netology.diplom.model.MediaModel

interface AuthRepository {

    suspend fun authorization(login: String, password: String)

    suspend fun registration(login: String, password: String,name: String)

    suspend fun registrationWithAvatar(login: String, passwodr: String, name: String, media: MediaModel)

}