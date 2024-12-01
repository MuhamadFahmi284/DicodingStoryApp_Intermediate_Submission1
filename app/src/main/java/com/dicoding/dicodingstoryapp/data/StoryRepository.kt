package com.dicoding.dicodingstoryapp.data

import com.dicoding.dicodingstoryapp.data.api.response.FileUploadResponse
import com.dicoding.dicodingstoryapp.data.api.response.LoginResponse
import com.dicoding.dicodingstoryapp.data.api.response.RegisterResponse
import com.dicoding.dicodingstoryapp.data.api.response.StoryDetailResponse
import com.dicoding.dicodingstoryapp.data.api.response.StoryResponse
import com.dicoding.dicodingstoryapp.data.api.retrofit.ApiService
import com.dicoding.dicodingstoryapp.data.pref.UserModel
import com.dicoding.dicodingstoryapp.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val userPreference: UserPreference,
    val apiService: ApiService
) {
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun uploadStory(image: MultipartBody.Part, description: RequestBody): FileUploadResponse {
        return apiService.uploadStory(image, description)
    }

    suspend fun getStories(): StoryResponse {
        return apiService.getStories()
    }

    suspend fun getStoryDetail(id: String): StoryDetailResponse {
        return apiService.getStoryDetail(id)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(userPreference, apiService)
            }.also { instance = it }

        fun clearInstance() {
            instance = null
        }
    }
}