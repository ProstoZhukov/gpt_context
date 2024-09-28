package ru.tensor.sbis.design.view.input.base

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

/**
 * Слушатель событий касания по полю ввода - обёртка над внешним слушателем.
 * @param baseInputView поле ввода для передачи в слушателе.
 *
 * @author mv.ilin
 */
internal class InputViewTouchListener(
    private val baseInputView: BaseInputView
) : View.OnTouchListener {
    /**
     * Внешний слушатель [View.OnClickListener].
     */
    var outer: View.OnTouchListener? = null

    /**
     * Передаёт во внешний слушатель экземпляр [BaseInputView], а не [android.widget.EditText]
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return outer?.onTouch(baseInputView, event) ?: false
    }
}