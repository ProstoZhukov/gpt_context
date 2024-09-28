package ru.tensor.sbis.design.chips.models

import ru.tensor.sbis.design.theme.global_variables.InlineHeight

/**
 * Модель конфигурации компонента.
 *
 * @author ps.smirnyh
 */
data class SbisChipsConfiguration(

    /** Режим выбора элементов. */
    var selectionMode: SbisChipsSelectionMode = SbisChipsSelectionMode.Single,

    /** Размещение элементов в одну или несколько строк. */
    var multiline: Boolean = false,

    /** Выключенное состояние элементов. */
    var readOnly: Boolean = false,

    /** Стиль фона с возможностью выбора режима фона выбранного элемента. */
    var style: SbisChipsBackgroundStyle = SbisChipsBackgroundStyle.Accented(SbisChipsStyle.DEFAULT),

    /** Режим отображения фона элементов. */
    var viewMode: SbisChipsViewMode = SbisChipsViewMode.FILLED,

    /** Размер элементов. */
    var size: InlineHeight = InlineHeight.X3S
)
