package ru.netology.diplom.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.dto.*
import ru.netology.diplom.model.EventModelState
import ru.netology.diplom.model.MediaModel
import ru.netology.diplom.repositry.event.EventRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class EventViewModel@Inject constructor(
    private val repository: EventRepository,
    private val appAuth: AppAuth,
) : ViewModel() {
    private val empty = EventCreate(
        id = appAuth.getAuthId(),
        content = "",
        datetime = "2023-07-10",
        coords = Coordinates("10.000000", "10.000000"),
        types = Type.ONLINE,
        link = "www.hi.ru",
        speakerIds = listOf(appAuth.getAuthId())
    )
    private val _media = MutableLiveData<MediaModel?>(null)
    val media: MutableLiveData<MediaModel?>
        get() = _media

    private val _state = MutableLiveData(EventModelState())
    val state: LiveData<EventModelState>
        get() = _state

    val edited = MutableLiveData(empty)

    val data: Flow<PagingData<Event>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { events ->
                    events.map { it.copy(ownedByMe = it.authorId == myId) }
                }
        }.flowOn(Dispatchers.Default)

    init {
        loadEvents()
    }

    fun loadEvents() {
        _state.value = EventModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getEvent()
                _state.value = EventModelState()
            } catch (e: Exception) {
                _state.value = EventModelState(error = true)
            }
        }
    }

    fun removeEventById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeEventById(id)
                //     _state.value = FeedModelState(removeError = false)
            } catch (e: Exception) {
            }
        }
    }

    fun saveEvent(content: String) {
        val text = content.trim()
        val event = edited.value
        if (event != null) {
            viewModelScope.launch {
                try {
                    when (val media = media.value) {
                        null -> {
                            repository.save(event = event.copy(content = text))
                        }
                        else -> {
                            repository.saveWithAttachment(event = event.copy(content = text), media)
                        }
                    }
                    edited.value = empty
                    clearMedia()

                } catch (_: Exception) {

                }
            }
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeById(id)
                //  _state.value = FeedModelState(likeError = false)
            } catch (e: Exception) {
                repository.cancelLike(id)
                //    _state.value = FeedModelState(likeError = true)

            }
        }
    }

    fun unlikeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.unlikeById(id)
                //          _state.value = FeedModelState(likeError = false)
            } catch (e: Exception) {
                repository.cancelLike(id)
                //         _state.value = FeedModelState(likeError = true)

            }
        }
    }

    fun changeMedia(uri: Uri, file: File, type: TypeAttachment) {
        clearMedia()
        _media.value = MediaModel(uri, file, type)
    }

    fun clearMedia() {
        _media.value = null
    }

}