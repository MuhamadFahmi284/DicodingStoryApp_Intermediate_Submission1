package com.dicoding.dicodingstoryapp.view.detail

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.dicodingstoryapp.data.ResultState
import com.dicoding.dicodingstoryapp.data.api.response.Story
import com.dicoding.dicodingstoryapp.databinding.ActivityDetailBinding
import com.dicoding.dicodingstoryapp.view.MainViewModel
import com.dicoding.dicodingstoryapp.view.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.getValue

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        if (storyId != null) {
            viewModel.getStoryDetail(storyId)
        } else {
            showDialog(this, "Story ID is missing.")
        }

        setupView()
        observeView()

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
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

    private fun observeView() {
        viewModel.storyDetail.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {

                    val story = result.data
                    if (true) {
                        loadDetail(story)
                    }
                    showLoading(false)
                }
                is ResultState.Error -> {
                    showLoading(false)
                    showDialog(this, result.error)
                }
                is ResultState.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun loadDetail(story: Story) {
        binding.apply {
            title.text = story.name
            description.text = story.description
            Glide.with(root.context)
                .load(story.photoUrl)
                .into(image)
        }
    }

    private fun showDialog(
        context: Context,
        message: String,
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle("error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Refresh") { dialog, _ ->
                val id = intent.getStringExtra(EXTRA_STORY_ID)
                if (id != null) {
                    viewModel.getStoryDetail(id)
                }
                refreshData(id.toString())
                dialog.dismiss()
            }
            .show()
    }

    private fun refreshData(id: String) {
        viewModel.getStoryDetail(id)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_STORY_ID = "EXTRA_STORY_ID"
    }


}