package ru.tensor.sbis.design.view.input.password.api

import ru.tensor.sbis.design.view.input.password.PasswordInputView

/**
 * Api для поля ввода пароля [PasswordInputView].
 *
 * @author ps.smirnyh
 */
interface PasswordInputViewApi {

    /**
     * Безопасный режим ввода для отображения точек вместо символов при вводе.
     */
    var isSecureMode: Boolean
}