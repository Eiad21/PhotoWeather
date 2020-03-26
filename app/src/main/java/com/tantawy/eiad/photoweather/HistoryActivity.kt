package com.tantawy.eiad.photoweather

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {
    private lateinit var imageViewModel: ImageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ImageListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this,  2)

        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        imageViewModel.allImages.observe(this, Observer { images ->
            // Update the cached copy of the words in the adapter.
            images?.let { adapter.setImages(it) }
        })

    }
}
