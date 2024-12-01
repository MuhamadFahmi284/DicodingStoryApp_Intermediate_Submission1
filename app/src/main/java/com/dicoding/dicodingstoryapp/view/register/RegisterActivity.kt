package com.dicoding.dicodingstoryapp.view.register

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
import com.dicoding.dicodingstoryapp.databinding.ActivityRegisterBinding
import com.dicoding.dicodingstoryapp.view.MainViewModel
import com.dicoding.dicodingstoryapp.view.ViewModelFactory
import com.dicoding.dicodingstoryapp.view.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.getValue

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()
        observeView()
        registerAction()

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

        val title =
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val name =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEdit =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f)
                .setDuration(100)
        val email =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEdit =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f)
                .setDuration(100)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f)
                .setDuration(100)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f)
                .setDuration(100)
        val signupButton =
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                name,
                nameEdit,
                email,
                emailEdit,
                password,
                passwordEdit,
                signupButton
            )
            start()
        }
    }

    private fun observeView() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    showLoading(false)
                    showDialog(
                        this,
                        "Register berhasil",
                        result.data.message.toString(),
                        "Lanjut",
                        true
                    )
                }

                is ResultState.Error -> {
                    showLoading(false)
                    val errorMessage = if (result.error.contains("Email is already taken")) {
                        "Email sudah terdaftar. Silakan gunakan email lain atau login."
                    } else {
                        "Registrasi gagal: ${result.error}"
                    }
                    showDialog(this, "Register gagal", errorMessage, "Coba Lagi", false)
                }

                is ResultState.Loading -> {
                    showLoading(true)
                }

            }
        }
    }

    private fun registerAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            viewModel.register(name, email, password)
        }
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showDialog(
        context: Context,
        title: String,
        message: String,
        setOnClick: String,
        moveToLogin: Boolean = false
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(setOnClick) { dialog, which ->
                if (moveToLogin) {
                    moveToLogin()
                } else {
                    dialog.dismiss()
                }
            }
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}