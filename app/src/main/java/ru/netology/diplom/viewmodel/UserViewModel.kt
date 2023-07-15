package ru.netology.diplom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.diplom.dto.User
import ru.netology.diplom.model.UserModelState
import ru.netology.diplom.repositry.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {

    val userData: LiveData<List<User>> = repository.userData

    val user: LiveData<User> = repository.user

    private val _state = MutableLiveData(UserModelState())
    val state: LiveData<UserModelState>
        get() = _state

    init {
        loadUsers()
    }

    fun loadUsers() {
        _state.value = UserModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getUsers()
                _state.value = UserModelState()
            } catch (e: Exception) {
                _state.value = UserModelState(loadError = true)
            }
        }
    }

    fun loadUserData(id: Long) {
        _state.value = UserModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getUserById(id)
                _state.value = UserModelState()
            } catch (e: Exception) {
                _state.value = UserModelState(loadError = true)
            }
        }
    }
}