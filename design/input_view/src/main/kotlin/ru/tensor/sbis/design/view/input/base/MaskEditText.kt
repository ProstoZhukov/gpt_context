package ru.tensor.sbis.design.view.input.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.delegateNotEqual
import ru.tensor.sbis.design.utils.findViewParent
import ru.tensor.sbis.design.view.input.mask.MaskSymbol
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * Класс для обработки поведения постановки курсора в начало при пустой статической маске.
 *
 * @author ps.smirnyh
 */
internal class MaskEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private val inputManager: InputMethodManager?
        get() = ContextCompat.getSystemService(context, InputMethodManager::class.java)

    /**
     * Текст пустой статической маски для проверки постановки курсора в начало
     */
    private var emptyMask: String? = null

    /**
     * Флаг для включения механизма перемещения каретки при первом тапе
     */
    private var isFirstTouch = false

    private val isSelectAll: Boolean
        get() = selectionStart == 0 && selectionEnd == length()

    /**
     * Режим работы selection при первом тапе
     */
    internal var isSelectAllOnBeginEditing by delegateNotEqual(false) { value ->
        setSelectAllOnFocus(value)
    }

    internal var moveCursorToEndPosition = true

    internal var clearFocusOnBackPressed = false

    /**
     * Текст маски для получения текста пустой маски
     */
    internal var mask: String? = null
        set(value) {
            field = value
            emptyMask = field?.map { char ->
                val maskSymbol = MaskSymbol.getByChar(char)
                if (maskSymbol != MaskSymbol.FIXED) {
                    maskSymbol.placeholder
                } else {
                    char
                }
            }?.let { String(it.toCharArray()) }
            minimumWidth = paint.measureText(field).roundToInt()
        }

    /** Callback для обновления ellipsize поля ввода. */
    internal var updateEllipsis: (() -> Unit)? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            importantForAutofill = IMPORTANT_FOR_AUTOFILL_NO
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)

        // Постановка курсора в начало при пустой статической маске
        if (isFirstTouch) {
            emptyMask?.let {
                if (text?.toString() == it && isFocused && !isSelectAll) {
                    setSelection(0)
                }
            }
        }
    }

    // Вызывается 2 раза. На второй вызов меняется фокус и переставляется каретка
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val result: Boolean
        try {
            // Выполняем стандартные действия, в т.ч. изменяем фокус и переставляем каретку на место тапа
            result = super.onTouchEvent(event)
            // Устанавливаем каретку в конец, если нужно
            // Нужно вызывать после super.onTouchEvent, чтобы система не изменила нашу позицию каретки
            moveToEndCursor()
            // Отключаем механизм перемещения каретки в конец
            isFirstTouch = false
        } catch (e: IndexOutOfBoundsException) {
            // Если будет краш как в https://dev.sbis.ru/opendoc.html?guid=528676ed-454b-47c1-85d2-174df05d9da0&client=3,
            // то мы его отловим и сможем найти место, где это произошло
            Timber.e(e, "InputView attach to ${findViewParent<BaseInputView>(this)?.parent}")
            return false
        }
        return result
    }

    // Вызывается на второй вызов onTouchEvent
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        updateEllipsis?.invoke()
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused && showSoftInputOnFocus) {
            inputManager?.showSoftInput(this, 0)
        }
        if (focused) {
            isFirstTouch = true
        }
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP && clearFocusOnBackPressed) {
            // Если нажали кнопку назад, то скрываем клавиатуру и убираем фокус
            KeyboardUtils.hideKeyboard(this)
            resetFocus()
            return true
        }
        return super.onKeyPreIme(keyCode, event)
    }

    override fun isSuggestionsEnabled(): Boolean = false

    /**
     * Используется кстомная реализация потому что андроид в некоторых версия API при сбросе фокуса ищет на какую вью
     * передать фокус, и если на экране ближайшая фокусабл вью это этот же едит текст то фокус выставится повторно
     */
    private fun resetFocus() {
        isFocusable = false
        isFocusable = true
        isFocusableInTouchMode = true
    }

    /**
     * Перемещение каретки в зависимости от режима начального редактирования при первом тапе
     */
    private fun moveToEndCursor() {
        if (isFirstTouch && !isSelectAllOnBeginEditing && moveCursorToEndPosition) {
            setSelection(length())
        }
    }
}