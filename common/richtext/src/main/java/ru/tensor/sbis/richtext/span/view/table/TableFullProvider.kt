package ru.tensor.sbis.richtext.span.view.table

import ru.tensor.sbis.richtext.util.Provider

/**
 * Поставщик данных для отображения полной таблицы в диалоговом окне.
 *
 * @property isShowing состояние отображения полной таблицы.
 * Необходимо для восстановления View при ее пересоздании.
 *
 * @author am.boldinov
 */
internal abstract class TableFullProvider : Provider<TableViewData> {

    @JvmField
    var isShowing = false

}