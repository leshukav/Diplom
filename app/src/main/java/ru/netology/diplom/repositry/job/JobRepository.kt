package ru.netology.diplom.repositry.job

import androidx.lifecycle.LiveData
import ru.netology.diplom.dto.Job

interface JobRepository {

    val jobData: LiveData<List<Job>>

    suspend fun getMyJob()

    suspend fun save(job: Job)

    suspend fun removeMyJobById(id: Long)

    suspend fun getUserJobById(id: Long)



}