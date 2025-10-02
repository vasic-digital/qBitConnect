package com.shareconnect.qbitconnect

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.shareconnect.qbitconnect.ui.App

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ensure dependency injection is initialized
        com.shareconnect.qbitconnect.di.DependencyContainer.init(this)

        setContent {
            App()
        }
    }
}
