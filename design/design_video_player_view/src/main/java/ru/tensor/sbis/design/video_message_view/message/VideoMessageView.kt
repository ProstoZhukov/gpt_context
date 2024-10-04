package ru.tensor.sbis.design.video_message_view.message

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.animation.addListener
import androidx.core.view.ScrollingView
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessage
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessageExpandableContainer
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.data.State
import ru.tensor.sbis.design.container.locator.watcher.AnchorWithManyAreas
import ru.tensor.sbis.design.container.locator.watcher.Area
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasure
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredHeight
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredWidth
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.media_player.utils.getPartiallyInvisibleHeight
import ru.tensor.sbis.design.media_player.utils.scrollByViewPosition
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.findViewParent
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.video_message_view.R
import ru.tensor.sbis.design.video_message_view.player.VideoPlayerView
import ru.tensor.sbis.design.video_message_view.player.children.StateListener
import ru.tensor.sbis.design.video_message_view.message.children.duration.VideoMessageDurationView
import ru.tensor.sbis.design.video_message_view.message.children.recognize.VideoMessageRecognizedExpandedView
import ru.tensor.sbis.design.video_message_view.message.children.recognize.VideoMessageRecognizedView
import ru.tensor.sbis.design.video_message_view.message.contract.VideoMessageViewApi
import ru.tensor.sbis.design.video_message_view.message.data.VideoMessageViewData

/**
 * Компонент видеосообщения с расшифровкой.
 * @see VideoMessageViewApi
 *
 * @author da.zhukov
 */
class VideoMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    VideoMessageViewApi,
    StateListener,
    AnchorWithManyAreas {

    /**
     * Метка о том, что сообщение является исходящим.
     */
    private val durationView = VideoMessageDurationView(context)

    private val recognizedExpandedViewTopSpace = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_xs)
    private val recognizedViewRightSpace =
        resources.getDimensionPixelSize(R.dimen.video_message_recognized_view_right_space)
    private val recognizedViewBottomLedge = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_3xs)

    private val durationArea: Area
        get() = Area(
            durationView.getAreaRect(),
            context.getDimen(ru.tensor.sbis.design.R.attr.borderRadius_3xs)
        )

    private val videoPlayerArea: Area
        get() {
            val videoPlayerRect = videoPlayerView.getAreaRect()
            return Area(
                videoPlayerRect,
                videoPlayerRect.width() / 2f
            )
        }

    private val recognizedButtonArea: Area
        get() = Area(
            recognizedView.getAreaRect(),
            context.getDimen(ru.tensor.sbis.design.R.attr.borderRadius_2xs)
        )

    private val recognizedTextArea: Area
        get() = Area(
            recognizedExpandedView.getAreaRect(),
            context.getDimen(ru.tensor.sbis.design.R.attr.borderRadius_2xs)
        )

    /**
     * Проигрыватель видеосообщения.
     */
    private val videoPlayerView = VideoPlayerView(context).apply {
        setStateListener(this@VideoMessageView)
        setOnLongClickListener { this@VideoMessageView.performLongClick() }
    }

    /**
     * Кнопка удаления видеосообщения.
     */
    private var deleteButton: SbisTextView? = null

    /**
     * Состояние воспроизведения.
     */
    private var state = State.DEFAULT

    /**
     * Максимальная ширина/высота вью.
     */
    private var maxSize = resources.getDimensionPixelSize(R.dimen.video_message_max_size)

    /**
     * Минимальная ширина/высота вью.
     */
    val collapsedSize: Int = resources.getDimensionPixelSize(R.dimen.video_message_collapsed_size)

    /**
     * Аниматор расширения/сужения вью.
     */
    private var sizeAnimator: ValueAnimator? = null

    /**
     * Длительность анимации расширения/сужения вью в миллисекундах.
     */
    private val animationDuration = 250L

    /**
     * Ширина вью длительности видеосообщения.
     */
    private val durationViewWidth = resources.getDimensionPixelSize(R.dimen.video_message_duration_view_width)

    /**
     * Высота вью длительности видеосообщения.
     */
    private val durationViewHeight = resources.getDimensionPixelSize(R.dimen.video_message_duration_view_height)

    /**
     * Ширина вью, которая отображает статус расшифровки.
     */
    private val recognizedViewWidth = resources.getDimensionPixelSize(R.dimen.video_message_recognized_view_width)

    /**
     * Высота вью, которая отображает статус расшифровки.
     */
    private val recognizedViewHeight = resources.getDimensionPixelSize(R.dimen.video_message_recognized_view_height)

    /**
     * Аниматор разворачивания/сворачивания расшифровки.
     */
    private var stateAnimator: ValueAnimator? = null

    /**
     * Необходимо ли менять видимость вью расшифровки и вью тображающую статус расшифровки.
     * (важно для правильного анимирования разворачивания/сворачивания расшифровки)
     */
    private var needChangeVisibility = true

    /**
     * Максимальная ширина.
     */
    private var maxWidth = 0

    /**
     * Признак раскрытой расшифровки.
     */
    private var isExpanded: Boolean = false
        set(value) {
            val changed = field != value
            field = value
            data?.isExpanded = value
            if (changed && needChangeVisibility) {
                recognizedExpandedView.isVisible = value
                updateRecognizeIconVisibility()
            }
        }

    /**
     * Признак наличия расшифровки.
     */
    private var hasRecognize: Boolean = false

    /**
     * Ширина дочернего view плеера.
     */
    val videoPlayerMeasuredWidth: Int
        get() = videoPlayerView.measuredWidth

    override var outcome: Boolean = false
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                recognizedView.setOutcome(value)
                recognizedExpandedView.setOutcome(value)
            }
        }

    /**
     * Вью отображающая статус расшифровки.
     */
    private val recognizedView = VideoMessageRecognizedView(context).apply {
        setOutcome(outcome)
        setOnClickListener { onExpandAreaClick() }
    }

    /**
     * Вью отображающая расшифровку.
     */
    private val recognizedExpandedView = VideoMessageRecognizedExpandedView(context).apply {
        isVisible = false
        setOutcome(outcome)
        setOnClickListener { onExpandAreaClick() }
    }

    override var data: VideoMessageViewData? = null
        set(value) {
            val changed = value != field
            val isSourceChanged = value?.playerData?.videoSource != field?.playerData?.videoSource
            field = value
            if (value != null) {
                this@VideoMessageView.needChangeVisibility = true
                this@VideoMessageView.isExpanded = value.isExpanded
                if (changed) {
                    if (isSourceChanged) clearState()
                    videoPlayerView.data = value.playerData
                    durationView.changeStatus(state)
                    setRecognize(value.recognizedText, value.recognized, value.isEdited)
                    safeRequestLayout()
                }
            } else {
                state = State.DEFAULT
            }
        }

    override var actionListener: MediaMessage.ActionListener? = null

    override var onDeleteClickListener: OnClickListener? = null
        set(value) {
            val isChanged = field != null && value == null || (field == null && value != null)
            field = value
            if (isChanged) {
                if (value != null) initDeleteButton()
                deleteButton?.isVisible = value != null
                deleteButton?.setOnClickListener(value)
                safeRequestLayout()
            }
        }

    init {
        id = R.id.design_video_player_view_video_message_view_id
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(videoPlayerView, LayoutParams(collapsedSize, collapsedSize))
        addView(durationView, LayoutParams(durationViewWidth, durationViewHeight))
        addView(recognizedView, LayoutParams(recognizedViewWidth, recognizedViewHeight))
        addView(recognizedExpandedView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * Установка проигрывателя для видео.
     */
    override fun setMediaPlayer(mediaPlayer: MediaPlayer) {
        videoPlayerView.setMediaPlayer(mediaPlayer)
    }

    /**
     * Задаёт обработчик нажатий на сообщение.
     */
    fun setOnMessageClickListener(messageClickListener: OnClickListener?) {
        videoPlayerView.setOnMessageClickListener(messageClickListener)
    }

    override fun showPlayClickAnimation(callback: () -> Unit) {
        changeSize { callback() }
    }

    override fun updateBufferingState(show: Boolean) {
        videoPlayerView.showBuffering(show)
    }

    override fun onStateChange(state: State, firstFrameRendered: Boolean) {
        val previousState = this.state
        this.state = state
        if (state == State.PLAYING && previousState != State.PAUSED) {
            // Первый кадр не всегда сразу отрисован
            videoPlayerView.postDelayed({
                videoPlayerView.showPreview(false)
            }, FIRST_FRAME_RENDER_DELAY_MS)
        } else if (state == State.DEFAULT) {
            changeSize(false)
            videoPlayerView.showPreview(true)
        }
        durationView.changeStatus(state)
    }

    override fun onFirstVideoFrameRendered(state: State) {
        if (state == State.PLAYING) {
            videoPlayerView.showPreview(false)
        }
    }

    override fun onDurationChange(duration: Int) {
        durationView.setDuration(duration)
    }

    override fun onVideoPlaybackError(error: Throwable) {
        actionListener?.onMediaPlaybackError(error)
    }

    override fun getAreas(): List<Area> =
        when {
            isExpanded -> listOf(videoPlayerArea, durationArea, recognizedTextArea)
            recognizedView.isGone -> listOf(videoPlayerArea, durationArea)
            else -> listOf(videoPlayerArea, durationArea, recognizedButtonArea)
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        maxWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd
        maxSize = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            minOf(maxWidth, maxSize)
        } else {
            maxSize
        }
        if (sizeAnimator?.isRunning != true) {
            when (state) {
                State.PLAYING,
                State.PAUSED -> {
                    videoPlayerView.layoutParams.apply {
                        width = maxSize
                        height = maxSize
                    }
                }
                State.DEFAULT -> {
                    videoPlayerView.layoutParams.apply {
                        width = collapsedSize
                        height = collapsedSize
                    }
                }
            }
        }
        deleteButton?.safeMeasure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        deleteButton?.layout(paddingStart, paddingTop)
        val videoLeft = if (outcome) measuredWidth - videoPlayerView.measuredWidth else paddingLeft
        videoPlayerView.safeLayout(videoLeft, paddingTop)
        durationView.layout(videoPlayerView.left, videoPlayerView.bottom - durationView.measuredHeight)
        val recognizedViewTop = videoPlayerView.bottom - (recognizedView.measuredHeight - recognizedViewBottomLedge)
        recognizedView.layout(
            videoPlayerView.right - recognizedView.measuredWidth - recognizedViewRightSpace,
            recognizedViewTop
        )
        val recognizedTextLeft = if (outcome) {
            videoPlayerView.right - recognizedExpandedView.measuredWidth
        } else {
            videoPlayerView.left
        }
        recognizedExpandedView.layout(recognizedTextLeft, videoPlayerView.bottom + recognizedExpandedViewTopSpace)
    }

    override fun getSuggestedMinimumWidth(): Int =
        maxOf(
            super.getSuggestedMinimumWidth(),
            videoPlayerView.measuredWidth,
            recognizedExpandedView.safeMeasuredWidth
        )

    override fun getSuggestedMinimumHeight(): Int =
        maxOf(
            super.getSuggestedMinimumHeight(),
            videoPlayerView.measuredHeight.plus(recognizedExpandedView.safeMeasuredHeight)
                .plus(recognizedExpandedViewTopSpace).plus(recognizedViewBottomLedge)
        )

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sizeAnimator?.cancel()
        sizeAnimator = null
    }

    override fun setOnLongClickListener(listener: OnLongClickListener?) {
        super.setOnLongClickListener(listener)
        children.forEach { it.setOnLongClickListener(listener) }
    }

    /**
     * Анимированное изменение размера вью.
     */
    private fun changeSize(isStart: Boolean = true, callback: (() -> Unit)? = null) {
        this.sizeAnimator?.cancel()
        this.sizeAnimator = null

        if (videoPlayerView.layoutParams.width == if (isStart) maxSize else collapsedSize) {
            callback?.invoke()
            return
        }

        val sizeAnimator = if (isStart) {
            ValueAnimator.ofInt(collapsedSize, maxSize)
        } else {
            ValueAnimator.ofInt(maxSize, collapsedSize)
        }
        this.sizeAnimator = sizeAnimator
        sizeAnimator.duration = animationDuration

        var scrollingView: ViewGroup? = null
        var container: View? = null
        var partiallyInvisibleHeight = 0
        var lastPartiallyInvisibleHeight = 0

        val needScroll = if (isStart) {
            scrollingView = findViewParent(this) { it is ScrollingView || it is ScrollView }
            if (scrollingView != null) {
                container = (findViewParent<View>(this) { it is MediaMessageExpandableContainer } ?: this).also {
                    partiallyInvisibleHeight = scrollingView.getPartiallyInvisibleHeight(it)
                    lastPartiallyInvisibleHeight = partiallyInvisibleHeight
                }
                true
            } else {
                false
            }
        } else {
            false
        }

        sizeAnimator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            videoPlayerView.updateLayoutParams {
                if (needScroll) {
                    val partialInvisibleDy = if (partiallyInvisibleHeight != 0) {
                        val partialFraction = 1f - valueAnimator.animatedFraction
                        val partialInvisibleValue = (partiallyInvisibleHeight * partialFraction).toInt()
                        (lastPartiallyInvisibleHeight - partialInvisibleValue).also {
                            lastPartiallyInvisibleHeight = partialInvisibleValue
                        }
                    } else {
                        0
                    }

                    val dy = if (partiallyInvisibleHeight >= 0) {
                        animatedValue - videoPlayerView.height + partialInvisibleDy
                    } else {
                        partialInvisibleDy
                    }
                    scrollingView?.scrollByViewPosition(container!!, dy)
                }
                height = animatedValue
                width = animatedValue
            }
        }
        sizeAnimator.addListener(
            onStart = {
                videoPlayerView.changeControlVisibility(isVisible = false)
            },
            onEnd = {
                videoPlayerView.changeControlVisibility(isVisible = true)
                callback?.invoke()
            }
        )
        sizeAnimator.start()
    }

    private fun setRecognize(text: CharSequence?, recognized: Boolean?, isEdited: Boolean) {
        hasRecognize = recognized == true || isEdited
        if (hasRecognize) recognizedExpandedView.setRecognizeText(text)
        recognizedView.isRecognized = hasRecognize
        updateRecognizeIconVisibility()
    }

    private fun updateRecognizeIconVisibility() {
        recognizedView.isVisible = !isExpanded && data?.let { it.recognized != false } ?: false
    }

    private fun onExpandAreaClick() {
        if (!hasRecognize) return
        if (isExpanded) needChangeVisibility = false
        isExpanded = !isExpanded
        stateAnimator?.cancel()
        safeRequestLayout()

        val needTryScroll = actionListener?.onExpandClicked(isExpanded) ?: true
        val container = findViewParent<View>(this) { it is MediaMessageExpandableContainer } ?: this
        val scrollingView: ViewGroup? = if (needTryScroll) {
            findViewParent(this) { it is ScrollingView || it is ScrollView }
        } else {
            null
        }
        val needScroll = scrollingView != null

        val currentHeight = recognizedExpandedView.safeMeasuredHeight

        recognizedExpandedView.safeMeasure(
            MeasureSpecUtils.makeAtMostSpec(maxWidth - paddingStart - paddingEnd),
            makeUnspecifiedSpec()
        )
        val newHeight = if (!isExpanded) 0 else recognizedExpandedView.safeMeasuredHeight

        recognizedExpandedView.updateLayoutParams<LayoutParams> {
            height = currentHeight
        }

        stateAnimator = ValueAnimator.ofInt(currentHeight, newHeight)
        stateAnimator?.duration = EXPAND_COLLAPSE_DURATION
        stateAnimator?.addUpdateListener { valueAnimator ->
            recognizedExpandedView.updateLayoutParams<LayoutParams> {
                val animatedValue = valueAnimator.animatedValue as Int
                if (needScroll) {
                    scrollingView?.scrollByViewPosition(
                        container,
                        animatedValue - recognizedExpandedView.height
                    )
                }
                height = animatedValue
            }
        }
        var animationIsCanceled = false
        stateAnimator?.addListener(
            onEnd = {
                needChangeVisibility = true
                if (!isExpanded && !animationIsCanceled) {
                    recognizedView.visibility = VISIBLE
                    recognizedExpandedView.visibility = GONE
                    animationIsCanceled = false
                }
            },
            onCancel = {
                needChangeVisibility = true
                animationIsCanceled = true
            }
        )
        stateAnimator?.start()
    }

    private fun View.getAreaRect(): Rect {
        val result = Rect()
        getDrawingRect(result)
        this@VideoMessageView.offsetDescendantRectToMyCoords(this, result)
        return result
    }

    private fun clearState() {
        sizeAnimator?.cancel()
        state = State.DEFAULT
        videoPlayerView.showPreview(true)
    }

    private fun initDeleteButton() {
        deleteButton = SbisTextView(context, R.style.VideoMessageDeleteButtonStyle)
        addView(deleteButton)
    }
}

/**
 * Длительность анимации разворачивания/сворачивания расшифроки.
 */
private const val EXPAND_COLLAPSE_DURATION = 250L
private const val FIRST_FRAME_RENDER_DELAY_MS = 50L