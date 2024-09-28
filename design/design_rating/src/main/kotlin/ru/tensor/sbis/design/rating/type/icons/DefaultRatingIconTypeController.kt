package ru.tensor.sbis.design.rating.type.icons

import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.rating.api.SBIS_RATING_MAX_ICON_COUNT
import ru.tensor.sbis.design.rating.api.SBIS_RATING_MIN_ICON_COUNT
import ru.tensor.sbis.design.rating.api.SBIS_RATING_MIN_RATING
import ru.tensor.sbis.design.rating.api.SbisRatingViewController
import ru.tensor.sbis.design.rating.model.SbisRatingColorsMode
import ru.tensor.sbis.design.rating.model.SbisRatingFilledMode
import ru.tensor.sbis.design.rating.model.SbisRatingPrecision
import ru.tensor.sbis.design.rating.type.RatingIconProvider
import ru.tensor.sbis.design.rating.type.RatingIconTypeController
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.utils.delegateNotEqual

/**
 * @author ps.smirnyh
 */
internal class DefaultRatingIconTypeController(
    controller: SbisRatingViewController,
    override val iconProvider: RatingIconProvider = DefaultRatingIconProvider(controller)
) : RatingIconTypeController {

    override var icons: MutableList<TextLayout> = mutableListOf()
    override var value: Double = SBIS_RATING_MIN_RATING
        set(value) {
            require(value in SBIS_RATING_MIN_RATING..maxValue.toDouble()) {
                "The rating value must be in the range from 0 to iconCount."
            }
            if (field == value) return
            field = value
            iconProvider.updateIcons(icons)
        }

    override var maxValue: Int = SBIS_RATING_MAX_ICON_COUNT.toInt()
        set(value) {
            require(value in SBIS_RATING_MIN_ICON_COUNT..SBIS_RATING_MAX_ICON_COUNT) {
                "The icon count value must be in the range from " +
                    "$SBIS_RATING_MIN_ICON_COUNT to $SBIS_RATING_MAX_ICON_COUNT"
            }
            if (field == value) return
            field = value
            icons.clear()
            icons.addAll(iconProvider.createIcons())
        }

    override var iconSize: IconSize by delegateNotEqual(IconSize.X7L) { _ ->
        iconProvider.updateIconSize(icons)
    }
    override var colorsMode: SbisRatingColorsMode by delegateNotEqual(SbisRatingColorsMode.STATIC) { _ ->
        iconProvider.updateIcons(icons)
    }

    override var emptyIconFilledMode: SbisRatingFilledMode by delegateNotEqual(SbisRatingFilledMode.BORDERED) { _ ->
        iconProvider.updateIcons(icons)
    }

    override var precision: SbisRatingPrecision by delegateNotEqual(SbisRatingPrecision.FULL) { _ ->
        iconProvider.updateIcons(icons)
    }

    init {
        icons.addAll(iconProvider.createIcons())
    }
}