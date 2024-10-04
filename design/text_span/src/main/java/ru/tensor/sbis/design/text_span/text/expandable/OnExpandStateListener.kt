package ru.tensor.sbis.design.text_span.text.expandable

/**
 * Интерфейс слушателя событий об изменении состояния расширяемой view.
 *
 * @author am.boldinov
 */
interface OnExpandStateListener {

    /**
     * Обработать изменение состояния расширяемой view.
     * @param manually  - было ли состояние изменено вручную.
     * @param expanded  - новое состояние view
     */
    fun onExpandStateChanged(manually: Boolean, expanded: Boolean)

}