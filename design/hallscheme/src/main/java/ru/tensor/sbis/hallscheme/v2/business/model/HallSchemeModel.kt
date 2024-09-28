package ru.tensor.sbis.hallscheme.v2.business.model

import ru.tensor.sbis.hallscheme.v2.PlanTheme
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi

/**
 * Модель схемы зала.
 */
data class HallSchemeModel internal constructor(
    internal val background: Background, // Объект с информацией об отображении фона.
    internal val backgrounds: List<String>? = null, // Список всех доступных изображений.
    internal val backgroundRoom: String? = null, // Ссылка на картинку с фоном.
    internal val bottom: Int, // Нижняя крайняя координата границы схемы зала.
    internal val inscribing: Boolean = false, // Включен ли авторазмер.
    internal val items: MutableList<HallSchemeItemUi>, // Список элементов схемы зала.
    internal val left: Int, // Левая крайняя координата границы схемы зала.
    internal val pinTables: Boolean, // Привязывать ли столики к фону "default": true.
    internal val planTheme: PlanTheme = PlanTheme.THEME_FLAT, // Тип отображения схемы "default": "flat".
    internal val right: Int, // Правая крайняя координата границы схемы зала.
    internal val top: Int, // Верхняя крайняя координата границы схемы зала.
    internal val opacity: Float? = null, // Прозрачность элементов (decor, shape, text).
    internal val textureType: Int = 1, // Тип текстуры в диапазоне [1..9].
    internal val tableConfig: TableConfig, // Настройки отображения информации на столе.
    internal val zoom: Int, // Масштабирование зала в %.
    internal val zoomBackground: Int = 100  // Масштабирование фона в процентах.
)