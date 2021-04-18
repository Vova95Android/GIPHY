package com.example.forgiphyapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gif_data")
data class GifData(

    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo(name = "full_url")
    val full_url: String?,
    @ColumnInfo(name = "preview_url")
    val preview_url: String?,
    @ColumnInfo(name = "active")
    var active: Boolean
)
