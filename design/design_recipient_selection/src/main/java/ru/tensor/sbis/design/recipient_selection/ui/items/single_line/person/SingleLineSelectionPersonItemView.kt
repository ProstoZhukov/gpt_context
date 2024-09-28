package ru.tensor.sbis.design.recipient_selection.ui.items.single_line.person

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.PaleButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeAtMostSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasure
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredHeight
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredWidth
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design_selection.R
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem

/**
 * View ячейки списка для отображения однострочной информации о персоне в компоненте выбора.
 *
 * @author vv.chekurda
 */
class SingleLineSelectionPersonItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val subtitleEndSpace = Offset.ST.getDimenPx(context)

    /**
     * Фотография персоны.
     */
    val personView = PersonView(context)

    /**
     * Заголовок.
     */
    val titleView = SbisTextView(context)

    /**
     * Подзаголовок.
     */
    val subtitleView = SbisTextView(context)

    /**
     * Иконка выбора.
     */
    val selectionIconView = SbisRoundButton(context)

    init {
        configureLayout()
        if (isInEditMode) showPreview()
    }

    /** @SelfDocumented */
    fun setData(data: SelectionPersonItem) {
        personView.setData(data.photoData)

        titleView.setTextWithHighlightRanges(
            text = data.title,
            positionList = data.titleHighlights.map { IntRange(it.start, it.end - 1) }
        )

        subtitleView.text = data.subtitle
        subtitleView.isVisible = !data.subtitle.isNullOrBlank()
    }

    fun changeSelectButtonVisibility(isVisible: Boolean) {
        selectionIconView.isVisible = isVisible
        subtitleView.updatePadding(right = if (selectionIconView.isVisible) subtitleEndSpace else 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val horizontalPadding = paddingStart + paddingEnd
        val verticalPadding = paddingTop + paddingBottom
        val width = MeasureSpec.getSize(widthMeasureSpec)

        personView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        selectionIconView.safeMeasure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        val availableTitleWidth = MeasureSpec.getSize(widthMeasureSpec)
            .minus(horizontalPadding)
            .minus(personView.measuredWidth)
            .minus(selectionIconView.safeMeasuredWidth)

        titleView.measure(makeAtMostSpec(availableTitleWidth), makeUnspecifiedSpec())

        val availableSubtitleWidth = availableTitleWidth - titleView.measuredWidth
        subtitleView.safeMeasure(makeAtMostSpec(availableSubtitleWidth), makeUnspecifiedSpec())

        val wrappedHeight = maxOf(
            personView.measuredHeight,
            titleView.measuredHeight,
            selectionIconView.safeMeasuredHeight
        ).plus(verticalPadding)

        setMeasuredDimension(width, wrappedHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val availableHeight = measuredHeight - paddingTop - paddingBottom

        val personViewTop = paddingTop + (availableHeight - personView.measuredHeight) / 2
        personView.layout(paddingStart, personViewTop)

        val titleTop = paddingTop + (availableHeight - titleView.measuredHeight) / 2
        titleView.layout(personView.right, titleTop)

        val subtitleTop = titleView.top + titleView.baseline - subtitleView.baseline
        subtitleView.safeLayout(titleView.right, subtitleTop)

        val selectionIconLeft = measuredWidth - paddingEnd - selectionIconView.measuredWidth
        val selectionIconTop = paddingTop + (availableHeight - selectionIconView.measuredHeight) / 2
        selectionIconView.safeLayout(selectionIconLeft, selectionIconTop)
    }

    private fun configureLayout() {
        val containerHorizontalPadding = Offset.XS.getDimenPx(context)
        updatePadding(left = containerHorizontalPadding, right = containerHorizontalPadding)

        personView.apply {
            id = R.id.selection_person_item_photo
            val verticalPadding = Offset.X2S.getDimenPx(context)
            updatePadding(top = verticalPadding, bottom = verticalPadding)
            setSize(PhotoSize.XS)
            setHasActivityStatus(true)
            this@SingleLineSelectionPersonItemView.addView(this)
        }

        titleView.apply {
            id = R.id.selection_person_item_title
            textSize = FontSize.M.getScaleOnDimen(context)
            setTextColor(TextColor.DEFAULT.getValue(context))
            includeFontPadding = false
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            val horizontalPadding = Offset.S.getDimenPx(context)
            val verticalPadding = Offset.ST.getDimenPx(context)
            updatePadding(
                left = horizontalPadding,
                right = horizontalPadding,
                top = verticalPadding,
                bottom = verticalPadding
            )
            this@SingleLineSelectionPersonItemView.addView(this)
        }

        subtitleView.apply {
            id = R.id.selection_person_item_subtitle
            textSize = FontSize.XS.getScaleOnDimen(context)
            setTextColor(StyleColor.UNACCENTED.getTextColor(context))
            includeFontPadding = false
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            updatePadding(right = subtitleEndSpace)
            this@SingleLineSelectionPersonItemView.addView(this)
        }

        selectionIconView.apply {
            id = R.id.selection_person_item_selection_icon_click_area
            style = PaleButtonStyle
            icon = SbisButtonTextIcon(
                fontIcon = SbisMobileIcon.Icon.smi_navBarPlus,
                size = SbisButtonIconSize.XL
            )
            size = SbisRoundButtonSize.XS
            this@SingleLineSelectionPersonItemView.addView(this)
        }
    }

    /**
     * Показать preview в студии.
     */
    private fun showPreview() {
        titleView.text = "Иванов Василий"
        subtitleView.text = "Отдел внутреннего контроля отделов внутреннего контроля"
    }
}
