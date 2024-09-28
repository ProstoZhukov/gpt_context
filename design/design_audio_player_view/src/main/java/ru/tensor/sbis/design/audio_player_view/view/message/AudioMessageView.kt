package ru.tensor.sbis.design.audio_player_view.view.message

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.animation.addListener
import androidx.core.view.ScrollingView
import androidx.core.view.doOnNextLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.audio_player_view.R
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageViewData
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessage
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessageExpandableContainer
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewApi
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.audio_player_view.view.player.AudioPlayerView
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasure
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredHeight
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredWidth
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.media_player.utils.scrollByViewPosition
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.findViewParent
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Компонент аудиосообщения с расшифровкой.
 * @see AudioMessageViewApi
 *
 * @author vv.chekurda
 */
class AudioMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes),
    AudioMessageViewApi {

    private val audioPlayerView = AudioPlayerView(context).apply {
        id = R.id.design_audio_message_view_audio_player_view_id
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val verticalSpacing = context.getDimenPx(RDesign.attr.offset_3xs)
        updatePadding(top = verticalSpacing, bottom = verticalSpacing)
        this@AudioMessageView.addView(this)
    }

    private val smileView = ImageView(context).apply {
        id = R.id.cloud_view_message_block_audio_message_smile_view_id
        val paddingEnd = resources.getDimensionPixelSize(R.dimen.design_audio_message_view_smile_padding_end)
        val size = context.getDimenPx(RDesign.attr.inlineHeight_xs)
        layoutParams = LayoutParams(size, size)
        translationX -= resources.getDimensionPixelSize(R.dimen.design_audio_message_view_smile_translation_x).toFloat()
        isVisible = false
        updatePadding(right = paddingEnd + translationX.toInt())
        this@AudioMessageView.addView(this)
    }

    /**
     * Кнопка удаления аудиосообщения.
     */
    private var deleteButton: TextLayout? = null

    private val recognizeContainer: ViewGroup
    private val recognitionProgress: View
    private val recognizedTextView: SbisTextView
    private val expandRecognizeIconView: SbisTextView
    private val expandRecognizeClickArea: View

    /**
     * Признак для предотвращения одновременного срабатывания onClick и onLongClick.
     */
    private var isLongClickPerformed = false

    /**
     * Аниматор разворачивания/сворачивания расшифровки.
     */
    private var stateAnimator: ValueAnimator? = null

    /**
     * Эмоция для аудиосообщения. Будет показан соответствующий смайлик [AudioMessageEmotion].
     */
    private var messageEmotion: AudioMessageEmotion = AudioMessageEmotion.DEFAULT
        set(value) {
            val isChanged = field != value
            field = value
            if (!isChanged) return
            smileView.isVisible = field.drawableResId?.also(smileView::setImageResource) != null
        }

    private var isExpanded: Boolean = false
        set(value) {
            val changed = field != value
            field = value
            data?.isExpanded = value
            if (changed) {
                recognizedTextView.maxLines = if (field) EXPANDED_MAX_LINES else COLLAPSED_MAX_LINES
                expandRecognizeIconView.rotationX = if (field) EXPANDED_ROTATION else COLLAPSED_ROTATION
            }
        }

    override var data: AudioMessageViewData? = null
        set(value) {
            val changed = value != field
            field = value
            value?.apply {
                this@AudioMessageView.isExpanded = isExpanded
                if (changed) {
                    messageEmotion = emotion
                    audioPlayerView.data = playerData
                    setRecognize(recognizedText, recognized)
                    safeRequestLayout()
                }
            }
        }

    override var actionListener: MediaMessage.ActionListener? = null
        set(value) {
            field = value
            audioPlayerView.setListener(value)
        }

    override var onDeleteClickListener: OnClickListener? = null
        set(value) {
            val isChanged = field != null && value == null || (field == null && value != null)
            field = value
            if (isChanged) {
                if (value != null) initDeleteButton()
                deleteButton?.configure { isVisible = value != null }
                deleteButton?.setOnClickListener { _, _ -> value?.onClick(this) }
                safeRequestLayout()
            }
        }

    override var isCard: Boolean = true
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) updateCardBackground()
        }

    init {
        setWillNotDraw(false)
        LayoutInflater.from(context).inflate(R.layout.design_audio_message_view, this, true).apply {
            recognizeContainer = findViewById(R.id.design_audio_message_view_recognize_container)
            recognitionProgress = findViewById(R.id.design_audio_message_view_recognition_in_progress)
            recognizedTextView = findViewById(R.id.design_audio_message_view_recognized_text_view)
            expandRecognizeIconView = findViewById(R.id.design_audio_message_view_expand_recognized_text_icon)
            expandRecognizeClickArea = findViewById(R.id.design_audio_message_view_expand_recognized_text_click_area)
        }

        expandRecognizeClickArea.setOnClickListener {
            if (isLongClickPerformed) {
                isLongClickPerformed = false
                return@setOnClickListener
            }
            onExpandAreaClick()
        }
        recognizedTextView.setOnClickListener {
            if (isLongClickPerformed) {
                isLongClickPerformed = false
                return@setOnClickListener
            }
            onExpandAreaClick()
        }

        if (isCard) updateCardBackground()
        if (background == null) setBackgroundColor(getColorFromAttr(com.google.android.material.R.attr.backgroundColor))
    }

    override fun setMediaPlayer(mediaPlayer: MediaPlayer) {
        audioPlayerView.setMediaPlayer(mediaPlayer)
    }

    override fun recycle() {
        data = null
        actionListener = null
        audioPlayerView.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val horizontalPadding = paddingStart + paddingEnd
        val contentWidth = width - horizontalPadding
        smileView.safeMeasure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        val playerWidth = contentWidth - smileView.safeMeasuredWidth - (deleteButton?.width ?: 0)
        audioPlayerView.measure(makeExactlySpec(playerWidth), makeUnspecifiedSpec())
        recognizeContainer.measure(makeExactlySpec(contentWidth), makeUnspecifiedSpec())
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        )
    }

    override fun getSuggestedMinimumWidth(): Int =
        maxOf(
            super.getSuggestedMinimumWidth(),
            smileView.safeMeasuredWidth
                .plus(audioPlayerView.measuredWidth)
                .plus(deleteButton?.width ?: 0)
                .plus(paddingStart)
                .plus(paddingEnd)
        )

    override fun getSuggestedMinimumHeight(): Int =
        maxOf(
            super.getSuggestedMinimumHeight(),
            audioPlayerView.measuredHeight
                .plus(recognizeContainer.safeMeasuredHeight)
                .plus(paddingTop)
                .plus(paddingBottom)
        )

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val smileTop = paddingTop + if (smileView.isVisible) {
            (audioPlayerView.measuredHeight - smileView.safeMeasuredHeight) / 2
        } else 0
        deleteButton?.also {
            val top = paddingTop + (audioPlayerView.measuredHeight - deleteButton!!.height) / 2
            it.layout(paddingStart, top)
        }
        smileView.safeLayout(deleteButton?.right ?: paddingStart, smileTop)
        audioPlayerView.layout(smileView.right, paddingTop)
        recognizeContainer.safeLayout(paddingStart, audioPlayerView.bottom)
    }

    override fun getBaseline(): Int =
        paddingTop + audioPlayerView.baseline

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        deleteButton?.onTouch(this, event) == true || super.onTouchEvent(event)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        deleteButton?.draw(canvas)
    }

    private fun setRecognize(text: CharSequence?, recognized: Boolean?) {
        if (recognized != null) {
            recognizeContainer.isVisible = !text.isNullOrEmpty()
            recognizedTextView.text = text
            if (recognized) {
                recognizedTextView.setTextColor(context.getThemeColorInt(RDesign.attr.textColor))
                recognizedTextView.alpha = RECOGNIZED_TEXT_ALPHA
            } else {
                recognizedTextView.setTextColor(context.getThemeColorInt(RDesign.attr.unaccentedTextColor))
                recognizedTextView.alpha = NOT_RECOGNIZED_TEXT_ALPHA
            }
            if (!text.isNullOrBlank()) {
                updateExpandIcon()
            } else {
                expandRecognizeIconView.isInvisible = true
                expandRecognizeClickArea.isInvisible = true
            }
            recognizedTextView.isVisible = true
            recognitionProgress.isVisible = false
        } else {
            expandRecognizeIconView.isInvisible = true
            expandRecognizeClickArea.isInvisible = true
            recognizedTextView.isVisible = false
            recognitionProgress.isVisible = true
        }
    }

    private fun updateExpandIcon() {
        // этот вызов safeRequestLayout() нужен чтобы recognizedTextView.layout не выдал null
        recognizedTextView.safeRequestLayout()
        expandRecognizeClickArea.safeRequestLayout()

        recognizedTextView.doOnNextLayout {
            val layout = recognizedTextView.layout ?: return@doOnNextLayout
            val lineCount = layout.lineCount
            if (lineCount > 0) {
                val ellipsized = layout.getEllipsisCount(lineCount - 1) > 0
                val expandedState = lineCount > COLLAPSED_MAX_LINES
                val showIcon = ellipsized || expandedState
                expandRecognizeIconView.isInvisible = !showIcon
                expandRecognizeClickArea.isInvisible = !showIcon
            } else {
                expandRecognizeIconView.isInvisible = true
                expandRecognizeClickArea.isInvisible = true
            }
        }
    }

    private fun onExpandAreaClick() {
        if (!expandRecognizeIconView.isVisible || !recognizedTextView.isClickable) {
            this.callOnClick()
            return
        }
        stateAnimator?.cancel()
        val currentHeight = recognizedTextView.height

        isExpanded = !isExpanded

        val needTryScroll = actionListener?.onExpandClicked(isExpanded) ?: true
        val container = findViewParent<View>(this) { it is MediaMessageExpandableContainer } ?: this
        val scrollingView: ViewGroup? = if (needTryScroll) {
            findViewParent(this) { it is ScrollingView || it is ScrollView }
        } else {
            null
        }
        val needScroll = scrollingView != null

        recognizedTextView.updateLayoutParams<FrameLayout.LayoutParams> { height = LayoutParams.WRAP_CONTENT }
        recognizedTextView.safeMeasure(makeExactlySpec(recognizedTextView.measuredWidth), makeUnspecifiedSpec())
        val newHeight = recognizedTextView.measuredHeight

        recognizedTextView.updateLayoutParams<FrameLayout.LayoutParams> { height = currentHeight }
        if (!isExpanded) recognizedTextView.maxLines = EXPANDED_MAX_LINES

        stateAnimator = ValueAnimator.ofInt(currentHeight, newHeight)
        stateAnimator?.duration = EXPAND_COLLAPSE_DURATION
        stateAnimator?.addUpdateListener { valueAnimator ->
            recognizedTextView.updateLayoutParams<FrameLayout.LayoutParams> {
                val animatedValue = valueAnimator.animatedValue as Int
                if (needScroll) {
                    scrollingView?.scrollByViewPosition(
                        container,
                        animatedValue - recognizedTextView.height
                    )
                }
                height = animatedValue
            }
        }
        stateAnimator?.addListener(
            onEnd = {
                if (!isExpanded) recognizedTextView.maxLines = COLLAPSED_MAX_LINES
                recognizedTextView.updateLayoutParams<FrameLayout.LayoutParams> { height = LayoutParams.WRAP_CONTENT }
            }
        )
        stateAnimator?.start()
    }

    private fun updateCardBackground() {
        clipToOutline = isCard
        if (isCard) {
            val corners = context.getDimenPx(RDesign.attr.borderRadius_2xs)
            val defaultCardSpacing = context.getDimenPx(RDesign.attr.offset_xs)
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, corners.toFloat())
                }
            }
            updatePadding(left = defaultCardSpacing, bottom = defaultCardSpacing, right = defaultCardSpacing)
        } else {
            setPadding(0)
            outlineProvider = null
        }
    }

    override fun setOnLongClickListener(listener: OnLongClickListener?) {
        super.setOnLongClickListener(listener)
        val longClickListener = listener?.let {
            OnLongClickListener {
                isLongClickPerformed = true
                listener.onLongClick(this)
            }
        }
        expandRecognizeClickArea.setOnLongClickListener(longClickListener)
        recognizedTextView.setOnLongClickListener(longClickListener)
    }

    private fun initDeleteButton() {
        deleteButton = TextLayout.createTextLayoutByStyle(
            context,
            R.style.AudioMessageDeleteButtonStyle
        ).apply {
            makeClickable(this@AudioMessageView)
        }
    }
}

private const val EXPANDED_MAX_LINES = Int.MAX_VALUE
private const val COLLAPSED_MAX_LINES = 2
private const val EXPANDED_ROTATION = 180f
private const val COLLAPSED_ROTATION = 0f
private const val EXPAND_COLLAPSE_DURATION = 250L
private const val NOT_RECOGNIZED_TEXT_ALPHA = 1f
private const val RECOGNIZED_TEXT_ALPHA = 0.8f