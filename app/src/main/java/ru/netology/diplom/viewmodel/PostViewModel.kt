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
import ru.netology.diplom.repositry.PostRepository
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

    val edited = MutableLiveData(empty)

    val data: Flow<PagingData<Post>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                    posts.map { it.copy(ownedByMe = it.authorId == myId) }
                }
        }.flowOn(Dispatchers.Default)

    val wallData: LiveData<List<Wall>> = repository.wallData

    val userData: LiveData<User> = repository.userData

    init {
        loadPosts()
    }

    fun loadUserData(id: Long) {
        viewModelScope.launch {
            try {
                repository.getUserById(id)
            } catch (e: Exception) {
            }
        }
    }

    fun loadPosts() {
        _state.value = PostModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getPosts()
                _state.value = PostModelState()
            } catch (e: Exception) {
                _state.value = PostModelState(error = true)
            }
        }
    }

    fun loadWallById(id: Long) {
        viewModelScope.launch {
            try {
                repository.getWallByAuthorId(id)
            } catch (e: Exception) {
            }
        }
    }

    fun removePostById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removePostById(id)
                //     _state.value = FeedModelState(removeError = false)
            } catch (e: Exception) {
            }
        }
    }

    fun removeWallPostDao(id: Long) {
        viewModelScope.launch {
            repository.removeWallPostDao(id)
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
                        }
                        else -> {
                            repository.saveWithAttachment(post = post.copy(content = text), media)
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