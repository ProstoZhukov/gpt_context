package ru.tensor.sbis.design.recipient_selection.ui.items.single_line.folder

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
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
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design_selection.R
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem

/**
 * View ячейки списка для отображения однострочной информации о папке в компоненте выбора.
 *
 * @author vv.chekurda
 */
class SingleLineSelectionFolderItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * Иконка папки.
     */
    val folderIcon = SbisTextView(context)

    /**
     * Заголовок.
     */
    val titleView = SbisTextView(context)

    /**
     * Подзаголовок.
     */
    val subtitleView = SbisTextView(context)

    /**
     * Иконка возможности проваливания в папку.
     */
    val fallInsideIcon = SbisTextView(context)

    /**
     * Иконка выбора.
     */
    val selectionIconView = SbisRoundButton(context)

    init {
        configureLayout()
        if (isInEditMode) showPreview()
    }

    /** @SelfDocumented */
    fun setData(data: SelectionFolderItem) {
        titleView.setTextWithHighlightRanges(
            text = data.title,
            positionList = data.titleHighlights.map { IntRange(it.start, it.end - 1) }
        )

        subtitleView.text = data.subtitle
        subtitleView.isVisible = !data.subtitle.isNullOrBlank()

        selectionIconView.isVisible = data.selectable
        fallInsideIcon.isVisible = data.openable
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val horizontalPadding = paddingStart + paddingEnd
        val verticalPadding = paddingTop + paddingBottom
        val width = MeasureSpec.getSize(widthMeasureSpec)

        folderIcon.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        selectionIconView.safeMeasure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        fallInsideIcon.safeMeasure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        val availableTitleWidth = MeasureSpec.getSize(widthMeasureSpec)
            .minus(horizontalPadding)
            .minus(folderIcon.measuredWidth)
            .minus(selectionIconView.safeMeasuredWidth)
            .minus(fallInsideIcon.safeMeasuredWidth)

        titleView.measure(makeAtMostSpec(availableTitleWidth), makeUnspecifiedSpec())

        val availableSubtitleWidth = availableTitleWidth - titleView.measuredWidth
        subtitleView.safeMeasure(makeAtMostSpec(availableSubtitleWidth), makeUnspecifiedSpec())

        val wrappedHeight = maxOf(
            folderIcon.measuredHeight,
            titleView.measuredHeight,
            selectionIconView.safeMeasuredHeight
        ).plus(verticalPadding)

        setMeasuredDimension(width, wrappedHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val availableHeight = measuredHeight - paddingTop - paddingBottom

        val folderIconTop = paddingTop + (availableHeight - folderIcon.measuredHeight) / 2
        folderIcon.layout(paddingStart, folderIconTop)

        val titleTop = paddingTop + (availableHeight - titleView.measuredHeight) / 2
        titleView.layout(folderIcon.right, titleTop)

        val subtitleTop = titleView.top + titleView.baseline - subtitleView.baseline
        subtitleView.safeLayout(titleView.right, subtitleTop)

        val fallInsideIconTop = titleView.top + titleView.baseline - fallInsideIcon.baseline
        fallInsideIcon.safeLayout(subtitleView.right, fallInsideIconTop)

        val selectionIconLeft = measuredWidth - paddingEnd - selectionIconView.measuredWidth
        val selectionIconTop = paddingTop + (availableHeight - selectionIconView.measuredHeight) / 2
        selectionIconView.safeLayout(selectionIconLeft, selectionIconTop)
    }

    private fun configureLayout() {
        val containerHorizontalPadding = Offset.XS.getDimenPx(context)
        updatePadding(left = containerHorizontalPadding, right = containerHorizontalPadding)

        folderIcon.apply {
            id = R.id.selection_folder_icon
            val verticalPadding = Offset.X2S.getDimenPx(context)
            text = SbisMobileIcon.Icon.smi_folderBlack.character.toString()
            textSize = IconSize.X3L.getDimen(context)
            setTextColor(StyleColor.UNACCENTED.getIconColor(context))
            setTypeface(TypefaceManager.getSbisMobileIconTypeface(context), Typeface.DEFAULT.style)
            updatePadding(top = verticalPadding, bottom = verticalPadding)
            this@SingleLineSelectionFolderItemView.addView(this)
        }

        titleView.apply {
            id = R.id.selection_folder_title
            textSize = FontSize.M.getScaleOnDimen(context)
            paint.color = TextColor.DEFAULT.getValue(context)
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
            this@SingleLineSelectionFolderItemView.addView(this)
        }

        subtitleView.apply {
            id = R.id.selection_folder_subtitle
            textSize = FontSize.XS.getScaleOnDimen(context)
            setTextColor(StyleColor.UNACCENTED.getTextColor(context))
            includeFontPadding = false
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            updatePadding(right = Offset.S.getDimenPx(context))
            this@SingleLineSelectionFolderItemView.addView(this)
        }

        fallInsideIcon.apply {
            id = R.id.selection_folder_fall_inside_icon
            typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            text = SbisMobileIcon.Icon.smi_ArrowNarrowRight.character.toString()
            textSize = IconSize.S.getDimen(context)
            setTextColor(IconColor.LABEL.getValue(context))
            this@SingleLineSelectionFolderItemView.addView(this)
        }

        selectionIconView.apply {
            id = R.id.selection_person_item_selection_icon_click_area
            style = PaleButtonStyle
            icon = SbisButtonTextIcon(
                fontIcon = SbisMobileIcon.Icon.smi_navBarPlus,
                size = SbisButtonIconSize.XL
            )
            size = SbisRoundButtonSize.XS
            this@SingleLineSelectionFolderItemView.addView(this)
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
