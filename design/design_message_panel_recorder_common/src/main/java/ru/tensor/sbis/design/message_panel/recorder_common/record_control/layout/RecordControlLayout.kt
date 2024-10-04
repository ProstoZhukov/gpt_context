package ru.tensor.sbis.design.message_panel.recorder_common.record_control.layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.SimplePaint
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasure
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredHeight
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredWidth
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.message_panel.common.view.quote.MessagePanelQuoteView
import ru.tensor.sbis.design.message_panel.common.view.recipients.MessagePanelRecipientsView
import ru.tensor.sbis.design.message_panel.decl.record.RecordControlButtonPosition
import ru.tensor.sbis.design.message_panel.decl.record.RecordControlButtonPosition.FIRST_ALIGN_END
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.RecordControlView
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordButtonControlContainer
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordHintDrawable
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordLockView
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordTimeDrawable
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecorderFieldDrawable
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordingIndicatorDrawable
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon

/**
 * Разметка панели управления записью аудио/видео сообщений.
 * @see RecordControlView
 *
 * @author vv.chekurda
 */
internal class RecordControlLayout(val view: RecordControlView) {

    private val context: Context
        get() = view.context
    private val measuredWidth: Int
        get() = view.measuredWidth
    private val measuredHeight: Int
        get() = view.measuredHeight

    /**
     * Верхний разделитель.
     */
    private val topDividerView = View(context).apply {
        setBackgroundColor(context.getColorFromAttr(RDesign.attr.readonlyBorderColor))
    }

    /**
     * Кликабельная прозрачная заглушка для предотвращения нажатий по элементам под панелью в визуальной области панели записи.
     */
    private val clickableStubView = View(context).apply { isClickable = true }
    private val backgroundPaint = SimplePaint {
        color = context.getColorFromAttr(RDesign.attr.contrastBackgroundColor)
        style = Paint.Style.FILL
    }
    private val backgroundRect = Rect()

    private val isFirstButtonPosition: Boolean
        get() = recordButtonPosition == FIRST_ALIGN_END

    /**
     * Размеры внутренней разметки.
     */
    val dimens = RecordControlDimens.create(view.context)

    /**
     * Замочек закрепления записи.
     */
    val lockView = RecordLockView(context)

