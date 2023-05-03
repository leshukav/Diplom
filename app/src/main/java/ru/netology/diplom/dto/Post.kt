package ru.netology.diplom.dto

data class Post(
    val id: Long,
    val authorId: Long,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val published: String,
    var coords: Coordinates? = null,
    val link: String? = null,
    val likeOwnerIds: List<Long> = emptyList(),
    val mentionIds: List<Long> = emptyList(),
    val mentionedMe: Boolean,
    val likeByMe: Boolean,
    var attachment: Attachment? = null,
    val ownedByMe: Boolean,
    var users: UserPreview? = null,
)