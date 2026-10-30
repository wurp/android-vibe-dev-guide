package com.vibedev.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vibedev.app.R

/** A Material Symbols icon from res/drawable. */
@Composable
fun AppIcon(@DrawableRes id: Int, modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.onSurface) {
    Icon(painterResource(id), contentDescription = null, modifier = modifier, tint = tint)
}

/** An icon in a soft tinted circle, used as a friendly visual anchor. */
@Composable
fun IconBadge(
    @DrawableRes id: Int,
    size: Dp = 64.dp,
    container: Color = MaterialTheme.colorScheme.primaryContainer,
    content: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Box(
        Modifier.size(size).clip(CircleShape).background(container),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(id, Modifier.size(size * 0.5f), tint = content)
    }
}

/** Six rounded segments: done and current filled, the rest muted. */
@Composable
fun StepProgress(current: Int, total: Int, modifier: Modifier = Modifier, height: Dp = 8.dp) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (i in 1..total) {
            val color by animateColorAsState(
                if (i <= current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                label = "segment",
            )
            Box(Modifier.weight(1f).height(height).clip(CircleShape).background(color))
        }
    }
}

/** Numbered "what to do" list. Stays on screen before and after a page is opened. */
@Composable
fun Tips(items: List<String>, heading: String = "What to do there") {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(R.drawable.ic_lightbulb, Modifier.size(22.dp), tint = MaterialTheme.colorScheme.tertiary)
                Spacer(Modifier.width(8.dp))
                Text(heading, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.tertiary)
            }
            items.forEachIndexed { i, item ->
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    NumberDot(i + 1)
                    Text(item, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun NumberDot(n: Int) {
    Box(
        Modifier.padding(top = 2.dp).size(28.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "$n",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

/** A tappable card that opens a web page or another app. */
@Composable
fun LinkCard(
    title: String,
    subtitle: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    @DrawableRes trailing: Int = R.drawable.ic_open_in_new,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            IconBadge(icon, size = 48.dp, container = MaterialTheme.colorScheme.primary, content = MaterialTheme.colorScheme.onPrimary)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
            AppIcon(trailing, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

/** Full-width main action. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes trailingIcon: Int? = null,
) {
    Button(onClick = onClick, enabled = enabled, modifier = modifier.heightIn(min = 60.dp)) {
        Text(text, style = MaterialTheme.typography.labelLarge)
        trailingIcon?.let {
            Spacer(Modifier.width(8.dp))
            AppIcon(it, Modifier.size(22.dp), tint = LocalContentColor.current)
        }
    }
}

/** A small rounded label, e.g. a repository name or a status. */
@Composable
fun Pill(
    text: String,
    @DrawableRes icon: Int? = null,
    container: Color = MaterialTheme.colorScheme.secondaryContainer,
    content: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Surface(color = container, contentColor = content, shape = CircleShape) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                AppIcon(it, Modifier.size(18.dp), tint = content)
                Spacer(Modifier.width(6.dp))
            }
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** A short side note in a soft colored box. */
@Composable
fun Note(text: String, @DrawableRes icon: Int = R.drawable.ic_lightbulb) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AppIcon(icon, Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
            Spacer(Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
