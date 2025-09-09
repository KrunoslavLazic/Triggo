package com.klazic.triggo.data.streak

import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

@Immutable
data class StreakSummary(
    val todayActive: Boolean = false,
    val current: Int = 0,
    val lastDay: LocalDate? = null
)

class DailyStreakScore(
    private val dataStore: DataStore<Preferences>,
    private val zoneId: ZoneId = ZoneId.systemDefault()
) {
    private val K_CUR = intPreferencesKey("streak.current")
    private val K_LAST = stringPreferencesKey("streak.last_day")
    private val K_DAYS = stringSetPreferencesKey("streak.days")

    val summary: Flow<StreakSummary> = dataStore.data.map { p ->
        val today = LocalDate.now(zoneId)
        val days = p[K_DAYS] ?: emptySet()
        StreakSummary(
            todayActive = days.contains(today.toString()),
            current = p[K_CUR] ?: 0,
            lastDay = p[K_LAST]?.let(LocalDate::parse)
        )
    }
    suspend fun markActiveToday() {
        val today = LocalDate.now(zoneId)
        dataStore.edit { p ->
            val set = (p[K_DAYS] ?: emptySet()).toMutableSet()
            if (set.contains(today.toString())) return@edit
            val last = p[K_LAST]?.let(LocalDate::parse)
            val cur = p[K_CUR] ?: 0
            val newCur = when {
                last == null -> 1
                last.plusDays(1) == today -> cur + 1
                last == today -> cur
                else -> 1
            }
            set.add(today.toString())
            if (set.size > 400) {
                val cutoff = today.minusDays(400)
                set.removeAll { LocalDate.parse(it).isBefore(cutoff) }
            }
            p[K_CUR] = newCur
            p[K_LAST] = today.toString()
            p[K_DAYS] = set
        }
    }

    suspend fun reset() {
        dataStore.edit { p ->
            p.remove(K_CUR); p.remove(K_LAST); p.remove(K_DAYS)
        }
    }

}
