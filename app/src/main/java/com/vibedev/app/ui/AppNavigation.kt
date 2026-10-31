package com.vibedev.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.vibedev.app.data.GuideState

private sealed interface Screen {
    data object Home : Screen
    data class Guide(val appId: Long) : Screen
    data class Claude(val appId: Long) : Screen
}

/** Picks the screen. Reopening the app resumes the guide where the user left off. */
@Composable
fun AppNavigation(state: GuideState) {
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
        )
        is Screen.Guide -> state.app(s.appId)
            ?.let { GuideScreen(it, state, onHome = { go(Screen.Home) }) }
            ?: LaunchedEffect(s) { go(Screen.Home) }
        is Screen.Claude -> state.app(s.appId)
            ?.let { ContinueInClaudeScreen(it, state.githubUsername, onBack = { go(Screen.Home) }) }
            ?: LaunchedEffect(s) { go(Screen.Home) }
    }
}
