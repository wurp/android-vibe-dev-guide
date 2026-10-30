package com.vibedev.app.model

/** One app the user is setting up. [step] is 1..[GuideSteps.COUNT], or past it when finished. */
data class StartedApp(
    val id: Long,
    val name: String = "",
    val description: String = "",
    val step: Int = 1,
) {
    val repo: String get() = repoNameFor(name)
    val isNamed: Boolean get() = name.isNotBlank()
}

/** Lowercase letters, digits and dashes, e.g. "Plant Diary!" -> "plant-diary". */
fun repoNameFor(appName: String): String =
    appName.lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
        .ifEmpty { "my-app" }
