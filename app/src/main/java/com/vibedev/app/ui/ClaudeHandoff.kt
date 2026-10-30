package com.vibedev.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.vibedev.app.R
import com.vibedev.app.links.Links
import com.vibedev.app.model.GuideSteps
import com.vibedev.app.model.StartedApp
import com.vibedev.app.ui.components.AppIcon
import com.vibedev.app.ui.components.LinkCard
import com.vibedev.app.ui.components.Tips

/** Repository name, message to copy, and a button that hands off to the Claude app. */
@Composable
fun ClaudeHandoff(app: StartedApp, links: Links, onOpened: () -> Unit = {}) {
    val message = GuideSteps.claudeMessage(app)
    var copied by remember(message) { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Your repository", style = MaterialTheme.typography.titleSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(R.drawable.ic_folder, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(Modifier.width(10.dp))
                    Text(app.repo, style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
        OutlinedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Message for Claude", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Text("“$message”", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic)
                FilledTonalButton(
                    onClick = { links.copy(message); copied = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    AppIcon(
                        if (copied) R.drawable.ic_check_circle else R.drawable.ic_content_copy,
                        Modifier.size(20.dp),
                        tint = LocalContentColor.current,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (copied) "Copied" else "Copy message")
                }
            }
        }
        Tips(GuideSteps.startInClaudeBullets(app))
        LinkCard(
            title = "Open Claude",
            subtitle = "Opens the Claude app",
            icon = R.drawable.ic_forum,
            onClick = {
                links.openInApp(GuideSteps.CLAUDE_CODE_URL)
                onOpened()
            },
        )
    }
}
