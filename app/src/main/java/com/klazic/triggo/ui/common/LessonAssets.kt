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
            "brojevna_kruznica",
            R.string.brojevna_kruznica,
            R.drawable.ic_inverse
        ),
        LessonDef(
            "osnove_kutova_i_radijana",
            R.string.osnove_kutova_i_radijana,
            R.drawable.ic_angles
        ),
        LessonDef(
            "definicija_trigonometrijskih_funkcija",
            R.string.definicija_trigonometrijskih_funkcija,
            R.drawable.ic_inverse
        ),
        LessonDef(
            "odredivanje_vrijednosti_trigonometrijskih_funkcija",
            R.string.odredivanje_vrijednosti_trigonometrijskih_funkcija,
            R.drawable.ic_inverse
        ),
        LessonDef(
            "svojstva_trigonometrijskih_funkcija",
            R.string.svojstva_trigonometrijskih_funkcija,
            R.drawable.ic_inverse
        ),
        LessonDef(
            "osnovni_trigonometrijski_identiteti",
            R.string.osnovni_trigonometrijski_identiteti,
            R.drawable.ic_identity
        ),
        LessonDef(
            "adicijske_formule",
            R.string.adicijske_formule,
            R.drawable.ic_identity
        ),
        LessonDef(
            "grafovi_trigonometrijskih_funkcija",
            R.string.grafovi_trigonometrijskih_funkcija,
            R.drawable.ic_graphs
        ),
        LessonDef(
            "trigonometrijske_jednadzbe",
            R.string.trigonometrijske_jednadzbe,
            R.drawable.ic_equations
        ),
        LessonDef(
            "trigonometrijske_nejednadzbe",
            R.string.trigonometrijske_nejednadzbe,
            R.drawable.ic_identity
        ),
        LessonDef(
            "trigonometrija_pravokutnog_trokuta",
            R.string.trigonometrija_pravokutnog_trokuta,
            R.drawable.ic_identity
        ),
        LessonDef(
            "sinusov_i_kosinusov_poucak",
            R.string.sinusov_i_kosinusov_poucak,
            R.drawable.ic_identity
        )
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