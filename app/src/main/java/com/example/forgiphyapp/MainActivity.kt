package com.example.forgiphyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.forgiphyapp.navigation.Router
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val router: Router by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        router.setActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        router.clearActivity()
    }
}