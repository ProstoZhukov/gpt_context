package ru.tensor.sbis.design.rating.type.icons

import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.rating.R
import ru.tensor.sbis.design.rating.api.SBIS_RATING_MIN_RATING
import ru.tensor.sbis.design.rating.api.SbisRatingViewController
import ru.tensor.sbis.design.rating.model.SbisRatingFilledMode
import ru.tensor.sbis.design.rating.model.SbisRatingPrecision
import ru.tensor.sbis.design.rating.type.RatingIconProvider
import ru.tensor.sbis.design.rating.utils.RatingIconModel
import ru.tensor.sbis.design.rating.utils.RatingStyleHolder
import ru.tensor.sbis.design.rating.utils.RatingTextLayoutFactory
import ru.tensor.sbis.design.rating.utils.getOffsetIcon
import ru.tensor.sbis.design.theme.global_variables.Offset
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Класс для предоставления иконки в зависимости от параметров рейтинга.
 *
 * @author ps.smirnyh
 */
internal class DefaultRatingIconProvider(
    private val controller: SbisRatingViewController,
    private val iconFactory: RatingTextLayoutFactory = RatingTextLayoutFactory()
) : RatingIconProvider {

    private val styleHolder: RatingStyleHolder
        get() = controller.styleHolder

    private val isAllowUseHalfIcon: Boolean
        get() = controller.precision == SbisRatingPrecision.HALF

    @Px
    private var iconSizePx: Int = 0

    @get:Px
    private val touchPadding: Int by lazy {
        Offset.XS.getDimenPx(controller.ratingView.context)
    }

    /** Цвет залитой иконки. */
    @ColorInt
    internal var filledIconColor: Int = 0

    /** Цвет пустой иконки. */
    @ColorInt
    internal var emptyIconColor: Int = 0

    /** Отступ между иконками в px. */
    @Px
    override var iconsOffset: Int = 0

    /** @SelfDocumented */
    override fun updateIcons(icons: List<TextLayout>) {
        filledIconColor = controller.colorsMode.getFilledIconColor(
            if (isAllowUseHalfIcon) {
                controller.value.roundToInt()
            } else {
                controller.value.toInt()
            },
            styleHolder
        )
        emptyIconColor = styleHolder.emptyIconColor
        icons.forEachIndexed { index, textLayout ->
            val iconModel = getIcon(index, controller.value)
            textLayout.configure {
                text = iconModel.icon
                paint.color = iconModel.color
                paint.textSize = iconSizePx.toFloat()
                padding = TextLayout.TextLayoutPadding(
                    if (iconsOffset == 0) {
                        touchPadding
                    } else {
                        0
                    }
                )
            }
        }
    }

    /** @SelfDocumented */
    override fun updateIconSize(icons: List<TextLayout>) {
        iconSizePx = controller.iconSize.getDimenPx(controller.ratingView.context)
        iconsOffset = controller.iconSize.getOffsetIcon()?.getDimenPx(controller.ratingView.context) ?: 0
        updateIcons(icons)
    }

    /** @SelfDocumented */
    override fun createIcons(): List<TextLayout> {
        val icons = mutableListOf<TextLayout>()
        for (index in 0 until controller.maxValue) {
            icons.add(
                createIcon().apply {
                    id = R.id.rating_icon
                    makeClickable(controller.ratingView)
                    setOnClickListener { _, _ ->
                        val newRating = index.toDouble() + 1
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
        val countFilledIcons = floor(currentRating)
        if (index < countFilledIcons) {
            return RatingIconModel(
                icon = controller.iconType.filledIcons.first(),
                color = filledIconColor
            )
        }
        if (index > countFilledIcons) {
            return RatingIconModel(
                icon = getEmptyIcon(),
                color = emptyIconColor
            )
        }
        val fractionPart = currentRating % 1
        return getCurrentRatingIcon(fractionPart)
    }

    private fun getCurrentRatingIcon(fractionPartRating: Double): RatingIconModel =
        if (isAllowUseHalfIcon && fractionPartRating >= 0.5) {
            RatingIconModel(
                icon = controller.iconType.halfFilledIcons.first(),
                color = filledIconColor
            )
        } else {
            RatingIconModel(
                icon = getEmptyIcon(),
                color = emptyIconColor
            )
        }

    private fun getEmptyIcon(): CharSequence =
        if (controller.emptyIconFilledMode == SbisRatingFilledMode.FILLED) {
            controller.iconType.filledIcons.first()
        } else {
            controller.iconType.emptyIcons.first()
        }
}