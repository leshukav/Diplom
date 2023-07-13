package ru.netology.diplom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.dto.User
import ru.netology.diplom.repositry.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel@Inject constructor(
    private val repository: UserRepository,
    private val appAuth: AppAuth,
): ViewModel() {

    val userData: LiveData<List<User>> = repository.userData

    fun loadUsers() {
        viewModelScope.launch {
            try {
                repository.getUsers()

            } catch (e: Exception) {
            }
        }
    }
}