    /**
     * Контейнер замочка закрепления записи.
     */
    val lockContainer = FrameLayout(context).apply {
        val lp = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            marginStart = dimens.lockViewMarginHorizontal
            marginEnd = dimens.lockViewMarginHorizontal
        }
        addView(lockView, lp)
    }

    /**
     * Высота панели сообщений.
     */
    var panelHeight: Int = 0
        private set

    /**
     * Контейнер подвижной кнопки записи.
     */
    val controlContainer = RecordButtonControlContainer(context)

    /**
     * Разметка кнопки отмены записи.
     */
    val cancelLayout = controlContainer.cancelLayout

    /**
     * Индикатор записи (красный кружок).
     */
    val recordingIndicator = RecordingIndicatorDrawable(context).apply {
        alpha = 0
        callback = view
    }

    /**
     * Таймер продолжительности записи.
     */
    val timerDrawable = RecordTimeDrawable(context).apply {
        alpha = 0
        callback = view
    }

    /**
     * Разметка текста-подсказки для отмены записи при движении кнопки влево.
     */
    val hintDrawable = RecordHintDrawable(context).apply {
        alpha = 0
        callback = view
    }

    /**
     * Скругленное поле анимируемого фона.
     */
    val fieldDrawable = RecorderFieldDrawable(context).apply {
        callback = view
    }

    /**
     * Панель получателей.
     */
    val recipientsView = MessagePanelRecipientsView(context).apply { isVisible = false }

    /**
     * Панель цитирования.
     */
    val quoteView = MessagePanelQuoteView(context).apply { isVisible = false }

    /**
     * Кнопка отправки сообщения для анимации перехода к записи.
     */
    val decorSendButton = SbisRoundButton(context).apply {
        icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_BtArrow)
        style = PrimaryButtonStyle
        size = SbisRoundButtonSize.S
    }

    /**
     * Кнопка прикрепления вложений для анимации перехода к записи.
     */
    val decorAttachButton = TextLayout {
        text = SbisMobileIcon.Icon.smi_attach.character.toString()
        paint.apply {
            typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            textSize = dimens.attachButtonTextSize.toFloat()
            color = ContextCompat.getColor(context, RMPCommon.color.design_message_panel_control_selectable_color)
        }
        padding = TextLayout.TextLayoutPadding(
            start = dimens.attachButtonHorizontalPadding,
            end = dimens.attachButtonHorizontalPadding,
            bottom = dimens.attachButtonPaddingBottom
        )
    }

    /**
     * Исходная позиция кнопки записи, которой управляет пользователь.
     */
    var recordButtonPosition: RecordControlButtonPosition = FIRST_ALIGN_END
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                controlContainer.recordButtonPosition = value
                view.safeRequestLayout()
            }
        }

    /**
     * Инициализировать разметку.
     */
    fun init() {
        view.apply {
            addView(decorSendButton)
            addView(clickableStubView)
            addView(quoteView)
            addView(recipientsView)
            addView(topDividerView)
            addView(lockContainer)
            addView(controlContainer)
        }
    }

    /**
     * Измерить дочерние элементы разметки.
     */
    fun onMeasure(widthMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val horizontalPadding = with(view) { paddingStart + paddingEnd }
        recipientsView.safeMeasure(
            makeExactlySpec(width - horizontalPadding),
            makeUnspecifiedSpec()
        )
        quoteView.safeMeasure(
            makeExactlySpec(width - horizontalPadding),
            makeUnspecifiedSpec()
        )
        val fieldHeight = dimens.backgroundFieldVerticalMargin * 2 + dimens.backgroundFieldHeight
        panelHeight = fieldHeight + quoteView.safeMeasuredHeight + recipientsView.safeMeasuredHeight
        lockContainer.measure(
            makeUnspecifiedSpec(),
            makeExactlySpec(dimens.lockViewSpacingBottom - fieldHeight + lockView.measuredHeight)
        )
        topDividerView.safeMeasure(
            makeExactlySpec(width - horizontalPadding),
            makeExactlySpec(dimens.dividerHeight)
        )
        clickableStubView.measure(
            makeExactlySpec(width),
            makeExactlySpec(fieldHeight + recipientsView.safeMeasuredHeight + quoteView.safeMeasuredHeight)
        )
        controlContainer.measure(
            makeExactlySpec(width - horizontalPadding),
            makeExactlySpec(getSuggestedMinimumHeight())
        )
        decorSendButton.safeMeasure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
    }

    /**
     * Получить предлагаемую минимальную высоту.
     */
    fun getSuggestedMinimumHeight(): Int =
        view.paddingBottom.plus(view.paddingTop)
            .plus(dimens.backgroundFieldVerticalMargin * 2)
            .plus(dimens.backgroundFieldHeight)
            .plus(recipientsView.safeMeasuredHeight)
            .plus(quoteView.safeMeasuredHeight)
            .plus(lockContainer.measuredHeight)

    /**
     * Разместить элементы разметки.
     */
    fun onLayout() {
        val fieldStart = view.paddingStart + dimens.backgroundFieldHorizontalMargin
        val fieldBottom = measuredHeight - view.paddingBottom - dimens.backgroundFieldVerticalMargin
        val fieldTop = fieldBottom - dimens.backgroundFieldHeight
        val fieldEnd = measuredWidth - view.paddingEnd - if (isFirstButtonPosition) {
            dimens.backgroundFieldHorizontalMargin
        } else {
            (dimens.sendButtonHorizontalSpacing * 2f + decorSendButton.measuredWidth).roundToInt()
        }
        val fieldCenterX = fieldStart + (fieldEnd - fieldStart) / 2
        fieldDrawable.apply {
            setBounds(fieldStart, fieldTop, fieldEnd, fieldBottom)
            collapsedPaddingStart = decorAttachButton.width.toFloat() - dimens.backgroundFieldHorizontalMargin
            collapsedPaddingEnd = if (isFirstButtonPosition) {
                dimens.sendButtonHorizontalSpacing * 2f + decorSendButton.measuredWidth - dimens.backgroundFieldHorizontalMargin
            } else {
                0f
            }
        }

        val inputTop = fieldTop - dimens.backgroundFieldVerticalMargin
        quoteView.safeLayout(
            view.paddingStart,
            inputTop - quoteView.safeMeasuredHeight
        )
        recipientsView.safeLayout(
            view.paddingStart,
            quoteView.top - recipientsView.safeMeasuredHeight
        )
        topDividerView.safeLayout(0, recipientsView.top)
        clickableStubView.layout(0, recipientsView.top)
        controlContainer.layout(
            measuredWidth - view.paddingEnd - controlContainer.measuredWidth,
            measuredHeight - view.paddingBottom - controlContainer.measuredHeight
        )

        lockContainer.layout(
            controlContainer.left + controlContainer.recordButtonCenterX - (lockContainer.measuredWidth / 2f).toInt(),
            inputTop - lockContainer.measuredHeight
        )

        val indicatorStart = fieldStart + dimens.recordIndicatorPaddingStart
        val indicatorTop = fieldTop + dimens.recordIndicatorPaddingTop
        recordingIndicator.setBounds(
            indicatorStart,
            indicatorTop,
            indicatorStart + dimens.recordIndicatorSize,
            indicatorTop + dimens.recordIndicatorSize
        )

        val timerStart = recordingIndicator.bounds.right + dimens.recordIndicatorPaddingEnd
        val timerBottom = fieldBottom - dimens.timerTextPaddingBottom
        timerDrawable.setBounds(
            timerStart,
            timerBottom - timerDrawable.intrinsicHeight,
            timerStart + timerDrawable.intrinsicWidth,
            timerBottom
        )

        val expectedHintStart = fieldCenterX - dimens.hintSpacingCenterLeft - hintDrawable.intrinsicWidth / 2
        val minHintStart = timerDrawable.bounds.right + dimens.minHintSpacingStart
        val hintStart = if (expectedHintStart >= minHintStart) {
            hintDrawable.scaleTranslationX = 1f
            expectedHintStart
        } else {
            hintDrawable.scaleTranslationX = dimens.hintAlignLeftSpacingStart / dimens.minHintSpacingStart.toFloat()
            timerDrawable.bounds.right + dimens.hintAlignLeftSpacingStart
        }
        val hintTop = (fieldTop + (fieldDrawable.bounds.height() - hintDrawable.intrinsicHeight) / 2f).roundToInt()
        hintDrawable.setBounds(
            hintStart,
            hintTop,
            hintStart + hintDrawable.intrinsicWidth,
            hintTop + hintDrawable.intrinsicHeight
        )

        cancelLayout.layout(
            fieldCenterX - dimens.hintSpacingCenterLeft - cancelLayout.width / 2,
            (fieldTop + (fieldDrawable.bounds.height() - cancelLayout.height) / 2f).roundToInt()
        )

        decorAttachButton.layout(view.paddingStart, fieldBottom - decorAttachButton.height)

        decorSendButton.safeLayout(
            measuredWidth - view.paddingEnd - dimens.sendButtonHorizontalSpacing - decorSendButton.safeMeasuredWidth,
            fieldTop
        )

        val backgroundHeight = dimens.backgroundFieldHeight.plus(dimens.backgroundFieldVerticalMargin * 2)
            .plus(recipientsView.safeMeasuredHeight)
            .plus(quoteView.safeMeasuredHeight)
        backgroundRect.set(
            0,
            measuredHeight - backgroundHeight,
            measuredWidth,
            measuredHeight
        )
    }

    /**
     * Нарисовать элементы разметки.
     */
    fun onDraw(canvas: Canvas) {
        canvas.drawRect(backgroundRect, backgroundPaint)
        fieldDrawable.draw(canvas)
        recordingIndicator.draw(canvas)
        timerDrawable.draw(canvas)
        cancelLayout.draw(canvas)
        hintDrawable.draw(canvas)
        decorAttachButton.draw(canvas)
    }

    /**
     * Подтвердить относится ли этот рисунок [who] к этой разметке..
     */
    fun verifyDrawable(who: Drawable): Boolean =
        when (who) {
            recordingIndicator, hintDrawable, timerDrawable, fieldDrawable -> true
            else -> false
        }
}