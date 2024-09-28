package ru.tensor.sbis.pin_code.view

import androidx.annotation.IdRes
import ru.tensor.sbis.pin_code.R

/**
 * Тип поля ввода кода
 *
 * @author mb.kruglova
 */
internal enum class CodeInputViewType(@IdRes val layoutRes: Int) {
    LIMITED_INPUT_VIEW(R.layout.pin_code_bubble_limited_input_view),
    DEFAULT_INPUT_VIEW(R.layout.pin_code_password_input_view),
    ACCESS_CODE_INPUT_VIEW(R.layout.pin_code_access_code_input_view)
}