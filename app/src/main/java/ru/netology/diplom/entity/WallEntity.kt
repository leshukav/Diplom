package ru.netology.diplom.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diplom.dto.*

@Entity(tableName = "WallEntity")
data class WallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
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
    fun toDto() = Wall(
        id,
        authorId,
        author,
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
        fun fromDto(dto: Wall) =
            WallEntity(
                dto.id,
                dto.authorId,
                dto.author,
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

fun List<WallEntity>.toDto(): List<Wall> = map(WallEntity::toDto)
fun List<Wall>.toEntity(): List<WallEntity> = map(WallEntity::fromDto)