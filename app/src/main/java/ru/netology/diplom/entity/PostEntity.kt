package ru.netology.diplom.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diplom.dto.Attachment
import ru.netology.diplom.dto.Coordinates
import ru.netology.diplom.dto.Post
import ru.netology.diplom.dto.UserPreview

@Entity(tableName = "post")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val published: String,
    @Embedded
    var coords: Coordinates? = null,
    val link: String? = null,
    val likeOwnerIds: List<Long> = emptyList(),
    val mentionIds: List<Long> = emptyList(),
    val mentionedMe: Boolean,
    val likeByMe: Boolean,
    @Embedded
    var attachment: Attachment? = null,
    val ownedByMe: Boolean,
    @Embedded
    var users: UserPreview? = null,
) {
    fun toDto() = Post(
        id,
        authorId,
        authorAvatar,
        authorJob,
        content,
        published,
        coords,
        link,
        likeOwnerIds,
        mentionIds,
        mentionedMe,
        likeByMe,
        attachment,
        ownedByMe,
        users
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.authorId,
                dto.authorAvatar,
                dto.authorJob,
                dto.content,
                dto.published,
                dto.coords,
                dto.link,
                dto.likeOwnerIds,
                dto.mentionIds,
                dto.mentionedMe,
                dto.likeByMe,
                dto.attachment,
                dto.ownedByMe,
                dto.users
            )

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)