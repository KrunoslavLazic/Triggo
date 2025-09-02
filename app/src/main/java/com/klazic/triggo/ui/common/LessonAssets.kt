package com.klazic.triggo.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.klazic.triggo.R

@Immutable
data class LessonDef(
    val id: String,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int
)

object LessonAssets {
    val allLessons = listOf(
        LessonDef(
            "osnove_kutova_i_radijana",
            R.string.osnove_kutova_i_radijana,
            R.drawable.ic_angles
        ),
        LessonDef(
            "trigonometrijski_omjeri",
            R.string.trigonometrijski_omjeri,
            R.drawable.ic_ratios
        ),
        LessonDef(
            "trigonometrijski_identiteti",
            R.string.trigonometrijski_identiteti,
            R.drawable.ic_identity
        ),
        LessonDef(
            "grafovi_trigonometrijskih_funkcija",
            R.string.grafovi_trigonometrijskih_funkcija,
            R.drawable.ic_graphs
        ),
        LessonDef(
            "inverzne_trigonometrijske_funkcije",
            R.string.inverzne_trigonometrijske_funkcije,
            R.drawable.ic_inverse
        ),
        LessonDef("trigonometrijske_jednadzbe", R.string.trigonometrijske_jednadzbe, R.drawable.ic_equations),
        LessonDef(
            "primjene_trigonometrije",
            R.string.primjene_trigonometrije,
            R.drawable.ic_applications
        ),
    )


    private val byId = allLessons.associateBy { it.id }
    private fun defFor(id: String): LessonDef? = byId[id]
    @StringRes
    fun titleRes(rawId: String, @StringRes fallback: Int = R.string.app_name): Int =
        defFor(rawId)?.titleRes ?: fallback

    @DrawableRes
    fun iconRes(rawId: String, @DrawableRes fallback: Int = R.drawable.ic_equations): Int =
        defFor(rawId)?.iconRes ?: fallback
}