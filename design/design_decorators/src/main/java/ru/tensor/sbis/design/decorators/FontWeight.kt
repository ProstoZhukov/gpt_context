package ru.tensor.sbis.design.decorators

import android.graphics.Typeface

/**
 * Толщина текста.
 *
 * @author ps.smirnyh
 */
enum class FontWeight(internal val style: Int) {
    DEFAULT(Typeface.NORMAL),
    BOLD(Typeface.BOLD)
}