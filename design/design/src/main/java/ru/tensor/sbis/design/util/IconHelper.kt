package ru.tensor.sbis.design.util

import ru.tensor.sbis.design.SbisMobileIcon
import timber.log.Timber

private const val SBIS_MOBILE_ICON_PREFIX = "smi_"

/**
 * Объект для работы с иконками из шрифта в приложении.
 *
 * @author ve.arefev
 */
object IconHelper {

    private val sbisMobileIcon = SbisMobileIcon()

    /**
     * Функция для получения фактического символа иконки по идентификатору.
     * В случае если символ иконки не найден, возвращается пустая строка
     *
     * @param name идентификатор иконки.
     */
    @SuppressWarnings("deprecation")
    fun getIcon(name: String): String =
        getIconModel(name)?.character?.toString() ?: ""

    /**
     * Функция для получения фактического объекта иконки по идентификатору.
     * В случае если объект иконки не найден, возвращается null
     *
     * @param name идентификатор иконки.
     */
    fun getIconModel(name: String): SbisMobileIcon.Icon? =
        try {
            val fullName = if (name.startsWith(SBIS_MOBILE_ICON_PREFIX)) name else SBIS_MOBILE_ICON_PREFIX + name
            sbisMobileIcon.getIcon(fullName) as? SbisMobileIcon.Icon
        } catch (exception: Exception) {
            Timber.w(exception, "Failed get icon $name")
            null
        }
}