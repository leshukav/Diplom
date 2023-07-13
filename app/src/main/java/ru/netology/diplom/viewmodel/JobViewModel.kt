package ru.netology.diplom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.dto.Job
import ru.netology.diplom.repositry.job.JobRepository
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class JobViewModel @Inject constructor(
    private val repository: JobRepository,
    private val appAuth: AppAuth,
): ViewModel() {
    private val empty = Job(
        id = appAuth.getAuthId(),
        name = "",
        position = "position",
        start = "2023-07-10"
    )

    private val jobCreate = MutableLiveData(empty)

    val jobData: LiveData<List<Job>> = repository.jobData

    fun loadMyJob() {
        viewModelScope.launch {
            try {
                repository.getMyJob()
            } catch (e: Exception) {
            }
        }
    }

    fun loadJobById(id: Long) {
        viewModelScope.launch {
            try {
                repository.getUserJobById(id)
            } catch (e: Exception) { }
        }
    }

    fun removeMyJobById(id: Long){
        viewModelScope.launch {
            try {
                repository.removeMyJobById(id)
            } catch (e: Exception) { }
        }
    }

    fun savePost(nameJob: String, linkJob: String) {
        val text = nameJob.trim()
        val job = jobCreate.value
        if (job != null) {
            viewModelScope.launch {
                try {
                    repository.save(job = job.copy(name = text, link = linkJob))
                    jobCreate.value = empty
                } catch (_: Exception) {

                }
            }
        }
    }
}