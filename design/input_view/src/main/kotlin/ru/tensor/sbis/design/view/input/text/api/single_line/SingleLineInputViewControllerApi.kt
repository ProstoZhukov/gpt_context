package ru.tensor.sbis.design.view.input.text.api.single_line

import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.view.input.base.api.BaseInputViewControllerApi

/**
 * Api класса логики однострочных полей ввода.
 *
 * @author ps.smirnyh
 */
internal interface SingleLineInputViewControllerApi :
    BaseInputViewControllerApi,
    SingleLineInputViewApi {

    /**
     * Кнопка-ссылка.
     */
    val linkView: TextLayout

    /**
     * Обновляет видимость внутренних элементов.
     * @param isProgressVisible true если прогресс видим, иначе false.
     * @param isClearVisible true если должна быть кнопка очистки, иначе false.
     * @param isReadOnly true если поле только для чтения, иначе false.
     */
    fun updateInternalVisibility(
        isProgressVisible: Boolean,
        isClearVisible: Boolean,
        isReadOnly: Boolean
    ): Boolean
}