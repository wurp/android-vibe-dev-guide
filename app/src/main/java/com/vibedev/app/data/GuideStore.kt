package com.vibedev.app.data

import android.content.Context
import com.vibedev.app.model.StartedApp
import org.json.JSONArray
import org.json.JSONObject

/** Where the guide keeps its progress. */
interface GuideStore {
    fun loadApps(): List<StartedApp>
    fun saveApps(apps: List<StartedApp>)
    var currentAppId: Long?
    var githubUsername: String
    var setupDone: Boolean
    fun isChecked(key: String): Boolean
    fun setChecked(key: String, checked: Boolean)
}

class PrefsGuideStore(context: Context) : GuideStore {
    private val prefs = context.getSharedPreferences("guide", Context.MODE_PRIVATE)

    override fun loadApps(): List<StartedApp> {
        val json = JSONArray(prefs.getString(KEY_APPS, "[]"))
        return (0 until json.length()).map { i ->
            val o = json.getJSONObject(i)
            StartedApp(
                id = o.getLong("id"),
                name = o.optString("name"),
                description = o.optString("description"),
                step = o.optInt("step", 1),
                openedSteps = o.optJSONArray("opened")
                    ?.let { a -> (0 until a.length()).map { a.getInt(it) }.toSet() }
                    ?: emptySet(),
            )
        }
    }

    override fun saveApps(apps: List<StartedApp>) {
        val json = JSONArray()
        apps.forEach {
            json.put(
                JSONObject()
                    .put("id", it.id)
                    .put("name", it.name)
                    .put("description", it.description)
                    .put("step", it.step)
                    .put("opened", JSONArray(it.openedSteps.toList())),
            )
        }
        prefs.edit().putString(KEY_APPS, json.toString()).apply()
    }

    override var currentAppId: Long?
        get() = prefs.getLong(KEY_CURRENT, -1).takeIf { it >= 0 }
        set(value) = prefs.edit().putLong(KEY_CURRENT, value ?: -1).apply()

    override var githubUsername: String
        get() = prefs.getString(KEY_USER, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER, value).apply()

    override var setupDone: Boolean
        get() = prefs.getBoolean(KEY_SETUP, false)
        set(value) = prefs.edit().putBoolean(KEY_SETUP, value).apply()

    override fun isChecked(key: String) = prefs.getBoolean("check_$key", false)
    override fun setChecked(key: String, checked: Boolean) =
        prefs.edit().putBoolean("check_$key", checked).apply()

    private companion object {
        const val KEY_APPS = "apps"
        const val KEY_CURRENT = "current_app"
        const val KEY_USER = "github_user"
        const val KEY_SETUP = "setup_done"
    }
}
