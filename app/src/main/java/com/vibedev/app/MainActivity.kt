package com.vibedev.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vibedev.app.data.GuideState
import com.vibedev.app.data.PrefsGuideStore
import com.vibedev.app.ui.AppNavigation
import com.vibedev.app.ui.theme.VibeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val state = GuideState(PrefsGuideStore(applicationContext))
        setContent {
            VibeAppTheme {
                AppNavigation(state)
                CrashReportDialog()
            }
        }
    }
}
