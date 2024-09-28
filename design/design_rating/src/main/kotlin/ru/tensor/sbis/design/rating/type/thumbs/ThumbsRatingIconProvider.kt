package ru.tensor.sbis.design.rating.type.thumbs

import androidx.annotation.Px
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.rating.R
import ru.tensor.sbis.design.rating.api.SBIS_RATING_MIN_RATING
import ru.tensor.sbis.design.rating.api.SbisRatingViewController
import ru.tensor.sbis.design.rating.type.RatingIconProvider
import ru.tensor.sbis.design.rating.utils.RatingIconModel
import ru.tensor.sbis.design.rating.utils.RatingTextLayoutFactory
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * Класс для предоставления иконки в зависимости от параметров рейтинга.
 *
 * @author ps.smirnyh
 */
internal class ThumbsRatingIconProvider(
    private val controller: SbisRatingViewController,
    private val iconFactory: RatingTextLayoutFactory = RatingTextLayoutFactory()
) : RatingIconProvider {

    @Px
    private var iconSizePx: Int = 0

    /** Отступ между иконками в px. */
    @Px
    override var iconsOffset: Int = 0

    /** @SelfDocumented */
    override fun updateIcons(icons: List<TextLayout>) {
        icons.forEachIndexed { index, textLayout ->
            val iconModel = getIcon(index, controller.value)
            textLayout.configure {
                text = iconModel.icon
                paint.color = iconModel.color
                paint.textSize = iconSizePx.toFloat()
            }
        }
    }

    /** @SelfDocumented */
    override fun updateIconSize(icons: List<TextLayout>) = with(controller.currentIconTypeController) {
        iconSizePx = iconSize.getDimenPx(controller.ratingView.context)
        iconsOffset = iconSize.getOffsetIcon().getDimenPx(controller.ratingView.context)
        updateIcons(icons)
    }

    /** @SelfDocumented */
    override fun createIcons(): List<TextLayout> {
        val icons = mutableListOf<TextLayout>()
        for (index in controller.iconType.emptyIcons.indices) {
            icons.add(
                createIcon().apply {
                    id = R.id.rating_icon
                    makeClickable(controller.ratingView)
                    setOnClickListener { _, _ ->
                        val newRating = getRatingByIconIndex(index)
                        if (newRating == controller.value && !controller.allowUserToResetRating) {
                            return@setOnClickListener
                        }
                        controller.value = if (newRating == controller.value) {
                            SBIS_RATING_MIN_RATING
                        } else {
                            newRating
                        }
                        controller.onRatingSelected?.invoke(controller.value)
                    }
                }
            )
        }
        updateIconSize(icons)
        return icons
    }

    private fun createIcon(): TextLayout {
        return iconFactory.create {
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(controller.ratingView.context)
        }
    }

    /**
     * Получить модель иконки рейтинга.
     *
     * @param index индекс в рейтинге для отображения иконки.
     * @param currentRating текущее значение рейтинга.
     */
    private fun getIcon(index: Int, currentRating: Double): RatingIconModel {
        val indexSelectedIcon = getIconIndexByRating(currentRating)
        val color = if (indexSelectedIcon == 0) {
            controller.styleHolder.otherIconColors.first()
        } else {
            controller.styleHolder.otherIconColors.last()
        }
        return if (index == indexSelectedIcon) {
            RatingIconModel(
                icon = controller.iconType.emptyIcons[index],
                color = color
            )
        } else {
            RatingIconModel(
                icon = controller.iconType.emptyIcons[index],
                color = controller.styleHolder.emptyIconColor
            )
        }
    }

    /*
    Значения для маппинга взяты из ТЗ по проекту.
    https://project.sbis.ru/uuid/d89fdafc-aad6-4575-84ae-a121cc2e690b/page/project-main
    */
    private fun getIconIndexByRating(rating: Double) = when {
        rating in 1.0..3.0 -> 0
        rating > 3.0 -> 1
        else -> -1
    }

    /*
    Значения для маппинга взяты из ТЗ по проекту.
    https://project.sbis.ru/uuid/d89fdafc-aad6-4575-84ae-a121cc2e690b/page/project-main
    */
    private fun getRatingByIconIndex(iconIndex: Int) = when (iconIndex) {
        0 -> 1.0
        1 -> 5.0
        else -> error("Unexpected rating icon index $iconIndex")
    }

    private fun IconSize.getOffsetIcon(): Offset = when {
        this <= IconSize.X3L -> Offset.ST
        else -> Offset.M
    }

}