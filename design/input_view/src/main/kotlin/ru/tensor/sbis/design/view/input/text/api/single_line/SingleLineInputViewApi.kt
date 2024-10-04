package ru.tensor.sbis.design.view.input.text.api.single_line

import android.view.View

/**
 * Класс для управления состоянием и внутренними компонентами однострочных полей ввода.
 *
 * @author ps.smirnyh
 */
interface SingleLineInputViewApi {

    /**
     * Текст кнопки-ссылки.
     */
    var linkText: String

    /**
     * Видимость кнопки-ссылки.
     * Принудительно скрывает кнопку-ссылку.
     * По умолчанию скрывается если нет текста в [linkText].
     * Не изменяется при скрытии кнопки-ссылки по стандартным правилам.
     */
    var isLinkVisible: Boolean

    /**
     * Слушатель клика по кнопке меню.
     */
    var onLinkClickListener: ((View) -> Unit)?

    /**
     * Отображение текста кнопки-ссылки в виде иконки.
     */
    var isFontIconLink: Boolean
}