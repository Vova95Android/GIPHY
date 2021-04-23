package com.example.forgiphyapp.workManager

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.forgiphyapp.App
import com.example.forgiphyapp.MainActivity
import com.example.forgiphyapp.database.GifDatabaseDao
import javax.inject.Inject

class ClearDbWork(private val appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {

    @Inject
    lateinit var database: GifDatabaseDao

    @Inject
    lateinit var notification: Notification

    override fun doWork(): Result {
        (appContext as App).component.inject(this)

        val allGif = database.getAllGifData()
        Log.i("ClearDbWork", "Work start")
        allGif.let { database.deleteAllGif(it) }

        notification.showNotification(
            "Clear database",
            "Database is clear",
            Intent(appContext, MainActivity::class.java),
            "Clear database",
            555
        )
        return Result.success()
    }
}