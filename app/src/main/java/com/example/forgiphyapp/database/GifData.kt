package com.example.forgiphyapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gif_data")
data class GifData(

    @PrimaryKey(autoGenerate = false)
    var id: String,
    @ColumnInfo(name = "full_url")
    var full_url: String?,
    @ColumnInfo(name = "preview_url")
    var preview_url: String?,
    @ColumnInfo(name = "active")
    var active: Boolean
)
