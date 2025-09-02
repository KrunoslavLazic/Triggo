package com.klazic.triggo.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.klazic.triggo.R

data class AvatarDef(
    @DrawableRes val resId: Int,
    val id: String,
    @StringRes val labelRes: Int)

object AvatarAssets{
    val avatars: List<AvatarDef> = listOf(
        AvatarDef(R.drawable.fox,   "fox",   R.string.avatar_scientist_fox),
        AvatarDef(R.drawable.owl,   "owl",   R.string.avatar_teacher_owl),
        AvatarDef(R.drawable.mouse, "mouse", R.string.avatar_astronaut_mouse),
        AvatarDef(R.drawable.dog,   "dog",   R.string.avatar_architect_dog),
        AvatarDef(R.drawable.cat,   "cat",   R.string.avatar_librarian_cat),
    )

    @DrawableRes
    fun at(index: Int): Int = avatars.getOrNull(index)?.resId ?: R.drawable.trigo_logo
}