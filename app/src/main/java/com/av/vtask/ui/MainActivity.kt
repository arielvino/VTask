package com.av.vtask.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.av.vtask.App
import com.av.vtask.datastructure.Task
import com.av.vtask.providers.InternalStorageProvider
import com.av.vtask.providers.Registries
import com.av.vtask.security.CryptoFactory
import com.av.vtask.ui.theme.VTaskTheme
import java.time.Duration
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VTaskTheme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {}
            }
        }

        //one-time operations:
        assert(CryptoFactory.generateAndStoreAESKey())
        App.dataProvider = InternalStorageProvider
        App.dataProvider.initialize(arrayOf(filesDir.absolutePath))//verify there is items dictionary

        //test:
        val id = App.dataProvider.createTask(Task(title = "example title", deadline = LocalDateTime.now().plusHours(6), duration = Duration.ofMinutes(788)))
        App.dataProvider.registerItemToBuildInRegistry(id, Registries.QuickTasks)
        val intent = Intent(this, AppBrowserActivity::class.java)
        intent.putExtra(AppBrowserActivity.URL, AppBrowserActivity.BUILTIN_REGISTRY + AppBrowserActivity.REGEX + Registries.QuickTasks.name)
        startActivity(intent)
    }
}