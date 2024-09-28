package ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified

import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale

/** Интерфейс содержащий информацию для корректного масштабировании пользовательского интерфейса. */
internal interface SpecifiedScaleFactor {

    /**
     * Возвращает специализированный коэффициент масштабирования для текущей темы [currentThemeScale].
     *
     * Дополнительно передаем указанное значение для темы [currentThemeScaleValue],
     * чтобы была возможность переопределять только конкретные темы.
     * */
    fun getSpecifiedScaleFactor(
        currentThemeScale: InterfaceScale,
        currentThemeScaleValue: Float
    ): Float

    /**
     * При установке значения true.
     * Определяет устройство в разряд "компактных" с "маленькой" диагональю, низким разрешением или плотностью пикселей.
     * Для таких устройств уменьшены коэффициенты масштабирования и урезана линейка масштабов.
     */
    fun isCompactDevice(): Boolean
}