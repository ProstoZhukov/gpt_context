package ru.tensor.sbis.design.message_panel.video_recorder.view.layout

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup.LayoutParams.*
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.models.style.UnaccentedButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasure
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.RecordControlView
import ru.tensor.sbis.design.message_panel.video_recorder.R
import ru.tensor.sbis.design.message_panel.video_recorder.view.VideoRecordView
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.RoundCameraView
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.video_message_view.player.VideoPlayerView
import kotlin.math.max
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Разметка панели управления записью аудио/видео сообщений.
 * @see VideoRecordView
 *
 * @author vv.chekurda
 */
internal class VideoRecordViewLayout(val view: VideoRecordView) {

    private val context: Context
        get() = view.context
    private val measuredWidth: Int
        get() = view.measuredWidth
    private val measuredHeight: Int
        get() = view.measuredHeight
    private val paddingStart: Int
        get() = view.paddingStart
    private val paddingTop: Int
        get() = view.paddingTop
    private val paddingEnd: Int
        get() = view.paddingEnd
    private val paddingBottom: Int
        get() = view.paddingBottom

    /**
     * Вью для записи круглых видео и показа изображения с камеры.
     */
    val cameraView = RoundCameraView(context).apply {
        autoClearState = false
        val minSize = context.resources.getDimensionPixelSize(R.dimen.design_message_panel_video_recorder_camera_view_min_size)
        minimumWidth = minSize
        minimumHeight = minSize
        val horizontalPadding = Offset.X2L.getDimenPx(context)
        updatePadding(left = horizontalPadding, right = horizontalPadding)
    }

    /**
     * Кнопка для смены камеры.
     */
    val switchCameraButton = SbisRoundButton(context).apply {
        style = UnaccentedButtonStyle.copy(defaultRoundButtonStyle = R.style.SwitchCameraButton)
        size = SbisRoundButtonSize.M
        val iconColor = ColorStateList.valueOf(
            IconColor.CONTRAST.getValue(context)
        )
        icon = SbisButtonTextIcon(
            SbisMobileIcon.Icon.smi_Execute,
            SbisButtonIconSize.X4L,
            SbisButtonIconStyle(iconColor)
        )
        setPadding(Offset.M.getDimenPx(context))
    }

    /**
     * Панель управления записью аудио/видео сообщений.
     */
    val controlView = RecordControlView(context).apply {
        id = R.id.design_message_panel_video_record_control_view
        isAudioRecord = false
    }

    /**
     * Круглая вью для проигрывания видео.
     */
    val videoPlayerView = VideoPlayerView(context).apply {
        val minSize = context.resources.getDimensionPixelSize(R.dimen.design_message_panel_video_recorder_camera_view_min_size)
        minimumWidth = minSize
        minimumHeight = minSize
        val horizontalPadding = Offset.X2L.getDimenPx(context)
        updatePadding(left = horizontalPadding, right = horizontalPadding)
        isVisible = false
    }

    /**
     * Вью заднего фона.
     */
    val backgroundView = View(context).apply {
        setBackgroundColor(context.getThemeColorInt(RDesign.attr.shadowColor))
        alpha = 0.45f
    }

    /**
     * Инициализировать разметку.
     */
    fun init() {
        view.apply {
            addView(backgroundView)
            addView(videoPlayerView)
            addView(cameraView)
            addView(controlView)
            addView(switchCameraButton)
            isClickable = true
        }
    }

    /**
     * Измерить дочерние элементы разметки.
     */
    fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        switchCameraButton.measure(MeasureSpecUtils.makeUnspecifiedSpec(), MeasureSpecUtils.makeUnspecifiedSpec())

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val horizontalPadding = paddingStart + paddingEnd
        val verticalPadding = paddingTop + paddingBottom
        val cameraWidthSpec = if (view.layoutParams.width == WRAP_CONTENT) {
            MeasureSpecUtils.makeUnspecifiedSpec()
        } else {
            MeasureSpecUtils.makeExactlySpec(width - horizontalPadding)
        }

        controlView.measure(cameraWidthSpec, MeasureSpecUtils.makeUnspecifiedSpec())

        val cameraHeightSpec = if (view.layoutParams.height == WRAP_CONTENT) {
            MeasureSpecUtils.makeUnspecifiedSpec()
        } else {
            MeasureSpecUtils.makeExactlySpec(height - verticalPadding - controlView.panelHeight)
        }

        val videoPlayerSpec = cameraWidthSpec.takeIf {
            MeasureSpec.getSize(cameraWidthSpec) <= MeasureSpec.getSize(cameraHeightSpec)
        } ?: cameraHeightSpec
        cameraView.safeMeasure(videoPlayerSpec, videoPlayerSpec)
        videoPlayerView.safeMeasure(videoPlayerSpec, videoPlayerSpec)
        backgroundView.measure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * Измерить дочерние элементы разметки.
     */
    fun getSuggestedMinimumWidth(): Int {
        val horizontalPadding = paddingStart + paddingEnd
        val contentMinWidth = with(cameraView) { minimumWidth + paddingStart + paddingEnd }
        return horizontalPadding + contentMinWidth
    }

    /**
     * Получить предлагаемую минимальную высоту.
     */
    fun getSuggestedMinimumHeight(): Int {
        val verticalPadding = paddingTop + paddingBottom
        val contentMinHeight = with(cameraView) { minimumHeight + paddingTop + paddingBottom } + switchCameraButton.measuredHeight
        return verticalPadding + contentMinHeight
    }

    /**
     * Разместить элементы разметки.
     */
    fun onLayout() {
        val switchTop = measuredHeight - paddingTop - paddingBottom - switchCameraButton.measuredHeight - controlView.panelHeight
        switchCameraButton.layout(
            paddingStart,
            switchTop,
            paddingStart + switchCameraButton.measuredWidth,
            switchTop + switchCameraButton.measuredHeight
        )

        val cameraAvailableHeight = measuredHeight - switchCameraButton.measuredHeight - paddingTop - paddingBottom
        val cameraTop = max(0, paddingTop + ((cameraAvailableHeight - cameraView.measuredHeight) / 2f).roundToInt())
        val cameraStart = ((measuredWidth - paddingStart - paddingEnd - cameraView.measuredWidth) / 2f).roundToInt()
        cameraView.safeLayout(cameraStart, cameraTop)
        videoPlayerView.safeLayout(cameraStart, cameraTop)

        val controlViewTop = measuredHeight - paddingTop - paddingBottom - controlView.measuredHeight
        controlView.layout(
            paddingStart,
            controlViewTop,
            measuredWidth - paddingEnd,
            controlViewTop + controlView.measuredHeight
        )
        backgroundView.layout(0, 0)
    }
}