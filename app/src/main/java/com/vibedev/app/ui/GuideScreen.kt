package com.vibedev.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vibedev.app.R
import com.vibedev.app.data.GuideState
import com.vibedev.app.links.Links
import com.vibedev.app.model.GuideSteps
import com.vibedev.app.model.StartedApp
import com.vibedev.app.ui.components.AppIcon
import com.vibedev.app.ui.components.IconBadge
import com.vibedev.app.ui.components.LinkCard
import com.vibedev.app.ui.components.Note
import com.vibedev.app.ui.components.Pill
import com.vibedev.app.ui.components.PrimaryButton
import com.vibedev.app.ui.components.StepProgress
import com.vibedev.app.ui.components.Tips
import com.vibedev.app.ui.components.stepIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(app: StartedApp, state: GuideState, onHome: () -> Unit) {
    val links = Links(LocalContext.current)
    val step = app.step.coerceIn(1, GuideSteps.COUNT)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(app.name.ifBlank { "New app" }, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onHome) { AppIcon(R.drawable.ic_arrow_back) }
                },
            )
        },
        bottomBar = {
            GuideBottomBar(
                step = step,
                canGoNext = GuideSteps.canContinue(app.copy(step = step)),
                lockedHint = if (GuideSteps.needsOpening(step)) "Open the page above first." else null,
                onBack = { state.goBack(app) },
                onNext = {
                    state.goNext(app)
                    if (step == GuideSteps.COUNT) onHome()
                },
            )
        },
    ) { padding ->
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                val dir = if (targetState > initialState) 1 else -1
                (slideInHorizontally { it * dir / 4 } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it * dir / 4 } + fadeOut())
            },
            modifier = Modifier.padding(padding),
            label = "step",
        ) { shown ->
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                StepHeader(shown)
                StepBody(shown, app, state, links)
                Spacer(Modifier.size(8.dp))
            }
        }
    }
}

@Composable
private fun StepHeader(step: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StepProgress(step, GuideSteps.COUNT)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(stepIcon(step))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "STEP $step OF ${GuideSteps.COUNT}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(GuideSteps.title(step), style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

@Composable
private fun StepBody(step: Int, app: StartedApp, state: GuideState, links: Links) {
    val opened = { state.save(app.copy(openedSteps = app.openedSteps + step)) }
    when (step) {
        GuideSteps.SETUP -> SetupStep(state, links)
        GuideSteps.DETAILS -> DetailsStep(app, state)
        GuideSteps.CREATE_REPO -> WebStep(
            intro = "Make your own copy of the starter app on GitHub. We've filled in the name and description for you.",
            bullets = GuideSteps.createRepoBullets(app),
            onOpen = { links.openInTab(GuideSteps.createRepoUrl(app)); opened() },
        )
        GuideSteps.CLAUDE_ACCESS -> {
            WebStep(
                intro = "Let Claude work on your new repository.",
                bullets = GuideSteps.claudeAccessBullets,
                onOpen = { links.openInTab(GuideSteps.CLAUDE_ACCESS_URL); opened() },
            )
            Note("Tip: choose All repositories instead, and this step is skipped for your future apps.")
            OutlinedButton(
                onClick = {
                    state.setChecked(GuideSteps.ALL_REPOS, true)
                    state.goNext(app)
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            ) { Text("Claude already has all my repositories") }
        }
        GuideSteps.START_IN_CLAUDE -> ClaudeHandoff(app, links, onOpened = opened)
        GuideSteps.INSTALL -> {
            Text("Claude will send you a download link when your app is ready.", style = MaterialTheme.typography.bodyLarge)
            Tips(GuideSteps.installBullets, heading = "How to install it")
        }
    }
}

@Composable
private fun GuideBottomBar(
    step: Int,
    canGoNext: Boolean,
    lockedHint: String?,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    val last = step == GuideSteps.COUNT
    Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!canGoNext && lockedHint != null) {
                Text(
                    lockedHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (step > GuideSteps.SETUP) {
                    OutlinedButton(onClick = onBack, modifier = Modifier.heightIn(min = 60.dp)) {
                        AppIcon(R.drawable.ic_arrow_back, Modifier.size(22.dp), tint = LocalContentColor.current)
                        Spacer(Modifier.width(6.dp))
                        Text("Back")
                    }
                }
                PrimaryButton(
                    text = if (last) "Finish" else "Done, next step",
                    onClick = onNext,
                    enabled = canGoNext,
                    trailingIcon = if (last) R.drawable.ic_celebration else R.drawable.ic_arrow_forward,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SetupStep(state: GuideState, links: Links) {
    Text("You only need these once. Tick what you have, or just skip ahead.", style = MaterialTheme.typography.bodyLarge)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GuideSteps.setupItems.forEach { item ->
            val checked = state.isChecked(item.key)
            Card(
                onClick = { state.setChecked(item.key, !checked) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (checked) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceContainerLow,
                ),
            ) {
              Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(
                        if (checked) R.drawable.ic_check_circle else R.drawable.ic_radio_button_unchecked,
                        Modifier.size(30.dp),
                        tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    )
                    Spacer(Modifier.width(14.dp))
                    Text(item.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    item.url?.let { url ->
                        OutlinedButton(onClick = { links.openInTab(url) }) {
                            Text("Open")
                            Spacer(Modifier.width(4.dp))
                            AppIcon(R.drawable.ic_open_in_new, Modifier.size(18.dp), tint = LocalContentColor.current)
                        }
                    }
                }
                item.bullets.forEachIndexed { i, line ->
                    Text(
                        "${i + 1}. $line",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 44.dp),
                    )
                }
              }
            }
        }
    }
}

@Composable
private fun DetailsStep(app: StartedApp, state: GuideState) {
    Text("Give your app a name and say in one sentence what it should do.", style = MaterialTheme.typography.bodyLarge)
    OutlinedTextField(
        value = app.name,
        onValueChange = { state.save(app.copy(name = it)) },
        label = { Text("App name") },
        placeholder = { Text("Plant Diary") },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = app.description,
        onValueChange = { state.save(app.copy(description = it)) },
        label = { Text("What should it do?") },
        placeholder = { Text("Reminds me when to water each plant.") },
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        minLines = 3,
        modifier = Modifier.fillMaxWidth(),
    )
    if (app.isNamed) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Your repository will be called", style = MaterialTheme.typography.bodyMedium)
            Pill(app.repo, icon = R.drawable.ic_folder)
        }
    } else {
        Text(
            "Type a name to continue.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun WebStep(intro: String, bullets: List<String>, onOpen: () -> Unit) {
    Text(intro, style = MaterialTheme.typography.bodyLarge)
    Tips(bullets)
    LinkCard(
        title = "Open GitHub",
        subtitle = "Close the page when you're done to come back here.",
        icon = R.drawable.ic_open_in_new,
        trailing = R.drawable.ic_arrow_forward,
        onClick = onOpen,
    )
}
