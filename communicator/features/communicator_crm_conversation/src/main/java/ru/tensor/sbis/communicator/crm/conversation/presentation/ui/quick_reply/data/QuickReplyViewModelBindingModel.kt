package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data

import android.util.TypedValue
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.consultations.generated.QuickReplySearchResult
import ru.tensor.sbis.design.custom_view_tools.utils.HighlightSpan
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.extentions.setLeftPadding
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVm
import java.util.UUID
import ru.tensor.sbis.design.R as RDesign

/**
 * UI модель быстрых ответов.
 *
 * @property id идентификатор объекта.
 * @property text текст для отображения.
 * @property isGroup признак группы.
 * @property isPinned признак того, что текст выделен в списке. Для закрепленных быстрых ответов.
 * @property isSeparator признак того, что после элемента нужно отрисовать линию-разделитель.
 * @property isTitle признак того, что элемент нужно отобразить как родительский элемент при поиске.
 * @property searchResult результат поиска.
 * @property path путь для найденного элемента. Задан только у моделей, возвращаемых как результат поиска.
 * @property needMediumFont true, если для данного элемента списка текст должен быть medium стиля.
 *
 * @author dv.baranov
 */
internal data class QuickReplyViewModelBindingModel(
    val id: UUID,
    val text: String,
    val isGroup: Boolean,
    val isPinned: Boolean,
    val isSeparator: Boolean,
    val isTitle: Boolean,
    val searchResult: QuickReplySearchResult?,
    val path: List<String>,
    val needMediumFont: Boolean,
    val onItemClick: () -> Unit,
) : ComparableItem<QuickReplyViewModelBindingModel> {

    /** @SelfDocumented */
    lateinit var swipeableVm: SwipeableVm
    override fun areTheSame(otherItem: QuickReplyViewModelBindingModel): Boolean = id == otherItem.id

    /** @SelfDocumented */
    fun onClick() { onItemClick() }
}

@BindingAdapter("setQuickReplyFontFamily")
internal fun SbisTextView.setQuickReplyFontFamily(needMediumFont: Boolean) {
    setTextAppearance(if (needMediumFont) RDesign.style.MediumStyle else RDesign.style.RegularStyle)
}

@BindingAdapter("setSearchHighlight")
internal fun SbisTextView.setSearchHighlight(searchResult: QuickReplySearchResult?) {
    searchResult?.let {
        val searchColor = context.getThemeColorInt(RDesign.attr.textBackgroundColorDecoratorHighlight)
        val textHighlights = TextHighlights(listOf(HighlightSpan(it.start, it.end)), searchColor)
        setTextWithHighlights(this.text, textHighlights)
    } ?: setTextWithHighlights(this.text, null)
}

@BindingAdapter("setQuickReplyTextStyle")
internal fun SbisTextView.setQuickReplyTextStyle(isTitleStyle: Boolean) {
    val color = if (isTitleStyle) TextColor.LABEL_CONTRAST else TextColor.DEFAULT
    val fontSize = if (isTitleStyle) FontSize.XS else FontSize.L
    setTextColor(color.getValue(context))
    setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize.getScaleOffDimen(context))
}

@BindingAdapter("setQuickReplyLeftPadding")
internal fun SbisTextView.setQuickReplyLeftPadding(itemLevel: Int) {
    val itemHasParentFolder = itemLevel > 1
    setLeftPadding(if (itemHasParentFolder) Offset.XL.getDimenPx(context) else 0)
}
