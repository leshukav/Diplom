package ru.netology.diplom.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.diplom.auth.AppAuth
import javax.inject.Inject

@HiltAndroidApp
class diplomApp() : Application() {
    private val appScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate() {
        super.onCreate()
        setupAuth()
    }

    private fun setupAuth() {
        appScope.launch {
        }
    }
}