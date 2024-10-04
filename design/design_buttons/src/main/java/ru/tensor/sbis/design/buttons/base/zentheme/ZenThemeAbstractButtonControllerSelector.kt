package ru.tensor.sbis.design.buttons.base.zentheme

import ru.tensor.sbis.design.buttons.base.AbstractSbisButton
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.theme.zen.ZenThemeModel

/**
 * Контракт селектора контроллеров Дзен темизации для кнопок одного типа.
 *
 * @author ra.geraskin
 */
internal abstract class ZenThemeAbstractButtonControllerSelector<T : AbstractSbisButton<*, *>> : ButtonZenThemeSupport {

    /**
     * Экземпляр кнопки контроллера.
     */
    internal lateinit var button: T

    /**
     * Список возможных контроллеров.
     */
    internal abstract val controllers: List<ZenThemeAbstractButtonController>

    /**
     * Инициализировать селектор контроллеров.
     */
    internal fun attach(button: T) {
        this.button = button
        controllers.forEach {
            it.attach(button)
        }
    }

    /**
     * Получить от наследника экземпляр контроллера в зависимости от параметров кнопки. Условие выбора должно быть
     * реализовано на стороне каждого потомка.
     */
    abstract fun getZenButtonController(button: T): ZenThemeAbstractButtonController?

    /** @SelfDocumented */
    override fun setZenTheme(themeModel: ZenThemeModel) {
        getZenButtonController(button)?.let { controller ->
            controller.setZenTheme(themeModel)
            button.invalidate()
        }
    }

    /** @SelfDocumented */
    override fun setZenThemeForced(themeModel: ZenThemeModel, style: SbisButtonStyle) {
        getZenButtonController(button)?.let { controller ->
            controller.setZenThemeForced(themeModel, style)
            button.invalidate()
        }
    }

}