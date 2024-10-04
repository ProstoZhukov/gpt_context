package ru.tensor.sbis.design.selection.ui.utils.fixed_button

import androidx.annotation.LayoutRes
import ru.tensor.sbis.design.selection.R

/**
 * Варианты поддерживаемых "Фиксированных кнопок"
 *
 * @author ma.kolpakov
 */
internal enum class FixedButtonType(
    @LayoutRes val buttonLayout: Int
) {

    /**
     * Кнопка "Выбрать все" (пример: выбор регионов)
     */
    CHOOSE_ALL(R.layout.selection_choose_all_button),

    /**
     * Кнопка "Новая группа" (пример: реестр чатов)
     */
    CREATE_GROUP(R.layout.selection_fixed_button_panel)
}