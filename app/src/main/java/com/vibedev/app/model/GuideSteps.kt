package com.vibedev.app.model

import java.net.URLEncoder

/** What each step shows. Text and links live here so screens only lay them out. */
object GuideSteps {
    const val COUNT = 6
    const val SETUP = 1
    const val DETAILS = 2
    const val CREATE_REPO = 3
    const val CLAUDE_ACCESS = 4
    const val START_IN_CLAUDE = 5
    const val INSTALL = 6

    fun title(step: Int): String = when (step) {
        SETUP -> "Before you start"
        DETAILS -> "Your app"
        CREATE_REPO -> "Create the repository"
        CLAUDE_ACCESS -> "Give Claude access"
        START_IN_CLAUDE -> "Start in Claude"
        INSTALL -> "Install on your phone"
        else -> "All done"
    }

    val setupItems = listOf(
        ChecklistItem("plan", "A Claude Pro or Max plan", null),
        ChecklistItem("github", "A free GitHub account", "https://github.com/signup"),
        ChecklistItem("connect", "Connect GitHub to Claude (once)", "https://claude.ai/code"),
    )

    fun createRepoUrl(app: StartedApp): String =
        "https://github.com/new?template_owner=wurp&template_name=vibe-dev-template" +
            "&owner=@me&visibility=private" +
            "&name=${encode(app.repo)}&description=${encode(app.description)}"

    const val CLAUDE_ACCESS_URL = "https://github.com/apps/claude/installations/new"
    const val CLAUDE_CODE_URL = "https://claude.ai/code"

    fun createRepoBullets(app: StartedApp) = listOf(
        "Check the name says ${app.repo}.",
        "Leave it Private, unless you want to share the app's link with friends.",
        "Tap Create repository.",
    )

    val claudeAccessBullets = listOf(
        "Choose your account.",
        "Under Repository access, add your new repository.",
        "Tap Save.",
    )

    fun claudeMessage(app: StartedApp) = "Start my new app in ${app.repo} from its description."

    fun startInClaudeBullets(app: StartedApp) = listOf(
        "The Claude app opens on the repository you used last. Tap the name above the message box and choose ${app.repo}.",
        "Paste the message and send it.",
        "Come back here when Claude gives you a download link.",
    )

    val installBullets = listOf(
        "In Claude, long-press the download link and tap Copy link.",
        "Paste it into Chrome's address bar. Chrome needs to be signed in to GitHub.",
        "If Chrome warns about the file, tap Download anyway, then tap the download to install.",
        "The first time, allow installs from Chrome. This is normal for apps not from the Play Store.",
        "The first time, Google Play Protect asks you to confirm. Say yes; it asks only once.",
        "Play Protect scans every new version before it installs. Just wait for it.",
    )

    fun downloadUrl(githubUser: String, app: StartedApp) =
        "https://github.com/$githubUser/${app.repo}/releases/download/test-build/app.apk"

    private fun encode(text: String) = URLEncoder.encode(text, "UTF-8")
}

data class ChecklistItem(val key: String, val label: String, val url: String?)
