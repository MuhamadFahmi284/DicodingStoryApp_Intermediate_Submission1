package com.dicoding.dicodingstoryapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingstoryapp.data.ResultState
import com.dicoding.dicodingstoryapp.data.StoryRepository
import com.dicoding.dicodingstoryapp.data.api.response.FileUploadResponse
import com.dicoding.dicodingstoryapp.data.api.response.LoginResponse
import com.dicoding.dicodingstoryapp.data.api.response.RegisterResponse
import com.dicoding.dicodingstoryapp.data.api.response.Story
import com.dicoding.dicodingstoryapp.data.api.response.StoryResponse
import com.dicoding.dicodingstoryapp.data.pref.UserModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    val registerResult = MutableLiveData<ResultState<RegisterResponse>>()
    val loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val uploadResult = MutableLiveData<ResultState<FileUploadResponse>>()

    private val _stories = MutableLiveData<ResultState<StoryResponse>>()
    val stories: LiveData<ResultState<StoryResponse>> = _stories

    private val _storyDetail = MutableLiveData<ResultState<Story>>()
    val storyDetail: LiveData<ResultState<Story>> = _storyDetail

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            registerResult.value = ResultState.Loading

            try {
                val response = repository.register(name, email, password)
                registerResult.value = ResultState.Success(response)
            } catch (e: Exception) {
                val errorMessage =
                    (e as? HttpException)?.response()?.errorBody()?.string() ?: e.localizedMessage
                registerResult.value = ResultState.Error(errorMessage)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginResult.value = ResultState.Loading

            try {
                val response = repository.login(email, password)
                val name = response.loginResult?.name.toString()
                val token = response.loginResult?.token.toString()
                val userModel =
                    UserModel(name = name, email = email, token = token, isLogin = true)
                saveSession(userModel)
                loginResult.value = ResultState.Success(response)
            } catch (e: Exception) {
                val errorMessage =
                    (e as? HttpException)?.response()?.errorBody()?.string() ?: e.localizedMessage
                loginResult.value = ResultState.Error(errorMessage)
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun uploadStory(image: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            uploadResult.value = ResultState.Loading
            try {
                val response = repository.uploadStory(image, description)
                uploadResult.value = ResultState.Success(response)
            } catch (e: Exception) {
                val errorMessage =
                    (e as? HttpException)?.response()?.errorBody()?.string() ?: e.localizedMessage
                registerResult.value = ResultState.Error(errorMessage)
            }
        }
    }

    fun getStories() {
        viewModelScope.launch {
            _stories.value = ResultState.Loading
            try {
                val response = repository.getStories()
                _stories.value = ResultState.Success(response)
            } catch (e: Exception) {
                _stories.value = ResultState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun getStoryDetail(id: String) {
        viewModelScope.launch {
            _storyDetail.value = ResultState.Loading
            try {
                val response = repository.getStoryDetail(id)
                val story = response.story

                if (story != null) {
                    _storyDetail.value = ResultState.Success(story)
                }

            } catch (e: Exception) {
                _storyDetail.value = ResultState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}