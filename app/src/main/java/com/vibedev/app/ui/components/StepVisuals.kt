package com.vibedev.app.ui.components

import androidx.annotation.DrawableRes
import com.vibedev.app.R
import com.vibedev.app.model.GuideSteps

/** The icon shown for each step. Kept in the UI layer so the model stays free of resources. */
@DrawableRes
fun stepIcon(step: Int): Int = when (step) {
    GuideSteps.SETUP -> R.drawable.ic_checklist
    GuideSteps.DETAILS -> R.drawable.ic_edit_note
    GuideSteps.CREATE_REPO -> R.drawable.ic_create_new_folder
    GuideSteps.CLAUDE_ACCESS -> R.drawable.ic_key
    GuideSteps.START_IN_CLAUDE -> R.drawable.ic_forum
    GuideSteps.INSTALL -> R.drawable.ic_install_mobile
    else -> R.drawable.ic_celebration
}
