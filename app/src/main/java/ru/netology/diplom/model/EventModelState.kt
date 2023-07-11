package ru.netology.diplom.model

class EventModelState(
    val loading: Boolean = false,
    val loadError: Boolean = loading,
    val error: Boolean = false,
    val refreshing: Boolean = false,
    val likeError: Boolean = false,
    val removeError: Boolean = false,
    val addServer: Boolean = true,
)