package com.klazic.triggo.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_preferences")

object Lang{
    const val SYSTEM = "system"
    const val EN = "en"
    const val HR = "hr"
}

private object PrefKey {
    val USER_NAME = stringPreferencesKey("name")
    val THEME = stringPreferencesKey("theme")
    val DYNAMIC = booleanPreferencesKey("dynamic")
    val AVATAR = intPreferencesKey("avatar")
    val LANGUAGE = stringPreferencesKey("language")

}

class UserPrefs(private val context: Context) {

    val name = context.dataStore.data.map { it[PrefKey.USER_NAME] ?: "" }
    val theme = context.dataStore.data.map { it[PrefKey.THEME] ?: "system" }
    val dynamic = context.dataStore.data.map { it[PrefKey.DYNAMIC] ?: false }
    val avatar = context.dataStore.data.map { it[PrefKey.AVATAR] ?: 0 }
    val language = context.dataStore.data.map { it[PrefKey.LANGUAGE] ?: Lang.SYSTEM }

    suspend fun setName(value: String) {
        context.dataStore.edit { it[PrefKey.USER_NAME] = value }
    }

    suspend fun setTheme(value: String){
        context.dataStore.edit { it[PrefKey.THEME] = value }
    }

    suspend fun setDynamic(enabled: Boolean){
        context.dataStore.edit { it[PrefKey.DYNAMIC] = enabled }
    }

    suspend fun setAvatar(index: Int){
        context.dataStore.edit { it[PrefKey.AVATAR] = index }
    }

    suspend fun setLanguage(value: String){
        context.dataStore.edit { it[PrefKey.LANGUAGE] = value }
    }

}