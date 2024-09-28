/**
 * Переиспользуемые инструменты для конфигурации стандартного фона и отступов ячеек.
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.list.view.decorator

import android.graphics.drawable.GradientDrawable
import android.view.View
import ru.tensor.sbis.design.util.dpToDimension
import ru.tensor.sbis.design.util.dpToPx
import ru.tensor.sbis.list.R
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.background.ColorProvider
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.section.SingleCard
import ru.tensor.sbis.list.view.utils.ProgressItem

/** @SelfDocumented */
internal fun configureItemBackground(
    view: View,
    position: Int,
    colorProvider: ColorProvider,
    sectionsHolder: ListDataHolder
) {
    if (hasCustomBackground(position, sectionsHolder)) return

    val background = getOrInitBackground(view, colorProvider)

    if (sectionsHolder.isCard(position)) {
        val standardCornerRadius = view.context.dpToDimension(STANDARD_CORNER_RADIUS_DP)
        val cardOption = sectionsHolder.getCardOption(position)
        if (cardOption is SingleCard) {
            val isFirstInSection = sectionsHolder.isFirstInSection(position)
            val isLastItemInSection = sectionsHolder.isLastItemInSection(position)
            background.cornerRadii = with(cardOption.cardCorners) {
                getRadii(
                    standardCornerRadius,
                    isFirstInSection && roundTopLeft,
                    isFirstInSection && roundTopRight,
                    isLastItemInSection && roundBottomLeft,
                    isLastItemInSection && roundBottomRight
                )
            }
        } else {
            background.cornerRadius = standardCornerRadius
        }
    }
}

/** @SelfDocumented */
internal fun configureItemPadding(view: View, position: Int, sectionsHolder: ListDataHolder) {
    val item = sectionsHolder.getItems()[position]
    if (item.customSidePadding) return
    view.setPadding(
        getLeftPadding(view, item),
        getTopPadding(view, item),
        getSidePadding(view, item),
        getSidePadding(view, item)
    )
}

private fun getOrInitBackground(view: View, colorProvider: ColorProvider): GradientDrawable {
    return (view.background as? GradientDrawable?) ?: GradientDrawable().apply {
        color = colorProvider.itemColorStateList
        view.background = this
    }
}

private fun hasCustomBackground(position: Int, sectionsHolder: ListDataHolder): Boolean {
    val item = sectionsHolder.getItems().getOrNull(position)
        ?: return true
    return item.customBackground || item is ProgressItem
}

private fun getRadii(
    standardCornerRadius: Float,
    roundLeftTop: Boolean,
    roundRightTop: Boolean,
    roundLeftBottom: Boolean,
    roundRightBottom: Boolean
): FloatArray {
    val topLeft = if (roundLeftTop) standardCornerRadius else 0f
    val topRight = if (roundRightTop) standardCornerRadius else 0f
    val bottomLeft = if (roundLeftBottom) standardCornerRadius else 0f
    val bottomRight = if (roundRightBottom) standardCornerRadius else 0f

    return floatArrayOf(topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft)
}

private fun getTopPadding(view: View, item: AnyItem) =
    view.context.dpToPx(item.getTopPaddingDp())

private fun getLeftPadding(view: View, item: AnyItem) : Int {
    return view.context.dpToPx(
        item.getLeftPaddingDp()
    ) + item.level * getLevelPadding(view)
}

private fun getSidePadding(
    view: View,
    item: AnyItem
) = view.context.dpToPx(item.getSidePaddingDp())

private fun getLevelPadding(view: View) =
    view.resources.getDimensionPixelOffset(R.dimen.list_item_level_padding)

/** @SelfDocumented */
const val STANDARD_CORNER_RADIUS_DP = 16