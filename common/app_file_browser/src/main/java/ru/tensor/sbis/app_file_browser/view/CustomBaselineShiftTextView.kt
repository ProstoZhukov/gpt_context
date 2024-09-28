package ru.tensor.sbis.app_file_browser.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.roundToInt

/**
 * Предназначен для корректировки базовой линии текста так, чтобы выравнивать первую строку текста по центру.
 * [baselineSource]
 *
 * @author us.bessonov
 */
internal class CustomBaselineShiftTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    /**
     * [View] по вертикальному центру которого нужно выравнивать первую строку текста
     */
    var baselineSource: View? = null

    override fun getBaseline(): Int {
        val baselineSource = baselineSource ?: return super.getBaseline()
        if (layout == null || baselineSource.measuredHeight == 0) return super.getBaseline()
        return baselineSource.baseline + (getFirstLineHeight() - baselineSource.measuredHeight) / 2
    }

    private fun getFirstLineHeight() = paint.fontMetrics.run { (descent - top).roundToInt() }
}