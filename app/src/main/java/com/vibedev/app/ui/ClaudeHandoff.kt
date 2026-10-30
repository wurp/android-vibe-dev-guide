package com.vibedev.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vibedev.app.links.Links
import com.vibedev.app.model.GuideSteps
import com.vibedev.app.model.StartedApp

/** Repository name, message to copy, and a button that hands off to the Claude app. */
@Composable
fun ClaudeHandoff(app: StartedApp, links: Links) {
    val message = GuideSteps.claudeMessage(app)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Your repository", style = MaterialTheme.typography.titleMedium)
        Text(
            app.repo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Message for Claude", style = MaterialTheme.typography.titleMedium)
                Text("\"$message\"", style = MaterialTheme.typography.bodyLarge)
                OutlinedButton(onClick = { links.copy(message) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Copy message", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        Bullets(GuideSteps.startInClaudeBullets(app))
        BigButton("Open Claude", { links.openInApp(GuideSteps.CLAUDE_CODE_URL) })
    }
}
