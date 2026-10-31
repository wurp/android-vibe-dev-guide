package com.vibedev.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vibedev.app.model.GuideSteps
import com.vibedev.app.model.StartedApp

/** Observable app state for the screens, saved through [store] on every change. */
class GuideState(private val store: GuideStore) {
    val apps = mutableStateListOf<StartedApp>().apply { addAll(store.loadApps()) }
    var githubUsername by mutableStateOf(store.githubUsername)
        private set
    private val checks = mutableStateMapOf<String, Boolean>()

    var currentAppId: Long?
        get() = store.currentAppId?.takeIf { id -> apps.any { it.id == id } }
        set(value) { store.currentAppId = value }

    fun app(id: Long): StartedApp? = apps.firstOrNull { it.id == id }

    fun startNewApp(): StartedApp {
        val first = if (store.setupDone) GuideSteps.DETAILS else GuideSteps.SETUP
        return StartedApp(id = System.currentTimeMillis(), step = first).also(::save)
    }

    fun save(app: StartedApp) {
        val i = apps.indexOfFirst { it.id == app.id }
        if (i >= 0) apps[i] = app else apps.add(0, app)
        store.saveApps(apps)
        if (app.step > GuideSteps.SETUP) store.setupDone = true
    }

    fun updateUsername(name: String) {
        githubUsername = name.trim().removePrefix("@")
        store.githubUsername = githubUsername
    }

    val claudeSeesAllRepos: Boolean get() = isChecked(GuideSteps.ALL_REPOS)

    fun goNext(app: StartedApp) = save(app.copy(step = GuideSteps.next(app.step, claudeSeesAllRepos)))
    fun goBack(app: StartedApp) = save(app.copy(step = GuideSteps.previous(app.step, claudeSeesAllRepos)))

    fun isChecked(key: String) = checks.getOrPut(key) { store.isChecked(key) }
    fun setChecked(key: String, checked: Boolean) {
        checks[key] = checked
        store.setChecked(key, checked)
    }
}
