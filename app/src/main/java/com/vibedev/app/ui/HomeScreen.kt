package com.vibedev.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vibedev.app.BuildConfig
import com.vibedev.app.R
import com.vibedev.app.data.GuideState
import com.vibedev.app.links.Links
import com.vibedev.app.model.GuideSteps
import com.vibedev.app.model.StartedApp
import com.vibedev.app.ui.components.AppIcon
import com.vibedev.app.ui.components.IconBadge
import com.vibedev.app.ui.components.Pill
import com.vibedev.app.ui.components.PrimaryButton
import com.vibedev.app.ui.components.StepProgress
import com.vibedev.app.ui.components.stepIcon

@Composable
fun HomeScreen(
    state: GuideState,
    onStartNew: () -> Unit,
    onContinueGuide: (StartedApp) -> Unit,
    onContinueInClaude: (StartedApp) -> Unit,
) {
    val links = Links(LocalContext.current)
    var askUsernameFor by remember { mutableStateOf<StartedApp?>(null) }

    fun download(app: StartedApp) {
        if (state.githubUsername.isBlank()) askUsernameFor = app
        else links.openInChrome(GuideSteps.downloadUrl(state.githubUsername, app))
    }

    Scaffold(
        floatingActionButton = {
            if (state.apps.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onStartNew,
                    icon = { AppIcon(R.drawable.ic_add, tint = LocalContentColor.current) },
                    text = { Text("Start another app") },
                )
            }
        },
    ) { padding ->
        if (state.apps.isEmpty()) {
            EmptyHome(onStartNew, Modifier.padding(padding))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 112.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding),
            ) {
                item {
                    Column {
                        Text("My apps", style = MaterialTheme.typography.displaySmall)
                        Text(
                            "Pick up where you left off.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                items(state.apps, key = { it.id }) { app ->
                    AppCard(
                        app = app,
                        onContinueGuide = { onContinueGuide(app) },
                        onContinueInClaude = { onContinueInClaude(app) },
                        onDownload = { download(app) },
                    )
                }
                item { VersionLabel() }
            }
        }
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
private fun EmptyHome(onStartNew: () -> Unit, modifier: Modifier) {
    Column(
        modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconBadge(R.drawable.ic_rocket_launch, size = 112.dp)
        Text("Make your own Android app", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Text(
            "This guide walks you through it with Claude, one simple step at a time. No coding needed.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        PrimaryButton(
            "Start my first app",
            onStartNew,
            trailingIcon = R.drawable.ic_arrow_forward,
            modifier = Modifier.fillMaxWidth(),
        )
        VersionLabel()
    }
}

@Composable
private fun VersionLabel() {
    Text(
        "Version ${BuildConfig.VERSION_NAME}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun AppCard(
    app: StartedApp,
    onContinueGuide: () -> Unit,
    onContinueInClaude: () -> Unit,
    onDownload: () -> Unit,
) {
    val finished = app.step > GuideSteps.COUNT
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(stepIcon(app.step), size = 52.dp)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(app.name.ifBlank { "New app" }, style = MaterialTheme.typography.titleLarge)
                    if (app.isNamed) Pill(app.repo, icon = R.drawable.ic_folder)
                }
            }
            StepProgress(minOf(app.step, GuideSteps.COUNT), GuideSteps.COUNT, height = 6.dp)
            Text(
                if (finished) "All steps done" else "Step ${app.step} of ${GuideSteps.COUNT}: ${GuideSteps.title(app.step)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FilledTonalButton(onClick = onContinueGuide, modifier = Modifier.fillMaxWidth()) {
                Text(if (finished) "Open the guide" else "Continue the guide")
            }
            if (app.isNamed) {
                Column {
                    CardAction("Continue in Claude", R.drawable.ic_forum, onContinueInClaude)
                    CardAction("Download the latest version", R.drawable.ic_download, onDownload)
                }
            }
        }
    }
}

@Composable
private fun CardAction(text: String, icon: Int, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        AppIcon(icon, Modifier.size(22.dp), tint = LocalContentColor.current)
        Spacer(Modifier.width(10.dp))
        Text(text, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun UsernameDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { AppIcon(R.drawable.ic_download, tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Your GitHub username") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("We need it once to find your app's download link.", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text("Username") },
                    shape = MaterialTheme.shapes.medium,
                )
            }
        },
        confirmButton = { TextButton(onClick = { onSave(name) }, enabled = name.isNotBlank()) { Text("Download") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
