package ru.netology.diplom.dto

import com.google.gson.annotations.SerializedName

data class EventCreate(
    val id: Long,
    val content: String,
    val datetime: String,
    val coords: Coordinates? = null,
    @SerializedName("type")
    val types: Type,
    val attachment: Attachment? = null,
    val link: String? = null,
    val speakerIds: List<Long> = emptyList(),
)
