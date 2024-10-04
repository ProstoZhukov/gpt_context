package ru.tensor.sbis.design.buttons.button.zentheme

import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisTranslucentButton
import ru.tensor.sbis.design.buttons.base.zentheme.ZenThemeAbstractButtonControllerSelector
import ru.tensor.sbis.design.buttons.base.zentheme.ZenThemeAbstractButtonController
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground.Contrast
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground.Default
import ru.tensor.sbis.design.buttons.translucent.zentheme.ZenThemeTranslucentButtonController
import ru.tensor.sbis.design.theme.global_variables.BorderThickness

/**
 * Общий контроллер Дзен темизации для обычных [кнопок][SbisButton]. В зависимости от параметров кнопки будет выбран
 * соответствующий контроллер Дзен темизации.
 *
 * @author ra.geraskin
 */
internal class ZenThemeButtonControllerSelector : ZenThemeAbstractButtonControllerSelector<SbisButton>() {

    /** @SelfDocumented */
    override val controllers: List<ZenThemeAbstractButtonController> by lazy(LazyThreadSafetyMode.NONE) {
        listOf(
            ZenThemeTranslucentButtonController(),
            ZenThemeFilledButtonController(),
            ZenThemeBorderButtonController(BorderThickness.S.getDimenPx(button.context))
        )
    }

    /** @SelfDocumented */
    override fun getZenButtonController(button: SbisButton): ZenThemeAbstractButtonController? {
        val controllerClass = when {
            button is SbisTranslucentButton -> ZenThemeTranslucentButtonController::class.java
            button.backgroundType == Contrast -> ZenThemeFilledButtonController::class.java
            button.backgroundType == Default -> ZenThemeBorderButtonController::class.java
            else -> return null
        }
        return controllers.filterIsInstance(controllerClass).firstOrNull()
    }

}