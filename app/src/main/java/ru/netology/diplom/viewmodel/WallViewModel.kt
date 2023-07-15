package ru.netology.diplom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.dto.Wall
import ru.netology.diplom.model.WallModelState
import ru.netology.diplom.repositry.wall.WallRepository
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class WallViewModel @Inject constructor(
    private val repository: WallRepository,
    private val appAuth: AppAuth,
) : ViewModel() {

    private val _state = MutableLiveData(WallModelState())
    val state: LiveData<WallModelState>
        get() = _state

    val wallData: LiveData<List<Wall>> = repository.wallData

    fun loadMyWall() {
        _state.value = WallModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getMyWall()
                _state.value = WallModelState()
            } catch (e: Exception) {
                _state.value = WallModelState(loadError = true)
            }
        }
    }

    fun loadWallById(id: Long) {
        _state.value = WallModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getWallByAuthorId(id)
                _state.value = WallModelState()
            } catch (e: Exception) {
                _state.value = WallModelState(loadError = true)
            }
        }
    }

    fun removeWallPostDao(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeWallPostDao(id)
                _state.value = WallModelState()
            } catch (e: Exception) {
                _state.value = WallModelState(removeError = true)
            }

        }
    }

    fun likeByIdWall(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeByIdWall(id)
                _state.value = WallModelState()
            } catch (e: Exception) {
                _state.value = WallModelState(likeError = true)
            }
        }
    }

    fun unlikeByIdWall(id: Long) {
        viewModelScope.launch {
            try {
                repository.unlikeByIdWall(id)
                _state.value = WallModelState()
            } catch (e: Exception) {
                _state.value = WallModelState(likeError = true)

            }
        }
    }

}