package ru.netology.diplom.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.model.AuthModel
import ru.netology.diplom.model.AuthModelState
import ru.netology.diplom.repositry.PostRepository
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
                repository.registration(login, pass, name)
                _state.value = AuthModelState(authorized = true)
            } catch (e: Exception) {
                _state.value = AuthModelState(errorCode = true)
            }
        }
    }
}