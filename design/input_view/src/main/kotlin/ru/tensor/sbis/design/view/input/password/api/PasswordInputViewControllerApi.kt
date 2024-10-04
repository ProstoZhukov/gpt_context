package ru.tensor.sbis.design.view.input.password.api

import ru.tensor.sbis.design.view.input.password.ShowHideToggleClickListener
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewControllerApi

/**
 * Api класса логики для поля ввода пароля.
 *
 * @author ps.smirnyh
 */
internal interface PasswordInputViewControllerApi :
    SingleLineInputViewControllerApi,
    PasswordInputViewApi {

    val showHideToggleClickListener: ShowHideToggleClickListener
}