package com.festadoviso.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.festadoviso.ui.navigation.FestaDoVisoNavigation
import com.festadoviso.ui.theme.FestaDoVisoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - ponto de entrada da aplicação Android.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FestaDoVisoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FestaDoVisoNavigation()
                }
            }
        }
    }
}
