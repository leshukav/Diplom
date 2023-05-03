package ru.netology.diplom.dto

data class EventCreate(
    val id: Long,
    val content: String,
    val datatime: String,
    val coords: Coordinates? = null,
    val type: Type? = null,
    val attachment: Attachment? = null,
    val link: String? = null,
    val speakerIds: List<Long> = emptyList(),
)
