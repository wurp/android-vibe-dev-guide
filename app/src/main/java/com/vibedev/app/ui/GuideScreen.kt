package com.vibedev.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vibedev.app.data.GuideState
import com.vibedev.app.links.Links
import com.vibedev.app.model.GuideSteps
import com.vibedev.app.model.StartedApp

@Composable
fun GuideScreen(app: StartedApp, state: GuideState, onHome: () -> Unit, modifier: Modifier = Modifier) {
    val links = Links(LocalContext.current)
    val step = app.step.coerceIn(1, GuideSteps.COUNT)
    val canGoNext = step != GuideSteps.DETAILS || app.isNamed

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = onHome) { Text("← My apps", style = MaterialTheme.typography.titleMedium) }
        Text("Step $step of ${GuideSteps.COUNT}", style = MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(progress = { step / GuideSteps.COUNT.toFloat() }, modifier = Modifier.fillMaxWidth())
        Text(GuideSteps.title(step), style = MaterialTheme.typography.headlineMedium)

        when (step) {
            GuideSteps.SETUP -> SetupStep(state, links)
            GuideSteps.DETAILS -> DetailsStep(app, state)
            GuideSteps.CREATE_REPO -> WebStep(
                intro = "Make a copy of the starter app on GitHub.",
                bullets = GuideSteps.createRepoBullets(app),
                buttonText = "Open GitHub",
                onOpen = { links.openInTab(GuideSteps.createRepoUrl(app)) },
            )
            GuideSteps.CLAUDE_ACCESS -> WebStep(
                intro = "Let Claude work on your new repository. If Claude already has all your repositories, skip this step.",
                bullets = GuideSteps.claudeAccessBullets,
                buttonText = "Open GitHub",
                onOpen = { links.openInTab(GuideSteps.CLAUDE_ACCESS_URL) },
            )
            GuideSteps.START_IN_CLAUDE -> ClaudeHandoff(app, links)
            GuideSteps.INSTALL -> Bullets(GuideSteps.installBullets, heading = "When Claude sends a download link:", numbered = true)
        }

        val last = step == GuideSteps.COUNT
        BigButton(
            text = if (last) "Done! Back to my apps" else "Done, next step",
            enabled = canGoNext,
            onClick = {
                state.save(app.copy(step = step + 1))
                if (last) onHome()
            },
        )
        if (step > GuideSteps.SETUP) {
            OutlinedButton(onClick = { state.save(app.copy(step = step - 1)) }, modifier = Modifier.fillMaxWidth()) {
                Text("Previous step", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun SetupStep(state: GuideState, links: Links) {
    Text("You only do this once. Tick what you have, or skip ahead.", style = MaterialTheme.typography.bodyLarge)
    GuideSteps.setupItems.forEach { item ->
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = state.isChecked(item.key), onCheckedChange = { state.setChecked(item.key, it) })
            Text(item.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            item.url?.let { url -> TextButton(onClick = { links.openInTab(url) }) { Text("Open") } }
        }
    }
}

@Composable
private fun DetailsStep(app: StartedApp, state: GuideState) {
    OutlinedTextField(
        value = app.name,
        onValueChange = { state.save(app.copy(name = it)) },
        label = { Text("App name") },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = app.description,
        onValueChange = { state.save(app.copy(description = it)) },
        label = { Text("One sentence: what should it do?") },
        textStyle = MaterialTheme.typography.bodyLarge,
        minLines = 2,
        modifier = Modifier.fillMaxWidth(),
    )
    if (app.isNamed) {
        Text("Repository name: ${app.repo}", style = MaterialTheme.typography.titleMedium)
    } else {
        Text("Type a name to continue.", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun WebStep(intro: String, bullets: List<String>, buttonText: String, onOpen: () -> Unit) {
    Text(intro, style = MaterialTheme.typography.bodyLarge)
    Bullets(bullets)
    BigButton(buttonText, onOpen)
    Text("Close the page when you're done to come back here.", style = MaterialTheme.typography.bodyMedium)
}
