package ru.tensor.sbis.design.selection.ui.list.items.single

import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.common_views.HighlightedTextView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.toSearchSpanList
import androidx.core.view.isVisible

internal const val MAX_LINES_DEFAULT = 1

/**
 * Максимальное количество строк для заголовка. Нереалистичное число с очень большим запасом.
 * [Int.MAX_VALUE] может создавать непредвиденное поведение, например в `HighlightedTextView`
 * из-за переполнения типа при сложении\умножении будет отображаться elliipsis, когда не надо.
 */
internal const val MAX_TITLE_LINES = 1000

/**
 * Реализация [RecyclerView.ViewHolder] для работы с базовыми параметрами [SelectorItemModel]
 *
 * @author ma.kolpakov
 */
internal open class DefaultSingleSelectorViewHolder<DATA : SelectorItemModel>(
    view: View,
) : RecyclerView.ViewHolder(view) {

    private val title = view.findViewById<HighlightedTextView>(R.id.title)
    private val subtitle = view.findViewById<TextView>(R.id.subtitle)

    /**
     * Данные, которые отображаются во вьюхолдере
     */
    protected lateinit var data: DATA
        private set

    /**
     * Установка данных из [SelectorItemModel] и отметка выбора для элемента
     */
    @CallSuper
    open fun bind(data: DATA) {
        this.data = data

        title.maxLines = getTitleMaxLines()
        title.setTextWithHighlight(data.title, data.meta.queryRanges.toSearchSpanList())
        subtitle.text = getSubtitle(data)
        subtitle.isVisible = isSubtitleVisible(data)
    }

    /**
     * Получение текста подзаголовка.
     */
    protected open fun getSubtitle(data: DATA): String? = data.subtitle

    /**
     * Определяет максимальное количество строк в заголовке в зависимости от правил того или иного стандарта.
     * Не использовать [Int.MAX_VALUE], вместо этого есть значение [MAX_TITLE_LINES]
     */
    protected open fun getTitleMaxLines(): Int = MAX_LINES_DEFAULT

    /**
     * Правила отображения подзаголовка
     */
    protected open fun isSubtitleVisible(data: DATA): Boolean =
        !data.subtitle.isNullOrEmpty()
}
