package ru.tensor.sbis.design_dialogs.dialogs.content

/**
 * Интерфейс контента. Должен быть реализован наследником [androidx.fragment.app.Fragment].
 *
 * @author sa.nikitin
 */
interface Content {

    /**
     * Обработать нажатие на кнопку "назад".
     */
    fun onBackPressed(): Boolean

    /**
     * Обработать событие закрытия контента.
     */
    fun onCloseContent() = Unit

    /**
     * Интерфейс слушателя анимации отображения содержимого.
     */
    interface ShowAnimationListener {

        /**
         * Обработать начало анимации отображения.
         */
        fun onStartShowAnimation()

        /**
         * Обработать завершение анимации отображения.
         */
        fun onFinishShowAnimation()

    }

}
