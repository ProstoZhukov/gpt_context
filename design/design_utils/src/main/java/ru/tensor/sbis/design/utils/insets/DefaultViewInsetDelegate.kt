package ru.tensor.sbis.design.utils.insets

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.tensor.sbis.design.utils.extentions.ViewMargins
import ru.tensor.sbis.design.utils.extentions.ViewPaddings
import ru.tensor.sbis.design.utils.extentions.getMargins
import ru.tensor.sbis.design.utils.extentions.getPaddings
import ru.tensor.sbis.design.utils.extentions.setBottomMargin
import ru.tensor.sbis.design.utils.extentions.setBottomPadding
import ru.tensor.sbis.design.utils.extentions.setLeftMargin
import ru.tensor.sbis.design.utils.extentions.setLeftPadding
import ru.tensor.sbis.design.utils.extentions.setRightMargin
import ru.tensor.sbis.design.utils.extentions.setRightPadding
import ru.tensor.sbis.design.utils.extentions.setTopMargin
import ru.tensor.sbis.design.utils.extentions.setTopPadding
import ru.tensor.sbis.design.utils.insets.IndentType.MARGIN
import ru.tensor.sbis.design.utils.insets.IndentType.PADDING
import ru.tensor.sbis.design.utils.insets.Position.BOTTOM
import ru.tensor.sbis.design.utils.insets.Position.LEFT
import ru.tensor.sbis.design.utils.insets.Position.RIGHT
import ru.tensor.sbis.design.utils.insets.Position.TOP

/**
 * Интерфейс для помощи установки дефолтных отступов для вью, когда приходят системные инстеты
 */
interface DefaultViewInsetDelegate {

    /**
     * Добавить подписку для установки отступов после получения инсетов
     */
    fun initInsetListener(params: DefaultViewInsetDelegateParams)
}

/**
 * Интерфейс для помощи установки дефолтных отступов для вью, когда приходят системные инсеты
 */
class DefaultViewInsetDelegateImpl : DefaultViewInsetDelegate {

    override fun initInsetListener(params: DefaultViewInsetDelegateParams) {
        val handlers = params.viewsToAddInset.map { InsetsHandler(it) }
        params.viewToOverrideInsetsListener.forEach { it.setOnApplyWindowInsetsListener { _, insets -> insets } }
        params.viewsToAddInset.forEachIndexed { index, viewToAddInset ->
            val initialPaddings = viewToAddInset.view.getPaddings()
            val initialMargins = viewToAddInset.view.getMargins()
            ViewCompat.setOnApplyWindowInsetsListener(viewToAddInset.view) { _, windowInsets ->
                handlers[index].applyInsets(
                    windowInsets,
                    initialPaddings,
                    initialMargins
                )
                windowInsets
            }
        }
    }
}

/** @SelfDocumented */
fun WindowInsetsCompat.getSystemBarsInsets() = getInsets(WindowInsetsCompat.Type.systemBars())

/**
 * Параметры для работы с инсетами
 *
 * @param viewsToAddInset - список вью для установки им отступа, равному размеру интсета
 * @param viewToOverrideInsetsListener - список вью для переопределения setOnApplyWindowInsetsListener у вью
 */
data class DefaultViewInsetDelegateParams(
    val viewsToAddInset: List<ViewToAddInset> = arrayListOf(),
    val viewToOverrideInsetsListener: List<View> = arrayListOf()
)

/**
 * Данные для установки вью отступа, равному размеру инсета
 *
 * @param view - вью для установки отступа
 * @param indents - отступ
 */
data class ViewToAddInset(
    val view: View,
    val indents: List<Indent> = arrayListOf()
)

typealias Indent = Pair<IndentType, Position>

/** @SelfDocument */
enum class IndentType {
    MARGIN,
    PADDING
}

/** @SelfDocument */
enum class Position {
    LEFT,
    TOP,
    RIGHT,
    BOTTOM
}

private class InsetsHandler(val params: ViewToAddInset) {
    private val lastInsets = hashMapOf<Indent, Int>()

    fun applyInsets(windowInsets: WindowInsetsCompat, paddings: ViewPaddings, margins: ViewMargins) {
        val view = params.view
        params.indents.forEach { indent ->
            when (indent) {
                MARGIN to LEFT -> indent.applyInset(windowInsets) { view.setLeftMargin(it + margins.left) }
                MARGIN to TOP -> indent.applyInset(windowInsets) { view.setTopMargin(it + margins.top) }
                MARGIN to RIGHT -> indent.applyInset(windowInsets) { view.setRightMargin(it + margins.right) }
                MARGIN to BOTTOM -> indent.applyInset(windowInsets) { view.setBottomMargin(it + margins.bottom) }
                PADDING to LEFT -> indent.applyInset(windowInsets) { view.setLeftPadding(it + paddings.left) }
                PADDING to TOP -> indent.applyInset(windowInsets) { view.setTopPadding(it + paddings.top) }
                PADDING to RIGHT -> indent.applyInset(windowInsets) { view.setRightPadding(it + paddings.right) }
                PADDING to BOTTOM -> indent.applyInset(windowInsets) { view.setBottomPadding(it + paddings.bottom) }
            }
        }
    }

    private fun Indent.applyInset(windowInsets: WindowInsetsCompat, onApply: (Int) -> Unit) {
        val systemBarsInsets = windowInsets.getSystemBarsInsets()
        val inset = when (second) {
            LEFT -> systemBarsInsets.left
            TOP -> systemBarsInsets.top
            RIGHT -> systemBarsInsets.right
            BOTTOM -> systemBarsInsets.bottom
        }
        val lastInset = lastInsets[this] ?: 0
        if (lastInset != inset) {
            onApply(inset)
            lastInsets[this] = inset
        }
    }
}