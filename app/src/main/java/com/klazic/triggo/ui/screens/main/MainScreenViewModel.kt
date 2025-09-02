package com.klazic.triggo.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klazic.triggo.data.prefs.UserPrefs
import com.klazic.triggo.data.progress.Progress
import com.klazic.triggo.data.progress.ProgressStore
import com.klazic.triggo.data.quiz.Difficulty
import com.klazic.triggo.data.quiz.QuizRepository
import com.klazic.triggo.ui.common.LessonAssets
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MainUiState(
    val name: String = "",
    val avatarIndex: Int = 0
)


class MainViewModel(
    prefs: UserPrefs,
    private val repo: QuizRepository,
    private val store: ProgressStore,
) : ViewModel() {

    private val lessons = LessonAssets.allLessons

    val header: StateFlow<MainUiState> = combine(
        prefs.name,
        prefs.avatar
    ) { name, avatarIdx ->
        MainUiState(
            name = name,
            avatarIndex = avatarIdx
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MainUiState()
    )

    private val categories: StateFlow<List<MainContract.CategoryUi>> =
        flow {
            val list = lessons.map {
                val total = repo.countsByDifficulty(it.id).values.sum()
                MainContract.CategoryUi(
                    id = it.id,
                    titleRes = it.titleRes,
                    questionCount = total,
                    iconRes = it.iconRes
                )
            }
            emit(list)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val totalQuestions: Int =
        lessons.sumOf { repo.countsByDifficulty(it.id).values.sum() }

    private val totalSolvedFlow: Flow<Int> =
        combine(
            lessons.flatMap {
                Difficulty.entries.map { d -> store.solvedCountFlow(it.id, d) }

            }
        ){ arr -> arr.sum() }
    val globalProgress: StateFlow<Progress> =
        totalSolvedFlow.map {
            Progress(totalQuestions,it, bestScorePct = 0)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, Progress())


    private val continueTarget: StateFlow<MainContract.ContinueTarget?> =
        combine(
            lessons.map { def ->
                combine(
                    store.solvedCountFlow(def.id, Difficulty.EASY),
                    store.solvedCountFlow(def.id, Difficulty.MEDIUM),
                    store.solvedCountFlow(def.id, Difficulty.HARD)
                ) { se, sm, sh ->
                    val counts = repo.countsByDifficulty(def.id)

                    fun pct(total: Int, solved: Int) =
                        if (total == 0) 100 else (100 * solved / total)

                    val e = pct(counts[Difficulty.EASY] ?: 0, se)
                    val m = pct(counts[Difficulty.MEDIUM] ?: 0, sm)
                    val h = pct(counts[Difficulty.HARD] ?: 0, sh)
                    val allDone = (e == 100 && m == 100 && h == 100)

                    val next = when {
                        e < 100 -> Difficulty.EASY
                        m < 100 -> Difficulty.MEDIUM
                        else    -> Difficulty.HARD
                    }

                    if (allDone) null else MainContract.ContinueTarget(
                        categoryId = def.id,
                        titleRes = def.titleRes,
                        difficulty = next
                    )
                }
            }
        ) { arr -> arr.firstOrNull { it != null } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val ui: StateFlow<MainContract.UiState> =
        combine(categories, continueTarget) { cats, cont ->
            MainContract.UiState(categories = cats, continueTarget = cont, isLoading = false)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, MainContract.UiState())
}

class MainViewModelFactory(
    private val prefs: UserPrefs,
    private val repo: QuizRepository,
    private val store: ProgressStore,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(prefs, repo, store) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}