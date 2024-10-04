package ru.tensor.sbis.design.video_message_view.player

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Outline
import android.util.AttributeSet
import android.view.Gravity
import android.view.TextureView
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.media3.ui.AspectRatioFrameLayout
import ru.tensor.sbis.attachments.ui.view.AttachmentPreviewView
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.video_message_view.R
import ru.tensor.sbis.design.video_message_view.player.children.StateListener
import ru.tensor.sbis.design.video_message_view.player.children.VideoPlayerControlView
import ru.tensor.sbis.design.video_message_view.player.contract.VideoPlayerViewApi
import ru.tensor.sbis.design.video_message_view.player.data.VideoPlayerViewData
import kotlin.math.roundToInt

/**
 * Компонент круглого проигрывателя видео.
 *
 * @author da.zhukov
 */
class VideoPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    VideoPlayerViewApi {

    /**
     * Вью для отображения превью видео
     */
    private val previewView = AttachmentPreviewView(context, attrs)

    /**
     * Вью для проигрывания видео
     */
    private val textureView = TextureView(context, attrs).apply {
        this.isOpaque = false
    }

    /**
     * Крутилка для отображения процесса буферизации
     */
    private val bufferingIndicator = ProgressBar(context, attrs).apply {
        isVisible = false
        indeterminateTintList =
            ColorStateList.valueOf(context.getThemeColorInt(RDesign.attr.unaccentedIconColor))
    }
    private val bufferingIndicatorRunnable = Runnable {
        bufferingIndicator.isVisible = true
    }
    private var isBufferingVisible: Boolean = false

    /**
     * Вью для отображения прогресса видео
     */
    private val controlView = VideoPlayerControlView(context, attrs, defStyleAttr, defStyleRes).apply {
        id = R.id.design_video_player_view_video_message_controller_view_id
        setTextureView(textureView)
        setOnLongClickListener { this@VideoPlayerView.performLongClick() }
    }

    /**
     * Лейаут в котором должны распологаться вью, для отображения поверх textureView
     */
    private val aspectRatioFrameLayout = AspectRatioFrameLayout(context).apply {
        setAspectRatio(1f)
        addView(textureView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(previewView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(bufferingIndicator, LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER))
        addView(controlView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        this@VideoPlayerView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                with(view) {
                    val width = measuredWidth - paddingStart - paddingEnd
                    val height = measuredHeight - paddingTop - paddingBottom
                    val size = minOf(width, height)
                    val start = ((measuredWidth - width) / 2f).roundToInt()
                    val top = ((measuredHeight - height) / 2f).roundToInt()
                    outline.setOval(start, top, start + size, top + size)
                }
            }

        }
        this@VideoPlayerView.clipToOutline = true
    }

    override var data: VideoPlayerViewData? = null
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                controlView.data = value
                if (value?.previewVM != null) {
                    previewView.setViewModel(value.previewVM)
                } else {
                    showPreview(false)
                }
            }
        }

    init {
        id = R.id.design_video_player_view_round_video_player_view_id
        addView(aspectRatioFrameLayout, MATCH_PARENT, MATCH_PARENT)
    }

    /**
     * Установка проигрывателя для видео
     */
    override fun setMediaPlayer(mediaPlayer: MediaPlayer) {
        controlView.setMediaPlayer(mediaPlayer)
    }

    /**
     * Установка слушателя изменения состояния проигрывания
     */
    override fun setStateListener(listener: StateListener) {
        controlView.setStateListener(listener)
    }

    /**
     * Показать превью видеосообщения.
     */
    override fun showPreview(show: Boolean) {
        previewView.isVisible = show
    }

    /**
     * Показать загрузку видеосообщения.
     */
    override fun showBuffering(show: Boolean) {
        if (show != isBufferingVisible) {
            isBufferingVisible = show
            if (show) {
                bufferingIndicator.postDelayed(bufferingIndicatorRunnable, BUFFERING_INDICATOR_DELAY_MS)
            } else {
                bufferingIndicator.handler.removeCallbacks(bufferingIndicatorRunnable)
                bufferingIndicator.isVisible = false
            }
        }
    }

    /**
     * Изменить видимость контролов для управления проигрыванием.
     */
    override fun changeControlVisibility(isVisible: Boolean) {
        if (isVisible) controlView.show() else controlView.hide()
    }

    /**
     * Подготовить первый кадр.
     */
    override fun prepareFirstFrame() {
        controlView.prepareFirstFrame()
    }

    /**
     * Очистить состояние.
     */
    override fun clearState() {
        controlView.clearState()
    }

    /**
     * Задаёт обработчик нажатий на сообщение
     */
    fun setOnMessageClickListener(messageClickListener: OnClickListener?) {
        controlView.setOnMessageClickListener(messageClickListener)
    }
}

private const val BUFFERING_INDICATOR_DELAY_MS = 200L