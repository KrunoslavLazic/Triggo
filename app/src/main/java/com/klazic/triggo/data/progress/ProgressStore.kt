package com.klazic.triggo.data.progress

import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.klazic.triggo.data.quiz.Difficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.math.max

private const val PROGRESS_PREFIX = "progress."

@Immutable
data class Coverage(
    val easy: Int,
    val medium: Int,
    val hard: Int
)

data class Progress(
    val attempted: Int = 0,
    val correct: Int = 0,
    val bestScorePct: Int = 0
) {
    val masteryPct: Int get() = if (attempted == 0) 0 else (100 * correct / attempted)
}

class ProgressStore(private val dataStore: DataStore<Preferences>) {
    private fun baseKey(cat: String, diff: Difficulty) =
        "$PROGRESS_PREFIX$cat.${diff.name.lowercase()}"

    fun flow(cat: String, diff: Difficulty): Flow<Progress> = dataStore.data.map { p ->
        val base = baseKey(cat, diff)
        Progress(
            attempted = p[intPreferencesKey("$base.attempted")] ?: 0,
            correct = p[intPreferencesKey("$base.correct")] ?: 0,
            bestScorePct = p[intPreferencesKey("$base.best")] ?: 0
        )
    }

    suspend fun recordSession(
        cat: String,
        diff: Difficulty,
        correctInSession: Int,
        totalInSession: Int
    ) {
        require(totalInSession > 0)
        val pct = (100 * correctInSession / totalInSession)
        val base = baseKey(cat, diff)
        dataStore.edit { prefs ->
            val aK = intPreferencesKey("$base.attempted")
            val cK = intPreferencesKey("$base.correct")
            val bK = intPreferencesKey("$base.best")

            val oldA = prefs[aK] ?: 0
            val oldC = prefs[cK] ?: 0
            prefs[aK] = oldA + totalInSession
            prefs[cK] = oldC + correctInSession
            val oldBest = prefs[bK] ?: 0
            if (pct > oldBest) prefs[bK] = pct
        }
    }

    suspend fun resetAll() {
        dataStore.edit { prefs ->
            val toRemove = prefs.asMap().keys.filter { it.name.startsWith(PROGRESS_PREFIX) }
            toRemove.forEach { prefs.remove(it) }
        }
    }

    private fun solvedKey(cat: String, diff: Difficulty) =
        stringSetPreferencesKey("${baseKey(cat, diff)}.solved_ids")

    fun solvedCountFlow(cat: String, diff: Difficulty) = dataStore.data.map { prefs ->
        (prefs[solvedKey(cat, diff)] ?: emptySet()).size
    }

    suspend fun recordSolved(cat: String, diff: Difficulty, correctIds: Set<String>) {
        dataStore.edit { prefs ->
            val key = solvedKey(cat, diff)
            val old = prefs[key] ?: emptySet()
            prefs[key] = old + correctIds
        }
    }

    fun coverageFlow(category: String): Flow<Coverage> = combine(
        solvedCountFlow(category,Difficulty.EASY),
        solvedCountFlow(category,Difficulty.MEDIUM),
        solvedCountFlow(category,Difficulty.HARD)
    ) { e, m, h -> Coverage(e,m,h)}
}

operator fun Progress.plus(o: Progress) = Progress(
    attempted = attempted + o.attempted,
    correct = correct + o.correct,
    bestScorePct = max(bestScorePct, o.bestScorePct)
)

