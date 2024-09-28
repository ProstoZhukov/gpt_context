package ru.tensor.sbis.main_screen.widget.view.fabcontainer

import ru.tensor.sbis.design.buttons.SbisRoundButton

/**
 * Кнопка, лениво создаваемая при обращении, с возможностью также обратиться к текущему значению, если оно есть, не
 * вызывая создания.
 *
 * @author us.bessonov
 */
internal class LateInitSbisRoundButton(
    private val createButton: () -> SbisRoundButton? = { null }
) {

    private var buttonValue: SbisRoundButton? = null

    /**
     * Получить кнопку, создав её, если это не было сделано ранее.
     */
    val button: SbisRoundButton?
        get() = buttonValue
            ?: createButton().also { buttonValue = it }

    /**
     * Получить текущий экземпляр кнопки, если он был создан.
     */
    val peekButton: SbisRoundButton?
        get() = buttonValue

    /**
     * Очистить ссылку на view кнопки.
     */
    fun clear() {
        buttonValue = null
    }

}