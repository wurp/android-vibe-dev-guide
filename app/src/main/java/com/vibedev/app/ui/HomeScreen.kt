package com.vibedev.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vibedev.app.BuildConfig
import com.vibedev.app.data.GuideState
import com.vibedev.app.links.Links
import com.vibedev.app.model.GuideSteps
import com.vibedev.app.model.StartedApp

@Composable
fun HomeScreen(
    state: GuideState,
    onStartNew: () -> Unit,
    onContinueGuide: (StartedApp) -> Unit,
    onContinueInClaude: (StartedApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val links = Links(LocalContext.current)
    var askUsernameFor by remember { mutableStateOf<StartedApp?>(null) }

    fun download(app: StartedApp) {
        if (state.githubUsername.isBlank()) askUsernameFor = app
        else links.openInChrome(GuideSteps.downloadUrl(state.githubUsername, app))
    }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("My apps", style = MaterialTheme.typography.headlineMedium)
        if (state.apps.isEmpty()) {
            Text(
                "This guide walks you through making your own Android app with Claude, one step at a time.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        BigButton(if (state.apps.isEmpty()) "Start my first app" else "Start another app", onStartNew)
        state.apps.forEach { app ->
            AppCard(
                app = app,
                onContinueGuide = { onContinueGuide(app) },
                onContinueInClaude = { onContinueInClaude(app) },
                onDownload = { download(app) },
            )
        }
        Text(
            "Version ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    askUsernameFor?.let { app ->
        UsernameDialog(
            onDismiss = { askUsernameFor = null },
            onSave = { name ->
                state.updateUsername(name)
                askUsernameFor = null
                links.openInChrome(GuideSteps.downloadUrl(state.githubUsername, app))
            },
        )
    }
}

@Composable
private fun AppCard(
    app: StartedApp,
    onContinueGuide: () -> Unit,
    onContinueInClaude: () -> Unit,
    onDownload: () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(app.name.ifBlank { "New app (no name yet)" }, style = MaterialTheme.typography.titleLarge)
            if (app.isNamed) Text("Repository: ${app.repo}", style = MaterialTheme.typography.bodyLarge)
            val progress = if (app.step > GuideSteps.COUNT) "Finished all steps"
            else "Step ${app.step} of ${GuideSteps.COUNT}: ${GuideSteps.title(app.step)}"
            Text(progress, style = MaterialTheme.typography.bodyLarge)
            OutlinedButton(onClick = onContinueGuide, modifier = Modifier.fillMaxWidth()) {
                Text(if (app.step > GuideSteps.COUNT) "Open the guide" else "Continue the guide")
            }
            if (app.isNamed) {
                OutlinedButton(onClick = onContinueInClaude, modifier = Modifier.fillMaxWidth()) {
                    Text("Continue in Claude")
                }
                OutlinedButton(onClick = onDownload, modifier = Modifier.fillMaxWidth()) {
                    Text("Download the latest version")
                }
            }
        }
    }
}

@Composable
private fun UsernameDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Your GitHub username") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("We need it once to find your app's download link.", style = MaterialTheme.typography.bodyLarge)
                OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true, label = { Text("Username") })
            }
        },
        confirmButton = { TextButton(onClick = { onSave(name) }, enabled = name.isNotBlank()) { Text("Download") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
