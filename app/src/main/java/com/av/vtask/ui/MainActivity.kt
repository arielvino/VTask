package com.av.vtask.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.av.vtask.App
import com.av.vtask.providers.InternalStorageProvider
import com.av.vtask.providers.Registries
import com.av.vtask.security.CryptoFactory
import com.av.vtask.ui.theme.VTaskTheme
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    //@OptIn(ExperimentalMaterial3Api::class)//todo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VTaskTheme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //todo test:
                    val intent = Intent(this, CreateTaskActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        //one-time operations:
        assert(CryptoFactory.generateAndStoreAESKey())
        App.dataProvider = InternalStorageProvider
        App.dataProvider.initialize(arrayOf(filesDir.absolutePath))//verify there is items dictionary

        //test:
        val intent = Intent(this, AppBrowserActivity::class.java)
        intent.putExtra(
            AppBrowserActivity.URL,
            AppBrowserActivity.BUILTIN_REGISTRY + AppBrowserActivity.REGEX + Registries.QuickTasks.name
        )
        //startActivity(intent)
    }
}