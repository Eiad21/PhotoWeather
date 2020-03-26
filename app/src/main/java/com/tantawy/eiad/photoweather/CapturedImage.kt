package com.tantawy.eiad.photoweather

import android.R
import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_table")
class CapturedImage(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int, @ColumnInfo(name = "image") val image: String)