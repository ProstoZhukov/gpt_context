package ru.tensor.sbis.design.view.input.base

import android.os.Build
import android.view.View
import ru.tensor.sbis.design.utils.KeyboardUtils

/**
 * Слушатель клика по полю ввода - обёртка над внешним слушателем.
 * @param baseInputView поле ввода для передачи в слушатель.
 *
 * @author ps.smirnyh
 */
internal class InputViewClickListener(
    private val baseInputView: BaseInputView
) : View.OnClickListener {
    /**
     * Внешний слушатель [View.OnClickListener].
     */
    var outer: View.OnClickListener? = null

    /**
     * Передаёт во внешний слушатель экземпляр [BaseInputView], а не [android.widget.EditText], а также содержит
     * исправление поднятия клавиатуры при клике.
     */
    override fun onClick(v: View?) {
        v ?: return
        /*
        На андроид 7.1.1 и ниже есть проблема с тем, что клавиатура не поднимается при клике на поле ввода.
        На версиях андроид выше 7.1.1 клавиатура поднимается без этой доработки.
         */
        if (!baseInputView.readOnly && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            KeyboardUtils.showKeyboard(v)
        }
        outer?.onClick(baseInputView)
    }
}