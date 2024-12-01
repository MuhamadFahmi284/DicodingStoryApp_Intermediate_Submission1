package com.dicoding.dicodingstoryapp.view.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingstoryapp.R
import com.dicoding.dicodingstoryapp.data.ResultState
import com.dicoding.dicodingstoryapp.data.StoryRepository
import com.dicoding.dicodingstoryapp.data.api.response.StoryResponse
import com.dicoding.dicodingstoryapp.databinding.ActivityMainBinding
import com.dicoding.dicodingstoryapp.view.MainAdapter
import com.dicoding.dicodingstoryapp.view.MainViewModel
import com.dicoding.dicodingstoryapp.view.ViewModelFactory
import com.dicoding.dicodingstoryapp.view.add.AddStoryActivity
import com.dicoding.dicodingstoryapp.view.welcome.WelcomeActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: MainAdapter

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MainAdapter()

        setupView(this)
        observeSession()
        observeView()

        binding.apply {
            fabAdd.setOnClickListener { addStory() }
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_locale -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        true
                    }

                    R.id.action_logout -> {
                        viewModel.logout()
                        StoryRepository.clearInstance()
                        ViewModelFactory.clearInstance()
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        }

    }

    private fun setupView(context: Context) {
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

        binding.apply {
            rvStories.layoutManager = LinearLayoutManager(context)
            rvStories.adapter = adapter
        }
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun observeView() {
        viewModel.getStories()

        viewModel.stories.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    setStories(result.data)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    showDialog(
                        this,
                        result.error,
                    )
                }
            }
        }
    }

    private fun setStories(stories: StoryResponse) {
        adapter.submitList(stories.listStory)
    }

    private fun addStory() {
        startActivity(Intent(this, AddStoryActivity::class.java))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showDialog(
        context: Context,
        message: String,
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Refresh") { dialog, _ ->
                viewModel.getStories()
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories()
    }

}