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
import ru.netology.diplom.dto.Coordinates
import ru.netology.diplom.dto.Post
import ru.netology.diplom.dto.PostCreate
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


//    val newerCount: LiveData<Int> = data.switchMap {
//        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
//            .catch { e -> e.printStackTrace() }
//            .asLiveData(Dispatchers.Default)
//    }


//    private val _media = MutableLiveData<MediaModel?>(null)
    //   val media: MutableLiveData<MediaModel?>
//        get() = _media

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
                _state.value = PostModelState(error = true)
            }
        }
    }

    /*  fun loadNewer() {
          viewModelScope.launch {
              try {
                  repository.loadNewer()
                  _state.value = FeedModelState()
              } catch (e: Exception) {
                  _state.value = FeedModelState(error = true)
              }
          }
      }

      suspend fun unreadCount(): Int {
          return repository.unreadCount()
      }

      fun refreshPosts() {
          _state.value = FeedModelState(refreshing = true)
          viewModelScope.launch {
              try {
                  repository.refresh()     //getAll()
                  _state.value = FeedModelState(refreshing = false)
              } catch (e: Exception) {
                  _state.value = FeedModelState(error = true)
              }
          }
      }

      fun removeById(id: Long) {
          //    val postOld: Unit = data.collectLatest { id == id }
          //     }
          //      val postOld: Post? = data.value?.posts?.find { it.id == id }
          viewModelScope.launch {
              try {
                  repository.removeById(id)
                  _state.value = FeedModelState(removeError = false)
              } catch (e: Exception) {
                  _state.value = FeedModelState(removeError = true)
                  //              postOld?.let { repository.saveOld(it) }
              }
          }
      }
     */
    fun savePost(content: String){
        val text = content.trim()
        var post = edited.value
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
                } catch (e: Exception) {

                }
            }
        }
    }

/*
      fun changeContentAndSave(content: String) {
          val text = content.trim()
          val post = edited.value
          if (post != null) {
              viewModelScope.launch {
                  try {
                      when (val media = media.value) {
                          null -> repository.save(post = post.copy(content = text))
                          else -> {
                              repository.saveWithAttachment(post = post.copy(content = text), media)
                          }
                      }
                      _postCreated.value = Unit
                      edited.value = empty
                      clearPhoto()
                      _state.value = FeedModelState()
                      //      repository.save(post = post.copy(content = text))
                  } catch (e: Exception) {
                      _state.value = FeedModelState(error = true)
                  }
              }
          }

      }
      */

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

    fun changePhoto(uri: Uri, file: File) {
        _media.value = MediaModel(uri, file)
    }

    fun clearPhoto() {
        _media.value = null
    }

    /*

    fun shareById(id: Long) {
        viewModelScope.launch {
            try {
                repository.shareById(id)
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun cancelEdit() {
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

   */


}