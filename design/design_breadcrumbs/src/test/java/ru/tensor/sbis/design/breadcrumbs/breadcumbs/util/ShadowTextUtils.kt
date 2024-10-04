package ru.tensor.sbis.design.breadcrumbs.breadcumbs.util

import android.text.TextPaint
import android.text.TextUtils
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/**
 * Реализует метод [TextUtils.ellipsize] для использования в тестах, предусматривая не просто усечение текста, а
 * добавление троеточия
 *
 * @author us.bessonov
 */
@Implements(TextUtils::class)
internal class ShadowTextUtils {

    companion object {

        @Implementation
        @JvmStatic
        fun ellipsize(
            text: CharSequence,
            p: TextPaint?,
            avail: Float,
            where: TextUtils.TruncateAt?
        ): CharSequence = when {
            avail <= 0                   -> ""
            text.length <= avail.toInt() -> text
            else -> "${text.take(avail.toInt() - 1)}\u2026"
        }
    }
}