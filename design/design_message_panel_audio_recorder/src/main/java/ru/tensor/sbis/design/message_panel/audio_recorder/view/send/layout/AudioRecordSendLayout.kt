package ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion.CRYING
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion.POUTING
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion.SMILING
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion.THINKING
import ru.tensor.sbis.design.audio_player_view.view.player.AudioPlayerView
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredWidth
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.message_panel.audio_recorder.R
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordMode
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.AudioRecordSendView
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordSendViewState
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout.record.AudioRecordControlView
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getThemeColorInt
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon

/**
 * Разметка копмпонента отправки записанного аудиосообщения.
 * @see AudioRecordSendView
 *
 * @author vv.chekurda
 */
internal class AudioRecordSendLayout(val view: AudioRecordSendView) {

    private val context: Context
        get() = view.context
    private val measuredWidth: Int
        get() = view.measuredWidth
    private val measuredHeight: Int
        get() = view.measuredHeight
    private val isLandscape: Boolean by lazy {
        DeviceConfigurationUtils.isLandscape(context)
    }

    private val dimens = AudioRecordSendDimens.create(context)

    private val smilesPressedColor = context.getThemeColorInt(RDesign.attr.paleActiveColor)

    private val smilingFace = createSmileButtonView(SMILING)
    private val poutingFace = createSmileButtonView(POUTING)
    private val thinkingFace = createSmileButtonView(THINKING)
    private val neutralFace = createSmileButtonView(CRYING)
    private val smileDrawableInnerPadding = smilingFace.drawableInnerPadding
    private var scrollHorizontalSpacing = 0
    private var startScrollPosition = 0

    var viewState: AudioRecordSendViewState = AudioRecordSendViewState.PLAYER

    var mode: AudioRecordMode = AudioRecordMode.SIMPLE
        set(value) {
            field = value
            recordControl.mode = value
            sendButton.isVisible = value == AudioRecordMode.MESSAGE_PANEL
            updateSmiles()
            updateBackground()
            view.safeRequestLayout()
        }

