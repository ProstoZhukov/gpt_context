/**
 * Расширения EditText
 *
 * @author ps.smirnyh
 */
package ru.tensor.sbis.design.utils.extentions

import android.text.TextUtils
import android.text.method.KeyListener
import android.widget.EditText
import ru.tensor.sbis.design.utils.KeyboardUtils

/**
 * Метод для ellipsize в зависимости от фокуса.
 * При [hasFocus] true - присваивается [actualKeyListener] в [EditText.setKeyListener],
 * убирается ellipsize и поднимается клавиатура,
 * иначе выставляется ellipsize [TextUtils.TruncateAt.END] и зануляется [EditText.setKeyListener].
 *
 * При ellipsize не вызывается [EditText.onTextChanged] и [EditText.getText] остается неизменным.
 *
 * У [EditText] должен быть [EditText.setSingleLine] true.
 *
 * @param hasFocus наличие фокуса у [EditText].
 * @param actualKeyListener [KeyListener], который будет установлен при наличии фокуса.
 * @param showKeyboard нужно ли поднимать клавиатуру после получения фокуса.
 * Зависит от параметра [EditText.getShowSoftInputOnFocus].
 *
 * @author ps.smirnyh
 */
fun EditText.ellipsizeOnFocusChange(hasFocus: Boolean, actualKeyListener: KeyListener, showKeyboard: Boolean = true) {
    if (hasFocus) {
        // Не устанавливаем повторно, если ничего не изменилось
        if (keyListener == actualKeyListener && ellipsize == null) return
        // Устанавливаем keyListener такой же, какой был до ellipsize
        keyListener = actualKeyListener
        // Убираем ellipsize, чтобы вернуть полную версию текста
        ellipsize = null
        // Нужно поднять клавиатуру вручную, т.к. при клике keyListener отсутствовал и сама она не поднимется
        if (showKeyboard) {
            if (showSoftInputOnFocus) KeyboardUtils.showKeyboard(this)
            // Нужно для запуска мигания курсора
            onWindowFocusChanged(true)
        }
    } else {
        // Убираем keyListener чтобы отработал стандартный механизм ellipsize в DynamicLayout.
        keyListener = null
        ellipsize = TextUtils.TruncateAt.END
    }
}
