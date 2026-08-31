package com.example.capacita_projeto_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.capacita_projeto_final.core.AppContainer
import com.example.capacita_projeto_final.ui.CapacitaApp
import com.example.capacita_projeto_final.ui.theme.CapacitaTheme

class MainActivity : ComponentActivity() {
    private val appContainer by lazy { AppContainer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CapacitaTheme {
                CapacitaApp(appContainer)
            }
        }
    }
}
