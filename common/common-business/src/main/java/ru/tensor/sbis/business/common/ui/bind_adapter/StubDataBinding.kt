package ru.tensor.sbis.business.common.ui.bind_adapter

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.postDelayed
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.business.common.R
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.stubview.StubViewMode
import ru.tensor.sbis.design.utils.getThemeColorInt
import kotlin.math.abs

/**
 * Data Binding адаптер метод для настройки высоты вью-заглушек (не списочных данных) в [RecyclerView]
 *
 * @param shouldTakeAllAvailableHeightInParent должен ли View заглушки занимать всю доступную высоту в родительском View,
 * при наличии свободного места
 * @param shouldWrapContentByDefault должен ли View заглушки иметь высоту [ViewGroup.LayoutParams.WRAP_CONTENT], если значение
 * [shouldTakeAllAvailableHeightInParent] ложно или заглушка не помещается по доступной высоте родителя
 */
@BindingAdapter(
    value = [
        "takeAllAvailableHeightInParent",
        "wrapContentByDefault"], requireAll = true
)
fun View.takeAllAvailableHeightInParent(
    shouldTakeAllAvailableHeightInParent: Boolean,
    shouldWrapContentByDefault: Boolean,
) {
    if (shouldTakeAllAvailableHeightInParent.not()) {
        if (shouldWrapContentByDefault && layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams = layoutParams.apply {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
        return
    }
    if (viewTreeObserver.isAlive.not()) {
        return
    }

    // При показе ошибки её vm обновляется быстрей, чем vm списка.
    // Из-за этого при расчёте положения заглушки ошибки учитываются старые view, которые сразу после этого расчёта удаляются.
    // Необходимо подождать некоторое время, когда старые view будут удалены, чтобы положение посчиталось корректно.
    // Задержки в 20 миллисекунд хватает и для глаза это никак не заметно.
    if (isShown.not() && height == 0) {
        alpha = 0f
    }
    doOnPreDraw {
        val runnable = {
            takeAllAvailableHeightInParent(shouldWrapContentByDefault)
            if (alpha == 0f) {
                animate().alpha(1f).setDuration(ALPHA_IN_DELAY).start()
            }
        }
        postDelayed(runnable, PRE_DRAW_DELAY)
    }
}

/**
 * Установка контента для отображения в [StubView].
 * При добавлении в RecyclerView не всегда корректно отрабатывает установка ширины текста, правит [postDelayed].
 */
@BindingAdapter(value = ["stubMode", "stubMinHeight", "stubContent"], requireAll = true)
internal fun StubView.setStubMode(mode: StubViewMode, minHeight: Boolean, content: StubViewContent) {
    @Suppress("SENSELESS_COMPARISON")
    if (content == null || mode == null) return
    postDelayed(LAYOUT_UPDATE_DELAY) {
        doOnLayout {
            setContent(content)
            setMode(
                mode = mode,
                minHeight = minHeight
            )
        }
    }
}

/**
 * Где:
 * @implant вью для которой делается расчет высоты
 * @implantRealHeight действительная высота контента [implant] до первого расчета [takeAllAvailableHeightInParent]
 * @neighbors список вью представляющих контент на экране помимо [implant]
 */
private fun View.takeAllAvailableHeightInParent(
    shouldWrapContentByDefault: Boolean,
) {
    if (parent == null || parent !is ViewGroup) {
        return
    }
    val implant = this@takeAllAvailableHeightInParent
    val implantRealHeight = implant.getTag(INITIAL_HEIGHT_KEY) as? Int ?: implant.height

    val parent = parent as ViewGroup

    /**
     * - не учитываем [implant] иначе при перестроении дерева высота контента уже может быть определена неверно
     * - не учитываем вью все еще доступные в списке, но уже не представленные в адаптере
     */
    val neighbors = (0 until parent.childCount)
        .map { parent.getChildAt(it) }
        .filter { it != this }
        .filter {
            parent !is RecyclerView || parent.getChildAdapterPosition(it) != RecyclerView.NO_POSITION
        }
    val topMostChildTop = neighbors
        .minByOrNull { it.top }
        ?.top
        ?: 0
    val bottomMostChildBottom = neighbors
        // не учитываем view, высота которых больше высоты текущего, так как это не имеет смысла и может вызывать ошибки позиционирования
        .filter { it.height <= implantRealHeight }
        .maxByOrNull { it.bottom }
        ?.bottom
        ?: 0

    val parentHeight = Rect()
        .apply { parent.getGlobalVisibleRect(this) }
        .height()
    val neighborsContentHeight = bottomMostChildBottom - topMostChildTop
    val ownContentHeight = implant.bottom - implant.top
    val locateOnBottom = implant.bottom > bottomMostChildBottom
    val totalContentHeight = if (locateOnBottom) {
        neighborsContentHeight + implantRealHeight
    } else {
        neighborsContentHeight
    }
    //TODO https://online.sbis.ru/opendoc.html?guid=dbbf5d8f-773a-4d51-99c3-d27a3cfcd2a3
    // отказаться от INITIAL_HEIGHT_KEY и вести расчет первоначальной высота импланта по чайлдам
    val decorHeightInContent = if (false && implant is ViewGroup) {
        val ownContentHeightByChildren =
            (0 until implant.childCount)
                .map { implant.getChildAt(it).measuredHeight }
                .reduce { acc, i -> acc.plus(i) }
        ownContentHeight - ownContentHeightByChildren
    } else 0

    if (totalContentHeight >= parentHeight) return

    val availableHeight = if (locateOnBottom) {
        parentHeight - parent.paddingBottom - neighborsContentHeight
    } else {
        parentHeight -
                parent.paddingBottom -
                (neighborsContentHeight - implantRealHeight) -
                decorHeightInContent
    }

    // оставляем как есть
    if (abs(availableHeight - implantRealHeight) <= SLOP_HEIGHT) {
        return
    }

    // доступная высота в родителе меньше требуемой и [shouldWrapContentByDefault] истинно,
    // тогда устанавливаем высоту по содержимому
    val suppliedHeight = if (shouldWrapContentByDefault && availableHeight < implantRealHeight) {
        ViewGroup.LayoutParams.WRAP_CONTENT
    }
    // устанавливаем высоту вью по доступной высоте в родителе (ожидаемый результат)
    else {
        val newHeight = availableHeight - SLOP_HEIGHT
        if (implant.getTag(INITIAL_HEIGHT_KEY) == null) {
            implant.setTag(INITIAL_HEIGHT_KEY, implantRealHeight)
        }
        newHeight
    }

    layoutParams = layoutParams.apply {
        height = suppliedHeight
    }
}

private const val PRE_DRAW_DELAY = 20L
private const val ALPHA_IN_DELAY = 200L
private const val LAYOUT_UPDATE_DELAY = 10L
private const val SLOP_HEIGHT = 16
private val INITIAL_HEIGHT_KEY = R.id.initial_height_tag_key