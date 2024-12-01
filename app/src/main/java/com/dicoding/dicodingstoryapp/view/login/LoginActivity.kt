package com.dicoding.dicodingstoryapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.dicodingstoryapp.data.ResultState
import com.dicoding.dicodingstoryapp.data.StoryRepository
import com.dicoding.dicodingstoryapp.databinding.ActivityLoginBinding
import com.dicoding.dicodingstoryapp.view.MainViewModel
import com.dicoding.dicodingstoryapp.view.ViewModelFactory
import com.dicoding.dicodingstoryapp.view.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()
        observeView()
        loginAction()

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEdit =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val button = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title, message, email, emailEdit, password, passwordEdit, button)
            start()
        }
    }

    private fun observeView() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    StoryRepository.clearInstance()
                    ViewModelFactory.clearInstance()
                    showLoading(false)
                    showDialog(
                        this,
                        "Login berhasil",
                        "Anda berhasil login. Sudah tidak sabar untuk berbagi cerita ya?",
                        "Lanjut",
                        true
                    )
                }

                is ResultState.Error -> {
                    showLoading(false)
                    val errorMessage = if (result.error.contains("Invalid password")) {
                        "Password salah. Silakan coba lagi."
                    } else if (result.error.contains("User not found")) {
                        "Akun tidak ditemukan. Silakan daftar terlebih dahulu."
                    } else {
                        "Login gagal: ${result.error}"
                    }
                    showDialog(this, "Login gagal", errorMessage, "Coba Lagi", false)
                }

                is ResultState.Loading -> {
                    showLoading(true)
                }

                null -> false
            }
        }
    }

    private fun loginAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                viewModel.login(email, password)
            } else {
                showDialog(
                    this,
                    "Login gagal",
                    "Email dan password tidak boleh kosong.",
                    "Coba Lagi",
                    false
                )
            }
        }
    }

    private fun showDialog(
        context: Context,
        title: String,
        message: String,
        setOnClick: String,
        moveToMain: Boolean = false
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(setOnClick) { dialog, which ->
                if (moveToMain) {
                    moveToMain()
                } else {
                    dialog.dismiss()
                }
            }
            .show()
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}