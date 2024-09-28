package ru.tensor.sbis.design.cloud_view.content.utils

import androidx.annotation.IntDef
import ru.tensor.sbis.design.cloud_view.CloudView

const val DEFAULT_TEXT = 0
const val DISABLED_TEXT = 1

/**
 * Перечень цвета текста в [CloudView]
 *
 * @author ma.kolpakov
 */
@Retention(AnnotationRetention.SOURCE)
@IntDef(
    DEFAULT_TEXT,
    DISABLED_TEXT
)
annotation class CloudViewTextColorType