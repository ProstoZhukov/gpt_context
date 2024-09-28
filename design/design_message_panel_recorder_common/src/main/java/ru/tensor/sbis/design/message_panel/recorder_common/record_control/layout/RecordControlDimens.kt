package ru.tensor.sbis.design.message_panel.recorder_common.record_control.layout

import android.content.Context
import ru.tensor.sbis.design.message_panel.recorder_common.R
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.IGNORE_HORIZONTAL_MOVEMENT_DISTANCE_PERCENT
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon
import ru.tensor.sbis.design.R as RDesign

/**
 * Модель с размерами для внутренней разметки панели управления записи аудио/видео сообщений.
 * @see RecordControlLayout
 *
 * @author vv.chekurda
 */
internal data class RecordControlDimens(
    val backgroundFieldHorizontalMargin: Int,
    val backgroundFieldVerticalMargin: Int,
    val backgroundFieldHeight: Int,
    val dividerHeight: Int,
    val recordIndicatorSize: Int,
    val recordIndicatorPaddingStart: Int,
    val recordIndicatorPaddingTop: Int,
    val recordIndicatorPaddingEnd: Int,
    val timerTextPaddingBottom: Int,
    val hintSpacingCenterLeft: Int,
    val minHintSpacingStart: Int,
    val hintAlignLeftSpacingStart: Int,
    val lockViewSpacingBottom: Int,
    val lockViewMarginHorizontal: Int,
    val lockViewHideDistance: Int,
    val lockRecordDistance: Int,
    val attachButtonTextSize: Int,
    val attachButtonPaddingBottom: Int,
    val attachButtonHorizontalPadding: Int,
    val sendButtonHorizontalSpacing: Int,
    val cancelIgnoreDx: Float
) {
    companion object {

        /**
         * Создать модель с размерами для внутренней разметки панели управления записи аудио/видео сообщений.
         */
        fun create(context: Context) = with(context) {
            RecordControlDimens(
                backgroundFieldHorizontalMargin = getDimenPx(RDesign.attr.offset_m),
                backgroundFieldVerticalMargin = getDimenPx(RDesign.attr.offset_xs),
                backgroundFieldHeight = getDimenPx(RDesign.attr.inlineHeight_2xs),
                dividerHeight = resources.getDimensionPixelSize(RMPCommon.dimen.design_message_panel_common_top_divider_height),
                recordIndicatorSize = getDimenPx(RDesign.attr.offset_xs),
                recordIndicatorPaddingStart = getDimenPx(RDesign.attr.offset_m),
                recordIndicatorPaddingTop = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_indicator_padding_top),
                recordIndicatorPaddingEnd = getDimenPx(RDesign.attr.offset_s),
                timerTextPaddingBottom = getDimenPx(RDesign.attr.offset_m),
                hintSpacingCenterLeft = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_hint_spacing_center_left),
                minHintSpacingStart = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_hint_min_spacing_start),
                hintAlignLeftSpacingStart = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_hint_align_left_spacing_start),
                lockViewSpacingBottom = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_lock_view_min_spacing_bottom),
                lockViewMarginHorizontal = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_lock_view_margin_horizontal),
                lockViewHideDistance = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_lock_view_hide_distance),
                lockRecordDistance = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_button_lock_record_distance),
                attachButtonTextSize = getDimenPx(RDesign.attr.iconSize_3xl),
                attachButtonPaddingBottom = getDimenPx(RDesign.attr.offset_2xs),
                attachButtonHorizontalPadding = getDimenPx(RDesign.attr.offset_s),
                sendButtonHorizontalSpacing = getDimenPx(RDesign.attr.offset_xs),
                cancelIgnoreDx = resources.getDimension(R.dimen.design_message_panel_recorder_common_record_button_horizontal_movement_distance) * IGNORE_HORIZONTAL_MOVEMENT_DISTANCE_PERCENT
            )
        }
    }
}