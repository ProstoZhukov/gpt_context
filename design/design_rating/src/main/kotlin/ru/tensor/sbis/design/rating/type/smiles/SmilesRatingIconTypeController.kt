package ru.tensor.sbis.design.rating.type.smiles

import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.rating.api.SBIS_RATING_MAX_RATING
import ru.tensor.sbis.design.rating.api.SBIS_RATING_MIN_RATING
import ru.tensor.sbis.design.rating.api.SbisRatingViewController
import ru.tensor.sbis.design.rating.model.SbisRatingColorsMode
import ru.tensor.sbis.design.rating.model.SbisRatingFilledMode
import ru.tensor.sbis.design.rating.model.SbisRatingIconType
import ru.tensor.sbis.design.rating.model.SbisRatingPrecision
import ru.tensor.sbis.design.rating.type.RatingIconProvider
import ru.tensor.sbis.design.rating.type.RatingIconTypeController
import ru.tensor.sbis.design.theme.global_variables.IconSize

/**
 * @author ps.smirnyh
 */
internal class SmilesRatingIconTypeController(
    controller: SbisRatingViewController,
    override val iconProvider: RatingIconProvider = SmilesRatingIconProvider(controller)
) : RatingIconTypeController {

    override var icons: MutableList<TextLayout> = mutableListOf()

    override var value: Double = SBIS_RATING_MIN_RATING
        set(value) {
            require(value in SBIS_RATING_MIN_RATING..SBIS_RATING_MAX_RATING) {
                "The rating value must be in the range from $SBIS_RATING_MIN_RATING to $maxValue."
            }
            if (field == value) return
            field = value
            iconProvider.updateIcons(icons)
        }

    override var maxValue: Int = SbisRatingIconType.SMILES.emptyIcons.size
        set(_) = Unit // Не поддерживается.

    override var iconSize: IconSize = IconSize.X3L
        set(value) {
            if (field == value) return
            field = if (value <= IconSize.X3L) {
                IconSize.X3L
            } else {
                IconSize.X7L
            }
            iconProvider.updateIconSize(icons)
        }

    override var colorsMode: SbisRatingColorsMode = SbisRatingColorsMode.DYNAMIC
        set(_) = Unit // Не поддерживается.

    override var emptyIconFilledMode: SbisRatingFilledMode = SbisRatingFilledMode.BORDERED
        set(_) = Unit // Не поддерживается.

    override var precision: SbisRatingPrecision = SbisRatingPrecision.FULL
        set(_) = Unit // Не поддерживается.

    init {
        icons.addAll(iconProvider.createIcons())
    }
}