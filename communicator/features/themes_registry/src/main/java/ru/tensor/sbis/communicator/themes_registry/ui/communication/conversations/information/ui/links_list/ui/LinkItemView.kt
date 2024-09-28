package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.richtext.view.RichTextView

/**
 * Ячейка элемента списка ссылок на экране информации о диалоге/канале.
 * Нужна для того, чтобы предотвратить дефолтную обработку MotionEvent-ов самим RichTextView.
 *
 * @author dv.baranov
 */
internal class LinkItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val richText = RichTextView(context)
    private var convert: ((link: String) -> Spannable)? = null

    init {
        setWillNotDraw(false)
        addView(richText, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    }

    /** @SelfDocumented */
    fun setText(text: String) {
        richText.isVisible = false
        richText.text = text
    }

    /** Сохранить функцию конвертации строки в декорированную ссылку. */
    fun setConvertAction(action: (link: String) -> Spannable) {
        convert = action
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (convert != null) {
            findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                val link = withContext(Dispatchers.IO) {
                    convert!!(decorateLinkString(richText.text.toString()))
                }
                richText.text = link
                richText.isVisible = true
            }
        }
    }

    private fun decorateLinkString(url: String): String =
        "[[\"p\", {\"version\": \"2\"}, [\"a\", {\"href\": \"$url\", \"target\": \"_blank\"}, \"$url\"]]]"

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        convert = null
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = true

    @SuppressLint("ClickableViewAccessibility")
    override fun performClick(): Boolean {
        richText.apply {
            dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0f, 0f, 0))
            dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 0f, 0f, 0))
        }
        return true
    }
}
