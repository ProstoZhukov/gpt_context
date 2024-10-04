package ru.tensor.sbis.design.tabs.tabItem

import android.graphics.Canvas
import android.view.View
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import ru.tensor.sbis.design.tabs.util.SbisTabItemContentViewInflater
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import java.util.LinkedList

/**
 * Класс для работы со списком [ContentView].
 *
 * @author da.zolotarev
 */
internal class ContentHolder(
    model: SbisTabsViewItem,
    mapper: SbisTabItemContentViewInflater,
    private val styleHolder: SbisTabItemStyleHolder
) {

    private var scope: CoroutineScope? = null

    /**
     * @SelfDocumented
     */
    private val isMainTab = model.isMain

    /**
     * ID вкладки, взятый из ее модели [SbisTabsViewItem.id].
     */
    var tabId: String? = null

    /**
     * NavxID вкладки, взятый из ее модели [SbisTabsViewItem.navxId].
     */
    var navxId: NavxIdDecl? = null

    @VisibleForTesting
    internal val content: LinkedList<ContentView> = LinkedList()

    init {
        tabId = model.id
        navxId = model.navxId
        model.content.forEach { content.add(mapper.inflate(it, model.customTitleColor, model.customIconColor)) }
    }

    /**
     * Обновить стили элементов [content].
     */
    fun updateStyleHolder() {
        content.forEach { it.updateStyleHolder() }
    }

    /**
     * Получить максимальный baseline из [content].
     */
    fun getContentItemMaxBaseline(): Int {
        var baseline = 0
        content.filterIsInstance<ContentView.Text>().forEach {
            baseline = maxOf(baseline, it.textLayout.baseline)
        }
        content.filterIsInstance<ContentView.AdditionalText>().forEach {
            baseline = maxOf(baseline, it.textLayout.baseline)
        }
        return baseline
    }

    /**
     * Получить максимальную высоту элемента [content].
     */
    fun getContentItemMaxHeight(): Int = content.maxByOrNull { it.getHeight() }?.getHeight() ?: 0

    /**
     * Отрисовать [content].
     */
    fun drawContent(canvas: Canvas) {
        content.forEach {
            it.onDraw(canvas)
        }
    }

    /**
     * Поменять состояние выбранности [content].
     */
    fun setSelected(isSelected: Boolean) {
        content.forEach { it.setSelected(isSelected) }
    }

    /**
     * Отмерить [content] и получить ширину.
     */
    fun measureContentAndReturnWidth(): Int {
        var widthOffset = styleHolder.horizontalPadding

        // Ищем самый большой элемент.
        val height = getContentItemMaxHeight()

        content.forEachIndexed { index, contentItem ->
            contentItem.onMeasure(widthOffset, height)
            widthOffset += contentItem.getWidth()

            // Между счётчиком и иконкой отступ отличается.
            if (index == content.lastIndex) return@forEachIndexed
            widthOffset += if (content.lastIndex != index &&
                (content[index] is ContentView.Icon && content[index + 1] is ContentView.Counter)
            ) {
                styleHolder.itemContentOffsetSmall
            } else {
                styleHolder.itemContentOffset
            }
        }
        return widthOffset
    }

    /**
     * Получить описание контента вкладки.
     */
    fun getContentDescription(): String {
        val output = StringBuilder("IsMain: $isMainTab\n")
        content.forEach {
            output.append("${it.getContentDescription()}\n")
        }
        return output.toString()
    }

    /**
     * Выполнить действия над [content] на методе [View.onAttachedToWindow]
     */
    fun onAttachedToWindow() {
        scope = CoroutineScope(Dispatchers.Main)
        content.forEach {
            it.onAttachedToWindow(scope)
        }
    }

    /**
     * Выполнить действие на методе [View.onDetachedFromWindow]
     */
    fun onDetachedFromWindow() {
        scope?.cancel()
        scope = null
    }
}