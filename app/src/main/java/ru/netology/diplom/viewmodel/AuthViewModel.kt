package ru.netology.diplom.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.model.AuthModel
import ru.netology.diplom.model.AuthModelState
import ru.netology.diplom.model.MediaModel
import ru.netology.diplom.repositry.PostRepository
import java.io.File
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(

    private val auth: AppAuth,
    private val repository: PostRepository
) : ViewModel() {
    val data: LiveData<AuthModel> = auth.authStateFlow
        .asLiveData(Dispatchers.Default)

    val authorized: Boolean
        get() = auth.authStateFlow.value.id != 0L

    private val _state = MutableLiveData(AuthModelState())
    val state: LiveData<AuthModelState>
        get() = _state

    private val _mediaAvatar = MutableLiveData<MediaModel?>(null)
    val mediaAvatar: LiveData<MediaModel?>
        get() = _mediaAvatar


    fun authorization(login: String, pass: String) {
        viewModelScope.launch {
            try {
                repository.authorization(login, pass)
                _state.value = AuthModelState(authorized = true)
            } catch (e: Exception) {
                _state.value = AuthModelState(errorCode = true)
            }

        }
    }

    fun registration(login: String, pass: String, name: String) {
        viewModelScope.launch {
            try {
                when (val media = mediaAvatar.value) {
                    null -> repository.registration(login, pass, name)
                    else -> repository.registrationWithAvatar(login, pass, name, media)
                }
            //    repository.registration(login, pass, name)
                _state.value = AuthModelState(authorized = true)
            } catch (e: Exception) {
                _state.value = AuthModelState(errorCode = true)
            }
        }
    }

    fun changeAvatar(file: File, uri: Uri) {
        _mediaAvatar.value = MediaModel(uri, file)
    }

    fun clearAvatar() {
        _mediaAvatar.value = null
    }
}