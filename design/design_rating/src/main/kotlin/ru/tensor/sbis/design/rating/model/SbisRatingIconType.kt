package ru.tensor.sbis.design.rating.model

import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.rating.api.SbisRatingViewController
import ru.tensor.sbis.design.rating.type.RatingIconTypeController
import ru.tensor.sbis.design.rating.type.icons.DefaultRatingIconTypeController
import ru.tensor.sbis.design.rating.type.smiles.SmilesRatingIconTypeController
import ru.tensor.sbis.design.rating.type.thumbs.ThumbsRatingIconTypeController

/**
 * Иконки, которые будут отображаться в рейтинге.
 *
 * @author ps.smirnyh
 */
enum class SbisRatingIconType(
    internal val emptyIcons: List<CharSequence>,
    internal val halfFilledIcons: List<CharSequence> = emptyList(),
    internal val filledIcons: List<CharSequence> = emptyList()
) {

    /** @SelfDocumented */
    STARS(
        emptyIcons = listOf(SbisMobileIcon.Icon.smi_SwipeStar.character.toString()),
        halfFilledIcons = listOf(SbisMobileIcon.Icon.smi_SwipeHalfStar.character.toString()),
        filledIcons = listOf(SbisMobileIcon.Icon.smi_SwipeFloodestar.character.toString())
    ) {
        override fun getIconTypeController(
            controller: SbisRatingViewController
        ): RatingIconTypeController = DefaultRatingIconTypeController(controller)

    },

    /** @SelfDocumented */
    HEARTS(
        emptyIcons = listOf(SbisMobileIcon.Icon.smi_SwipeFavorites.character.toString()),
        halfFilledIcons = listOf(SbisMobileIcon.Icon.smi_SwipeHalfFavorites.character.toString()),
        filledIcons = listOf(SbisMobileIcon.Icon.smi_SwipeFavoritesFilled.character.toString())
    ) {
        override fun getIconTypeController(
            controller: SbisRatingViewController
        ): RatingIconTypeController = DefaultRatingIconTypeController(controller)
    },

    SMILES(
        emptyIcons = listOf(
            SbisMobileIcon.Icon.smi_EmoiconAnnoyed.character.toString(),
            SbisMobileIcon.Icon.smi_EmoiconNeutral.character.toString(),
            SbisMobileIcon.Icon.smi_EmoiconSmile.character.toString()
        )
    ) {
        override fun getIconTypeController(
            controller: SbisRatingViewController
        ): RatingIconTypeController = SmilesRatingIconTypeController(controller)
    },

    THUMBS(
        emptyIcons = listOf(
            SbisMobileIcon.Icon.smi_disLikeNull.character.toString(),
            SbisMobileIcon.Icon.smi_ThumbUp2.character.toString()

        )
    ) {
        override fun getIconTypeController(
            controller: SbisRatingViewController
        ): RatingIconTypeController = ThumbsRatingIconTypeController(controller)
    };

    /** Получить класс для управления логикой компонента, в зависимости от типа иконок. */
    internal abstract fun getIconTypeController(
        controller: SbisRatingViewController
    ): RatingIconTypeController
}