package ru.netology.diplom.repositry.job

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import retrofit2.HttpException
import ru.netology.diplom.api.ApiService
import ru.netology.diplom.dao.job.JobDao
import ru.netology.diplom.dto.Job
import ru.netology.diplom.entity.JobEntity
import ru.netology.diplom.entity.toDto
import ru.netology.diplom.error.ApiError
import ru.netology.diplom.error.NetworkError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepositoryImpl @Inject constructor(
    private val jobDao: JobDao,
    private val apiService: ApiService,
): JobRepository {

    override val jobData: LiveData<List<Job>> = jobDao.get()
        .map(List<JobEntity>::toDto)

    override suspend fun getUserJobById(id: Long) {
        try {
            val response = apiService.getUserJob(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val jobs = response.body().orEmpty()
            jobDao.removeAll()
            jobDao.insert(jobs.map(JobEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun save(job: Job) {
        try {
            val jobResponse = apiService.saveMyJob(job)
            if (!jobResponse.isSuccessful) {
                throw ApiError(jobResponse.code(), jobResponse.message())
            }
            val body = jobResponse.body() ?: throw HttpException(jobResponse)

            jobDao.insert(JobEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun getMyJob() {
        try {
            val jobResponse = apiService.getMyJobs()
            if (!jobResponse.isSuccessful) {
                throw ApiError(jobResponse.code(), jobResponse.message())
            }
            val jobs = jobResponse.body() ?: throw  HttpException(jobResponse)
            jobDao.removeAll()
            jobDao.insert(jobs.map(JobEntity::fromDto))
        } catch (_: Exception) {
        }
    }

    override suspend fun removeMyJobById(id: Long) {
        try {
            val jobResponse = apiService.deleteMyJob(id)
            if (!jobResponse.isSuccessful) {
                throw ApiError(jobResponse.code(), jobResponse.message())
            }
            jobDao.removeById(id)
        } catch (_: Exception) {
        }
    }

}