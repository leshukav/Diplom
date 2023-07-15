package ru.netology.diplom.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.netology.diplom.dto.*

@Entity(tableName = "EventEntity")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val datetime: String,
    val published: String,
    @Embedded
    var coords: Coordinates? = null,
    @SerializedName("type")
    val types: Type,
    var likeOwnerIds: List<Long> = emptyList(),
    val likeByMe: Boolean,
    var speakerIds: List<Long> = emptyList(),
    var participantsIds: List<Long> = emptyList(),
    @Embedded
    var attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean,
    @Embedded
    var users: UserPreview? = null,
) {
    fun toDto() = Event(
        id,
        authorId,
        author,
        authorAvatar,
        authorJob,
        content,
        datetime,
        published,
        coords,
        types,
        likeOwnerIds,
        likeByMe,
        speakerIds,
        participantsIds,
        attachment,
        link,
        ownedByMe,
        users
    )

    companion object {
        fun fromDto(dto: Event) =
            EventEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.authorJob,
                dto.content,
                dto.datetime,
                dto.published,
                dto.coords,
                dto.types,
                dto.likeOwnerIds,
                dto.likeByMe,
                dto.speakerIds,
                dto.participantsIds,
                dto.attachment,
                dto.link,
                dto.ownedByMe,
                dto.users
            )

    }
}

fun List<EventEntity>.toDto(): List<Event> = map(EventEntity::toDto)
fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity.Companion::fromDto)