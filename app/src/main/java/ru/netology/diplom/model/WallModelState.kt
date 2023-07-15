package ru.netology.diplom.model

data class WallModelState (
    val loading: Boolean = false,
    val loadError: Boolean = false,
    val likeError: Boolean = false,
    val removeError: Boolean = false,
)