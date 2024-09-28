package ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout

import android.content.Context
import ru.tensor.sbis.design.message_panel.audio_recorder.R
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.AudioRecordSendView
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.R as RDesign

/**
 * Модель с размерами для внутренней разметки компонента отправки аудиосообщения.
 * @see AudioRecordSendView
 *
 * @author vv.chekurda
 */
internal data class AudioRecordSendDimens(
    val fieldHeight: Int,
    val fieldVerticalMargin: Int,
    val fieldMarginStart: Int,
    val playerMinWidth: Int,
    val smileSize: Int,
    val smileSpacing: Int,
    val smilesHorizontalPadding: Int,
    val sendButtonHorizontalMargin: Int,
    val sendButtonSize: Int
) {
    companion object {
        fun create(context: Context) = with(context) {
            val sendButtonSize = InlineHeight.X3S.getDimenPx(context)
            AudioRecordSendDimens(
                fieldHeight = context.getDimenPx(RDesign.attr.inlineHeight_2xs),
                fieldVerticalMargin = context.getDimenPx(RDesign.attr.offset_xs),
                fieldMarginStart = context.getDimenPx(RDesign.attr.offset_m),
                playerMinWidth = resources.getDimensionPixelSize(R.dimen.design_message_panel_audio_recorder_player_min_width),
                smileSize = resources.getDimensionPixelSize(R.dimen.design_message_panel_audio_recorder_smile_size),
                smileSpacing = resources.getDimensionPixelSize(R.dimen.design_message_panel_audio_recorder_smile_spacing),
                smilesHorizontalPadding = sendButtonSize / 2,
                sendButtonHorizontalMargin = context.getDimenPx(RDesign.attr.offset_xs),
                sendButtonSize = sendButtonSize
            )
        }
    }
}