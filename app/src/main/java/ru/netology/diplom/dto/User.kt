package ru.netology.diplom.dto

data class User(
    override val idUser: Long,
    val login: String,
    val name: String,
    val avatar: String? = null,
): Users
