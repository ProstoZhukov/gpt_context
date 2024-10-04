package ru.tensor.sbis.design.counters.utils

import android.content.Context
import androidx.annotation.IntRange
import ru.tensor.sbis.design.counters.R
import ru.tensor.sbis.design.utils.checkSafe
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Набор форматтеров счетчиков.
 *
 * @author da.zolotarev
 */
sealed interface Formatter {

    /**
     * Выдает отформатированное по правилам число
     */
    fun format(count: Int): String

    /** @SelfDocumented */
    class CustomFormatter(private val formatter: (Int) -> String) : Formatter {
        override fun format(count: Int) = formatter.invoke(count)
    }

    /**
     * Фоматировщик по международным правилам.
     *
     * @param isDropZeroes будет ли отображен дробный 0 (1.0к вместо 1к)
     */
    class InternationalFormatter(private val context: Context, isDropZeroes: Boolean = true) : Formatter {
        private val decimalFormat = DecimalFormat(
            if (isDropZeroes) FORMAT_PATTERN else FORMAT_PATTERN_WITH_ZERO,
            // Хардкодим US, чтобы во всех локалях дробная часть отделялась "."
            DecimalFormatSymbols(Locale.US)
        ).apply {
            maximumFractionDigits = 1
            roundingMode = RoundingMode.FLOOR
        }

        private val letterK = context.getString(R.string.design_counters_thousand_formatter_reduction)
        private val letterM = context.getString(R.string.design_counters_million_formatter_reduction)
        private val letterB = context.getString(R.string.design_counters_billion_formatter_reduction)
        private val letterT = context.getString(R.string.design_counters_trillion_formatter_reduction)

        override fun format(@IntRange(from = 0) count: Int): String {
            checkSafe(count >= 0)
            return when {
                // Оставил TRILLION на будущее, когда перейдем на Long
                count >= TRILLION -> decimalFormat.format((count / TRILLION)) + letterT
                count >= BILLION -> decimalFormat.format((count / BILLION)) + letterB
                count >= MILLION -> decimalFormat.format((count / MILLION)) + letterM
                count >= THOUSAND -> decimalFormat.format((count / THOUSAND)) + letterK
                count < 1 -> ""
                else -> count.toString()
            }
        }
    }

    /** @SelfDocumented */
    object HundredFormatter : Formatter {

        override fun format(@IntRange(from = 0) count: Int): String {
            checkSafe(count >= 0)
            return if (count == 0) "" else if (count < 100) count.toString() else "99+"
        }
    }

    companion object {
        const val TRILLION = 1_000_000_000_000.0
        const val BILLION = 1_000_000_000.0
        const val MILLION = 1_000_000.0
        const val THOUSAND = 1000.0
        const val FORMAT_PATTERN = ""
        const val FORMAT_PATTERN_WITH_ZERO = "#.0"
    }
}