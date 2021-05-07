package com.example.forgiphyapp.workManager

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.forgiphyapp.MainActivity
import com.example.forgiphyapp.database.GifDatabaseDao
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class ClearDbWork(private val appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters), KoinComponent {


    val database: GifDatabaseDao by inject()
    private val notification: Notification by inject()


    override fun doWork(): Result {

        //val allGif = database.getAllGifData()
        Log.i("ClearDbWork", "Work start")
        //allGif.let { database.deleteAllGif(it) }

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