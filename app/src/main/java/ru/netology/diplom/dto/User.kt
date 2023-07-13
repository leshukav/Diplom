package ru.netology.diplom.dto

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    override val idUser: Long,
    val login: String,
    val name: String,
    val avatar: String? = null,
): Users