    val smilesLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        val smileLayoutParams = ViewGroup.LayoutParams(
            dimens.smileSize + dimens.smileSpacing,
            dimens.smileSize
        )
        addView(smilingFace, smileLayoutParams)
        addView(poutingFace, smileLayoutParams)
        addView(thinkingFace, smileLayoutParams)
        addView(neutralFace, smileLayoutParams)
    }

    val smilesScrollContainer = HorizontalScrollView(context).apply {
        overScrollMode = View.OVER_SCROLL_NEVER
        isHorizontalScrollBarEnabled = false
        addView(smilesLayout)
        doOnNextLayout { resetScrollPosition() }
    }

    /**
     * Кнопка закрытия отправки аудиосообщения.
     */
    val cancelSendButton = TextLayout.createTextLayoutByStyle(
        context,
        R.style.AudioRecorderClearButtonDefaultStyle
    ).apply {
        makeClickable(view)
    }

    /**
     * Проигрыватель аудиосообщения.
     */
    val playerView = AudioPlayerView(context).apply {
        viewMode = AudioPlayerView.ViewMode.RECORDER
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, dimens.fieldHeight)
    }

    /**
     * Кнопка обычной отправки аудиосообщения.
     */
    val sendButton = SbisRoundButton(context).apply {
        id = R.id.design_message_panel_audio_record_send_button
        icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_BtArrow)
        style = PrimaryButtonStyle
        size = SbisRoundButtonSize.S
        isVisible = false
    }

    val recordControl = AudioRecordControlView(context)

    val fieldDrawable: Drawable =
        ContextCompat.getDrawable(context, RMPCommon.drawable.design_message_panel_common_input_field_drawable)!!

    fun resetScrollPosition() {
        smilesScrollContainer.scrollTo(startScrollPosition, 0)
    }

    /**
     * Инициализировать разметку.
     */
    fun init() {
        view.apply {
            addView(smilesScrollContainer)
            addView(playerView)
            addView(sendButton)
            addView(recordControl)
        }
        updateBackground()
        updateSmiles()
    }

    private fun updateBackground() {
        if (mode == AudioRecordMode.MESSAGE_PANEL) {
            view.setBackgroundColor(context.getColorFromAttr(RDesign.attr.contrastBackgroundColor))
        } else {
            view.background = null
        }
    }

    /**
     * Измерить дочерние элементы разметки.
     * P.S. Изощренные вычисления связаны с возможным отображением сколлируемых 3ех смайликов на узких девайсах.
     */
    fun onMeasure(widthMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        sendButton.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        val smileContainerVisibleWidth = if (isLandscape) measureAllSmiles() else measureTwoSmiles()
        if (viewState == AudioRecordSendViewState.PLAYER) {
            val playerWidth = width
                .minus(view.paddingStart)
                .minus(view.paddingEnd)
                .minus(cancelSendButton.width)
                .minus(sendButton.safeMeasuredWidth)
                .minus(if (mode == AudioRecordMode.MESSAGE_PANEL) dimens.sendButtonHorizontalMargin else 0)
                .minus(dimens.fieldMarginStart)
                .minus(smileContainerVisibleWidth)
            playerView.measure(makeExactlySpec(playerWidth), makeUnspecifiedSpec())
        } else {
            val recorderWidth = width
                .minus(view.paddingStart)
                .minus(view.paddingEnd)
                .minus(
                    if (mode == AudioRecordMode.MESSAGE_PANEL) {
                        sendButton.measuredWidth + dimens.sendButtonHorizontalMargin * 2
                    } else {
                        0
                    }
                )
            recordControl.apply {
                val sendButtonSpace = if (mode == AudioRecordMode.MESSAGE_PANEL) {
                    dimens.sendButtonHorizontalMargin
                } else 0
                animatedEndSpacing = smileContainerVisibleWidth - sendButtonSpace
                measure(makeExactlySpec(recorderWidth), makeUnspecifiedSpec())
            }
        }
    }

    private fun measureTwoSmiles(): Int {
        val additionalContainerVisibleWidth = if (mode == AudioRecordMode.MESSAGE_PANEL) {
            dimens.smileSpacing
        } else {
            0
        }
        val smileContainerVisibleWidth = (dimens.smileSize * 2)
            .plus(dimens.smileSpacing * 2)
            .plus(smileDrawableInnerPadding * 2)
            .plus(additionalContainerVisibleWidth)
        val additionalScrollSpace = if (mode == AudioRecordMode.MESSAGE_PANEL) {
            sendButton.measuredWidth
        } else {
            sendButton.measuredWidth / 2
        }
        val smileContainerWidth = smileContainerVisibleWidth + additionalScrollSpace
        smilesScrollContainer.measure(makeExactlySpec(smileContainerWidth), makeUnspecifiedSpec())
        return smileContainerVisibleWidth
    }

    private fun measureAllSmiles(): Int {
        smilesScrollContainer.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        return smilesScrollContainer.measuredWidth
    }

    /**
     * Получить предлагаемую минимальную ширину разметки.
     */
    fun getSuggestedMinimumWidth(): Int =
        view.paddingStart.plus(view.paddingEnd)
            .plus(cancelSendButton.width)
            .plus(dimens.playerMinWidth)
            .plus(smilesScrollContainer.measuredWidth - scrollHorizontalSpacing * 2)
            .plus(sendButton.measuredWidth)
            .plus(dimens.sendButtonHorizontalMargin)
            .plus(dimens.fieldMarginStart)

    /**
     * Получить предлагаемую минимальную высоту разметки.
     */
    fun getSuggestedMinimumHeight(): Int =
        view.paddingTop.plus(view.paddingBottom)
            .plus(
                if (viewState == AudioRecordSendViewState.PLAYER) {
                    dimens.fieldHeight + dimens.fieldVerticalMargin * 2
                } else {
                    recordControl.measuredHeight
                }
            )

    /**
     * Разместить дочерние элементы разметки.
     */
    fun onLayout() {
        val verticalPadding = view.paddingTop + view.paddingBottom
        val baseline = measuredHeight - view.paddingBottom - dimens.fieldVerticalMargin
        sendButton.safeLayout(
            measuredWidth - view.paddingEnd - dimens.sendButtonHorizontalMargin - sendButton.measuredWidth,
            baseline - sendButton.measuredHeight
        )

        val smilesStart = if (mode == AudioRecordMode.MESSAGE_PANEL) {
            val additionalSmileStart = if (!isLandscape) sendButton.measuredWidth / 2 else 0
            sendButton.left - smilesScrollContainer.measuredWidth + additionalSmileStart
        } else {
            measuredWidth - view.paddingEnd - smilesScrollContainer.measuredWidth
        }
        val smilesTop = sendButton.top
            .plus((sendButton.measuredHeight - smilesScrollContainer.measuredHeight) / 2f)
            .roundToInt()
        smilesScrollContainer.layout(smilesStart, smilesTop)

        if (viewState == AudioRecordSendViewState.PLAYER) {
            cancelSendButton.layout(
                view.paddingStart + dimens.fieldMarginStart,
                view.paddingTop + (measuredHeight - verticalPadding - cancelSendButton.height) / 2
            )
            playerView.layout(
                cancelSendButton.right,
                baseline - playerView.measuredHeight
            )
            fieldDrawable.setBounds(
                cancelSendButton.left,
                baseline - dimens.fieldHeight,
                playerView.right,
                baseline
            )
            cancelSendButton.setStaticTouchRect(
                Rect(cancelSendButton.left, fieldDrawable.bounds.top, cancelSendButton.right, fieldDrawable.bounds.bottom)
            )
        } else {
            recordControl.layout(
                view.paddingStart,
                view.measuredHeight - view.paddingBottom - recordControl.measuredHeight
            )
        }
    }

    /**
     * Нарисовать дочерние элементы разметки.
     */
    fun onDraw(canvas: Canvas) {
        fieldDrawable.draw(canvas)
        cancelSendButton.draw(canvas)
    }

    /**
     * Обработать событие касания.
     */
    fun onTouchEvent(event: MotionEvent): Boolean =
        cancelSendButton.onTouch(view, event)

    private fun updateSmiles() {
        val additional = if (isLandscape) 0 else dimens.sendButtonSize / 2
        scrollHorizontalSpacing = dimens.smileSpacing + additional
        smilesLayout.updatePadding(
            left = scrollHorizontalSpacing,
            right = if (mode == AudioRecordMode.MESSAGE_PANEL) scrollHorizontalSpacing else 0
        )

        startScrollPosition = if (isLandscape) 0 else scrollHorizontalSpacing * 2 + smileDrawableInnerPadding
        resetScrollPosition()
    }

    private fun createSmileButtonView(type: AudioMessageEmotion): SmileSendButton =
        SmileSendButton(context, smilesPressedColor, type).apply {
            id = R.id.design_message_panel_audio_record_smile_button
            updatePadding(left = dimens.smileSpacing / 2, right = dimens.smileSpacing / 2)
        }
}