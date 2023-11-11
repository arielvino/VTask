package com.av.vtask.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.av.vtask.App
import com.av.vtask.DateTimePrinter
import com.av.vtask.providers.Registries
import com.av.vtask.security.Permissions
import com.av.vtask.ui.theme.VTaskTheme
import java.nio.file.WatchEvent

class AppBrowserActivity : ComponentActivity() {
    companion object {
        const val URL = "url-view"
        const val REGEX = " "
        const val TASK = "task"
        const val CUSTOM_REGISTRY = "custom_registry"
        const val BUILTIN_REGISTRY = "builtin_registry"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            VTaskTheme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val url = intent.getStringExtra(URL)
                    if (url != null) {
                        //todo: parsing:
                        val urlParams = url.split(REGEX)
                        when (urlParams[0]) {
                            //display a task:
                            TASK -> {
                                val id = urlParams[1]
                                DisplayTask(id = id)
                            }
                            BUILTIN_REGISTRY ->{
                                val registry = Registries.valueOf(urlParams[1])
                                ShowBuiltinRegistry(registry = registry)
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun DisplayTask(id: String) {
    val task = App.dataProvider.loadTask(id)
    Column {
        //title:
        Text(
            text = task!!.title,
            textAlign = TextAlign.Start,
            fontSize = TextUnit(12F, TextUnitType.Sp)
        )

        //deadline:
        Text(
            text = DateTimePrinter.printRemainingTime(task!!.deadline),
            fontSize = TextUnit(8F, TextUnitType.Sp)
        )

        //duration:
        Text(
            text = DateTimePrinter.printAbsDuration(task!!.duration),
            fontSize = TextUnit(8F, TextUnitType.Sp)
        )

        //content:
        if (task.content != null) {
            Surface {
                Text(text = task.content, fontSize = TextUnit(12F, TextUnitType.Sp))
            }
        }
    }
    /**
     * todo:
     * add to registry
     * delete
     * edit button
     */
}

@Composable
fun ShowBuiltinRegistry(registry: Registries) {
    val ids = App.dataProvider.loadBuiltInRegistryIDs(registry)
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        for(id in ids){
            val task = App.dataProvider.loadTask(id)
            Column(
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp)
            ){
                //title:
                Text(
                    text = task!!.title,
                    textAlign = TextAlign.Start,
                    fontSize = TextUnit(18F, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                )

                //deadline:
                Text(
                    text = DateTimePrinter.printRemainingTime(task!!.deadline),
                    fontSize = TextUnit(12F, TextUnitType.Sp)
                )

                //duration:
                Text(
                    text = DateTimePrinter.printAbsDuration(task!!.duration),
                    fontSize = TextUnit(12F, TextUnitType.Sp)
                )
            }
        }
    }

}