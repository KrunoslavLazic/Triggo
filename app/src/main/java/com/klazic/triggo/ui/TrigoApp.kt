package com.klazic.triggo.ui

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import com.klazic.triggo.data.prefs.UserPrefs
import com.klazic.triggo.data.prefs.dataStore
import com.klazic.triggo.data.progress.ProgressStore
import com.klazic.triggo.data.quiz.QuizRepository
import com.klazic.triggo.data.streak.DailyStreakScore
import com.klazic.triggo.di.LocalDailyStreak
import com.klazic.triggo.di.LocalProgressStore
import com.klazic.triggo.di.LocalQuizRepository
import com.klazic.triggo.navigation.TrigoNavHost
import com.klazic.triggo.ui.theme.TrigoTheme

object ThemeOption{
    const val SYSTEM = "system"
    const val LIGHT = "light"
    const val DARK = "dark"
}

@Composable
fun TrigoApp(){
    val context = LocalContext.current
    val prefs = remember { UserPrefs(context) }

    val theme by prefs.theme.collectAsState(initial = ThemeOption.SYSTEM)
    val dynamic by prefs.dynamic.collectAsState(initial = false)
    val lang by prefs.language.collectAsState("system")

    val dark = when(theme){
        ThemeOption.LIGHT -> false
        ThemeOption.DARK -> true
        else -> isSystemInDarkTheme()
    }
    val dynamicApplied = dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val appCtx = LocalContext.current.applicationContext
    val progressStore = remember { ProgressStore(appCtx.dataStore) }
    val quizRepository = remember { QuizRepository(appCtx) }
    val streakStore = remember { DailyStreakScore(context.dataStore) }



    TrigoTheme(darkTheme = dark, dynamicColor = dynamicApplied){
        LaunchedEffect(lang) {
            val locales = when(lang){
                "system" -> LocaleListCompat.getEmptyLocaleList()
                "en" -> LocaleListCompat.forLanguageTags("en")
                "hr" -> LocaleListCompat.forLanguageTags("hr")
                else -> LocaleListCompat.forLanguageTags(lang)
            }
            AppCompatDelegate.setApplicationLocales(locales)

        }

        CompositionLocalProvider(
            LocalProgressStore provides progressStore,
            LocalQuizRepository provides quizRepository,
            LocalDailyStreak provides streakStore
        ) {
            TrigoNavHost()
        }
    }
}