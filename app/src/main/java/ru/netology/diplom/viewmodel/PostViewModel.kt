package ru.netology.diplom.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.dto.*
import ru.netology.diplom.model.MediaModel
import ru.netology.diplom.model.PostModelState
import ru.netology.diplom.repositry.post.PostRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
) : ViewModel() {
    private val empty = PostCreate(
        id = appAuth.getAuthId(),
        content = "",
        coords = Coordinates("10.000000", "10.000000"),
        link = "www.hi.ru",
        mentionIds = listOf(appAuth.getAuthId()),
    )
    private val _media = MutableLiveData<MediaModel?>(null)
    val media: MutableLiveData<MediaModel?>
        get() = _media

    private val _state = MutableLiveData(PostModelState())
    val state: LiveData<PostModelState>
        get() = _state

    private val edited = MutableLiveData(empty)

    val data: Flow<PagingData<Post>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                    posts.map { it.copy(ownedByMe = it.authorId == myId) }
                }
        }.flowOn(Dispatchers.Default)

    init {
        loadPosts()
    }

    fun loadPosts() {
        _state.value = PostModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getPosts()
                _state.value = PostModelState()
            } catch (e: Exception) {
                _state.value = PostModelState(loadError = true)
            }
        }
    }

    fun removePostById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removePostById(id)
                _state.value = PostModelState()
            } catch (e: Exception) {
                _state.value = PostModelState(removeError = true)
            }
        }
    }

    fun savePost(content: String) {
        val text = content.trim()
        val post = edited.value
        if (post != null) {
            viewModelScope.launch {
                try {
                    when (val media = media.value) {
                        null -> {
                            repository.save(post = post.copy(content = text))
                            _state.value = PostModelState()
                        }
                        else -> {
                            repository.saveWithAttachment(post = post.copy(content = text), media)
                            _state.value = PostModelState()
                        }
                    }
                    edited.value = empty
                    clearMedia()

                } catch (_: Exception) {
                    _state.value = PostModelState(saveError = true)
                }
            }
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeById(id)
                _state.value = PostModelState()
            } catch (e: Exception) {
                repository.cancelLike(id)
                _state.value = PostModelState(likeError = true)

            }
        }
    }

    fun unlikeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.unlikeById(id)
                _state.value = PostModelState()
            } catch (e: Exception) {
                repository.cancelLike(id)
                _state.value = PostModelState(likeError = true)

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