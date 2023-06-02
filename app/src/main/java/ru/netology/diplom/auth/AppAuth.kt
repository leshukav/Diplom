package ru.netology.diplom.auth

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.*
import ru.netology.diplom.api.ApiService
import ru.netology.diplom.model.AuthModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val TOKEN_KEY = "TOKEN_KEY"
    private val ID_KEY = "ID_KEY"

    private val _authStateFlow: MutableStateFlow<AuthModel>

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)
        if (token == null || id == 0L) {
            _authStateFlow = MutableStateFlow(AuthModel())
            prefs.edit { clear() }
        } else {
            _authStateFlow = MutableStateFlow(AuthModel(id, token))
        }
    }

    val authStateFlow: StateFlow<AuthModel> = _authStateFlow.asStateFlow()

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun apiService(): ApiService
    }

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthModel(id, token)
        prefs.edit {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
    }
    @Synchronized
    fun getAuthId() = authStateFlow.value.id


    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthModel(0L, null)
        prefs.edit { clear() }
    }


}
