package com.example.forgiphyapp.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "gif_data")
@Parcelize
data class GifData(

    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo(name = "full_url")
    val full_url: String?,
    @ColumnInfo(name = "preview_url")
    val preview_url: String?,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "like")
    val like: Boolean
) : Parcelable
