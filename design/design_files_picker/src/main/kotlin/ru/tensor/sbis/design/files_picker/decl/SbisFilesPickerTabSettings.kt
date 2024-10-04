package ru.tensor.sbis.design.files_picker.decl

import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.theme.res.SbisColor
import com.google.android.material.R as RMat

val defaultBackgroundColor = SbisColor.Attr(RMat.attr.backgroundColor)

/**
 * Настройка панели выбора файлов для таба
 *
 * @property backgroundColor    Цвет фоно заголовка шторки.
 * @property menu               Контекстное меню по трём точкам. Если null, то три точки отображаться не будут.
 *
 * @author ai.abramenko
 */
class SbisFilesPickerTabSettings(
    val backgroundColor: SbisColor = defaultBackgroundColor,
    val menu: SbisMenu? = null
)