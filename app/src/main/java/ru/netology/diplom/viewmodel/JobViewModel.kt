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
import ru.netology.diplom.model.JobModelState
import ru.netology.diplom.repositry.job.JobRepository
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class JobViewModel @Inject constructor(
    private val repository: JobRepository,
    private val appAuth: AppAuth,
) : ViewModel() {
    private val empty = Job(
        id = appAuth.getAuthId(),
        name = "",
        position = "position",
        start = LocalDateTime.now().toString(),
        finish = LocalDateTime.now().toString()
    )

    private val jobCreate = MutableLiveData(empty)

    val jobData: LiveData<List<Job>> = repository.jobData

    private val _state = MutableLiveData(JobModelState())
    val state: LiveData<JobModelState>
        get() = _state

    fun loadMyJob() {
        _state.value = JobModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getMyJob()
                _state.value = JobModelState()
            } catch (e: Exception) {
                _state.value = JobModelState(loadError = true)
            }
        }
    }

    fun loadJobById(id: Long) {
        _state.value = JobModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getUserJobById(id)
                _state.value = JobModelState()
            } catch (e: Exception) {
                _state.value = JobModelState(loadError = true)
            }
        }
    }

    fun removeMyJobById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeMyJobById(id)
                _state.value = JobModelState()
            } catch (e: Exception) {
                _state.value = JobModelState(removeError = true)
            }
        }
    }

    fun saveJob(nameJob: String, linkJob: String) {
        val text = nameJob.trim()
        val job = jobCreate.value
        if (job != null) {
            viewModelScope.launch {
                try {
                    repository.save(job = job.copy(name = text, link = linkJob))
                    _state.value = JobModelState()
                    jobCreate.value = empty
                } catch (_: Exception) {
                    _state.value = JobModelState(saveError = true)
                }
            }
        }
    }
}