package com.vibedev.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vibedev.app.data.GuideState
import com.vibedev.app.links.Links

private sealed interface Screen {
    data object Home : Screen
    data class Guide(val appId: Long) : Screen
    data class Claude(val appId: Long) : Screen
}

/** Picks the screen. Reopening the app resumes the guide where the user left off. */
@Composable
fun AppNavigation(state: GuideState, modifier: Modifier = Modifier) {
    var screen by remember { mutableStateOf(state.currentAppId?.let { Screen.Guide(it) } ?: Screen.Home) }
    fun go(to: Screen) {
        state.currentAppId = (to as? Screen.Guide)?.appId
        screen = to
    }
    BackHandler(enabled = screen != Screen.Home) { go(Screen.Home) }

    when (val s = screen) {
        Screen.Home -> HomeScreen(
            state = state,
            onStartNew = { go(Screen.Guide(state.startNewApp().id)) },
            onContinueGuide = { go(Screen.Guide(it.id)) },
            onContinueInClaude = { go(Screen.Claude(it.id)) },
            modifier = modifier,
        )
        is Screen.Guide -> state.app(s.appId)
            ?.let { GuideScreen(it, state, onHome = { go(Screen.Home) }, modifier = modifier) }
            ?: LaunchedEffect(s) { go(Screen.Home) }
        is Screen.Claude -> state.app(s.appId)?.let { app ->
            Column(
                modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TextButton(onClick = { go(Screen.Home) }) { Text("← My apps", style = MaterialTheme.typography.titleMedium) }
                Text("Continue in Claude", style = MaterialTheme.typography.headlineMedium)
                ClaudeHandoff(app, Links(LocalContext.current))
            }
        } ?: LaunchedEffect(s) { go(Screen.Home) }
    }
}
