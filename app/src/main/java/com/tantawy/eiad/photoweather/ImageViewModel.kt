package com.tantawy.eiad.photoweather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
class ImageViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: ImageRepository

    // LiveData gives us updated words when they change.
    val allImages: LiveData<List<CapturedImage>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val imagesDao = ImageRoomDatabase.getDatabase(application, viewModelScope).imageDao()
        repository = ImageRepository(imagesDao)
        allImages = repository.allImages
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(image: CapturedImage) = viewModelScope.launch {
        repository.insert(image)
    }
}