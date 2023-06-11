package ru.netology.diplom.model

import android.net.Uri
import ru.netology.diplom.dto.TypeAttachment
import java.io.File

data class MediaModel(val uri: Uri, val file: File, val type: TypeAttachment)