package com.vibedev.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** "What to do there" list. Always on screen, before and after the page is opened. */
@Composable
fun Bullets(items: List<String>, heading: String = "What to do there:", numbered: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(heading, style = MaterialTheme.typography.titleMedium)
            items.forEachIndexed { i, item ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (numbered) "${i + 1}." else "•", style = MaterialTheme.typography.bodyLarge)
                    Text(item, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun BigButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))
    }
}
