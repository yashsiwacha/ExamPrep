package com.examprep.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.examprep.android.navigation.AppNavigation
import com.examprep.core.designsystem.theme.ExamPrepTheme
import com.examprep.core.designsystem.theme.ThemeState
import dagger.hilt.android.AndroidEntryPoint

/**
 * The single activity for the ExamPrep OS application.
 *
 * Hosts the Compose navigation graph. All screen transitions happen
 * within this activity via Navigation Compose.
 *
 * Architecture: No business logic here — delegate to navigation + ViewModels.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by ThemeState.isDarkMode.collectAsState()
            ExamPrepTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}
