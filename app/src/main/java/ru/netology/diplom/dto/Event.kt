package ru.netology.diplom.dto

import com.google.gson.annotations.SerializedName

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val datetime: String,
    val published: String,
    var coords: Coordinates? = null,
    @SerializedName("type")
    val types: Type,
    var likeOwnerIds: List<Long> = emptyList(),
    val likeByMe: Boolean,
    var speakerIds: List<Long> = emptyList(),
    var participantsIds: List<Long> = emptyList(),
    var attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean,
    var users: UserPreview? = null,
)

data class Coordinates(
    var lat: String? = null,
    var long: String? = null,
)

data class Attachment(
    val url: String,
    val type: TypeAttachment,
)

enum class Type {
    OFFLINE, ONLINE
}

enum class TypeAttachment {
    IMAGE, VIDEO, AUDIO
}

data class UserPreview(
    override val idUser: Long,
    val name: String? = null,
    val avatar: String? = null,
): Users

sealed interface Users {
    val idUser: Long
}