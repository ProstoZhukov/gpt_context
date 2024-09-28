package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.data

import androidx.annotation.AttrRes
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.R as RDesign

/**
 * Интерфейс иконки по умолчанию для оценки работы оператора.
 *
 * @author dv.baranov
 */
interface CRMRateIconDefault {
    @get:AttrRes
    val defaultColor: Int
        get() = RDesign.attr.unaccentedIconColor
}

/**
 * Базовый класс иконки для оценки работы оператора.
 */
sealed class CRMRateIcon(
    val rateValue: Int = LOWEST_RATING,
    @AttrRes val activeColor: Int = 0,
    val activeIcon: String = StringUtils.EMPTY,
    val defaultIcon: String = StringUtils.EMPTY,
) : CRMRateIconDefault {

    /**
     * Иконка по умолчанию.
     */
    object Default : CRMRateIcon()

    /**
     * Иконка звезды.
     */
    data class Star(val rate: Int) : CRMRateIcon(
        rateValue = rate,
        activeColor = RDesign.attr.rateIconColor,
        activeIcon = SbisMobileIcon.Icon.smi_navBarFavoriteBlack.character.toString(),
        defaultIcon = SbisMobileIcon.Icon.smi_navBarFavorite.character.toString(),
    )

    /**
     * Иконки пальцев.
     */
    object Thumb {

        /** @SelfDocumented */
        object Like : CRMRateIcon(
            rateValue = 5,
            activeColor = RDesign.attr.successIconColor,
            activeIcon = SbisMobileIcon.Icon.smi_Like.character.toString(),
            defaultIcon = SbisMobileIcon.Icon.smi_LikeNull.character.toString(),
        )

        /** @SelfDocumented */
        object DisLike : CRMRateIcon(
            activeColor = RDesign.attr.dangerIconColor,
            activeIcon = SbisMobileIcon.Icon.smi_disLike.character.toString(),
            defaultIcon = SbisMobileIcon.Icon.smi_disLikeNull.character.toString(),
        )
    }

    /**
     * Иконки смайлов.
     */
    object Emoji {

        /** @SelfDocumented */
        object Smile : CRMRateIcon(
            rateValue = 5,
            activeColor = RDesign.attr.successIconColor,
            activeIcon = SbisMobileIcon.Icon.smi_EmoiconSmileInvert.character.toString(),
            defaultIcon = SbisMobileIcon.Icon.smi_EmoiconSmile.character.toString(),
        )

        /** @SelfDocumented */
        object Neutral : CRMRateIcon(
            rateValue = 3,
            activeColor = RDesign.attr.warningIconColor,
            activeIcon = SbisMobileIcon.Icon.smi_EmoiconNeutralInvert.character.toString(),
            defaultIcon = SbisMobileIcon.Icon.smi_EmoiconNeutral.character.toString(),
        )

        /** @SelfDocumented */
        object Annoyed : CRMRateIcon(
            activeColor = RDesign.attr.dangerIconColor,
            activeIcon = SbisMobileIcon.Icon.smi_EmoiconAnnoyedInvert.character.toString(),
            defaultIcon = SbisMobileIcon.Icon.smi_EmoiconAnnoyed.character.toString(),
        )
    }
}

internal const val LOWEST_RATING = 1
