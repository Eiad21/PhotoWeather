package com.tantawy.eiad.photoweather

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDao {

    @Query("SELECT * from image_table ORDER BY id DESC")
    fun getSortedImages(): LiveData<List<CapturedImage>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(image: CapturedImage)

    @Query("DELETE FROM image_table")
    suspend fun deleteAll()
}