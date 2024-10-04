package ru.tensor.sbis.design.message_panel.view.layout

import android.content.Context
import android.os.Build
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import androidx.core.view.updatePadding
import io.reactivex.Observable
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.common_views.sbisview.SbisEditTextWithHideKeyboardListener
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.message_panel.view.utils.ClipboardFilesProvider
import ru.tensor.sbis.design.utils.KeyboardUtils

/**
 * Реализация [SbisEditTextWithHideKeyboardListener] с динамическим паддингом для панели сообщений.
 * Вертикальный паддинг добавляется только в случае многострочного отображения,
 * чтобы сохранять минимальную высоту с динамическим размером шрифта.
 *
 * @author vv.chekurda
 */
class MessagePanelEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @StyleRes defStyleRes: Int = 0
) : SbisEditTextWithHideKeyboardListener(context, attrs, defStyleRes) {

    private var originalPaddingTop = 0
    private var originalPaddingBottom = 0
    private val maxTextSize = dp(MAX_TEXT_SIZE_DP).toFloat()
    private val minTextSize = dp(MIN_TEXT_SIZE_DP).toFloat()
    private val clipboardFilesProvider = ClipboardFilesProvider(getContext())

    /**
     * Признак блокировки ввода/установки текста.
     */
    var isInputLocked: Boolean = false

    init {
        updatePadding(top = paddingTop, bottom = paddingTop)
        setTextSize(COMPLEX_UNIT_PX, textSize)
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or
            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initCustomInsertionActionModeCallback()
        }
    }

    /**
     * Для подписки на Uri файлов, которые вставляются из буфера обмена.
     */
    val clipboardFileUri: Observable<String>
        get() = clipboardFilesProvider.clipboardFileUri

    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(
            COMPLEX_UNIT_PX,
            TypedValue.applyDimension(unit, size, resources.displayMetrics)
                .coerceAtMost(maxTextSize)
                .coerceAtLeast(minTextSize)
        )
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        when {
            text.isNullOrEmpty() -> Unit
            isInputLocked -> setText(EMPTY)
            else -> {
                if (clipboardFilesProvider.checkClipboardPastedContent(text, lengthBefore, lengthAfter)) {
                    setText(text.removeRange(lengthBefore, lengthAfter))
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec = if (maxHeight > 0) {
            MeasureSpecUtils.makeAtMostSpec(maxHeight)
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, heightSpec)
        val verticalPadding = paddingTop + paddingBottom
        updatePadding(top = originalPaddingTop, bottom = originalPaddingBottom)
        val verticalPaddingDiff = paddingTop + paddingBottom - verticalPadding
        setMeasuredDimension(measuredWidth, measuredHeight + verticalPaddingDiff)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        if (lineCount > 1 || minLines > 1) {
            super.setPadding(left, top, right, bottom)
        } else {
            originalPaddingTop = top
            originalPaddingBottom = bottom
            super.setPadding(left, 0, right, 0)
        }
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        val fileProviderResult = clipboardFilesProvider.onTextContextMenuItem(id)
        if (fileProviderResult == true) {
            // Единственный способ вставить картинку и не сбить курсор.
            clearFocus()
            requestFocus()
        }
        return fileProviderResult ?: super.onTextContextMenuItem(id)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? =
        super.onCreateInputConnection(outAttrs)?.let { superConnection ->
            clipboardFilesProvider.createInputConnection(superConnection, outAttrs)
        }

    @RequiresApi(Build.VERSION_CODES.M)
    fun initCustomInsertionActionModeCallback() {
        customInsertionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                KeyboardUtils.showKeyboard(this@MessagePanelEditText)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean = false
            override fun onDestroyActionMode(mode: ActionMode?) = Unit
        }
    }
}

private const val MAX_TEXT_SIZE_DP = 20
private const val MIN_TEXT_SIZE_DP = 16