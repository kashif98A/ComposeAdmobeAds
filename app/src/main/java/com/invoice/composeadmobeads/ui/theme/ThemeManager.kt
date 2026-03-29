package com.invoice.composeadmobeads.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Theme Mode Options
 */
enum class ThemeMode {
    LIGHT,      // Always light
    DARK,       // Always dark
    SYSTEM      // Follow system setting
}

// DataStore for saving theme preference
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")

/**
 * Theme Manager - Handles theme persistence
 */
class ThemeManager(private val context: Context) {

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_mode")

        @Volatile
        private var instance: ThemeManager? = null

        fun getInstance(context: Context): ThemeManager {
            return instance ?: synchronized(this) {
                instance ?: ThemeManager(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Get current theme mode as Flow
     */
    val themeMode: Flow<ThemeMode> = context.themeDataStore.data.map { preferences ->
        val themeName = preferences[THEME_KEY] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(themeName)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    /**
     * Save theme mode
     */
    suspend fun setThemeMode(mode: ThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_KEY] = mode.name
        }
    }
}

/**
 * Composable to remember theme state
 */
@Composable
fun rememberThemeMode(): ThemeMode {
    val context = LocalContext.current
    val themeManager = remember { ThemeManager.getInstance(context) }
    val themeMode by themeManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    return themeMode
}