package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui.rate_icons_view

import android.content.Context
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.data.CRMRateIcon
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ConsultationRateType
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.EmojiType
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.StarType
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ThumbType

/**
 * Фабрика создания RateIconsView в зависимости от типа оценки (звезды, смайлы или пальцы).
 *
 * @author dv.baranov
 */
internal class RateIconsViewFactoryImpl(
    private val context: Context,
    private val onRateIconClick: (rateIndex: Int) -> Unit,
) : RateIconsViewFactory {

    override fun createIconsView(
        consultationRateType: ConsultationRateType,
    ): RateIconsView = RateIconsView(
        context,
        onRateIconClick,
        when (consultationRateType) {
            is EmojiType -> listOf(
                CRMRateIcon.Emoji.Smile,
                CRMRateIcon.Emoji.Neutral,
                CRMRateIcon.Emoji.Annoyed,
            )
            is ThumbType -> listOf(
                CRMRateIcon.Thumb.Like,
                CRMRateIcon.Thumb.DisLike,
            )
            is StarType -> emptyList()
        },
    )
}
