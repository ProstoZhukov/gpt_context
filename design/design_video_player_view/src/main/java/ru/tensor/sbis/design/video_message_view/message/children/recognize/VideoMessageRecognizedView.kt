package ru.tensor.sbis.design.video_message_view.message.children.recognize

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.video_message_view.R
import ru.tensor.sbis.design.video_message_view.message.children.recognize.drawables.LoadingDotsDrawable

/**
 * Вью для отображения облочка со статусом распознования текста видеосообщения.
 *
 * @author da.zhukov
 */
internal class VideoMessageRecognizedView(
    context: Context
) : ViewGroup(context) {

    /**
     * Высота вью.
     */
    private val currentHeight = resources.getDimensionPixelSize(R.dimen.video_message_recognized_view_height)

    /**
     * Ширина вью.
     */
    private val currentWidth = resources.getDimensionPixelSize(R.dimen.video_message_recognized_view_width)

    /**
     * Состояние распознанности видеосообщения.
     */
    var isRecognized: Boolean = false
        set(value) {
            val changed = field != value
            field = value
            if (changed) {
                loadingDotsDrawable.setVisible(visible = !value, restart = true)
                safeRequestLayout()
            }
        }

    /**
     * Иконка распознанного видеосообщения.
     */
    private val recognizedIcon = TextLayout.createTextLayoutByStyle(
        context,
        R.style.VideoMessageRecognizeIcon
    )

    /**
     * Drawable для отображения анимируемых точек загрузки.
     */
    private val loadingDotsDrawable = LoadingDotsDrawable().apply {
        params = LoadingDotsDrawable.DotsParams(size = dp(2), accentSize = dp(3))
        callback = this@VideoMessageRecognizedView
    }

    private val outcomeBackground by lazy {
        AppCompatResources.getDrawable(context, R.drawable.video_message_recognized_out_bg)
    }
    private val incomeBackground by lazy {
        AppCompatResources.getDrawable(context, R.drawable.video_message_recognized_in_bg)
    }

    init {
        setWillNotDraw(false)
        layoutParams = LayoutParams(currentWidth, currentHeight)
    }

    /**
     * Установить background относительно того является ли видеосообщение исходящим.
     */
    fun setOutcome(isOutcome: Boolean) {
        background = if (isOutcome) {
            outcomeBackground
        } else {
            incomeBackground
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        recognizedIcon.configure { isVisible = !loadingDotsDrawable.isVisible }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val recognizedIconLeftPos = (measuredWidth - recognizedIcon.width) / 2
        val recognizedIconTopPos = (measuredHeight - recognizedIcon.height) / 2
        val loadingDotsDrawableLeftPos = (measuredWidth - loadingDotsDrawable.intrinsicWidth) / 2
        val loadingDotsDrawableTopPos = (measuredHeight - loadingDotsDrawable.intrinsicHeight) / 2
        recognizedIcon.layout(recognizedIconLeftPos, recognizedIconTopPos)
        loadingDotsDrawable.setBounds(
            loadingDotsDrawableLeftPos,
            loadingDotsDrawableTopPos,
            loadingDotsDrawableLeftPos + loadingDotsDrawable.intrinsicWidth,
            loadingDotsDrawableTopPos + loadingDotsDrawable.intrinsicHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        recognizedIcon.draw(canvas)
        loadingDotsDrawable.draw(canvas)
    }

    override fun verifyDrawable(who: Drawable): Boolean =
        who == loadingDotsDrawable || super.verifyDrawable(who)

    override fun hasOverlappingRendering(): Boolean = false
}