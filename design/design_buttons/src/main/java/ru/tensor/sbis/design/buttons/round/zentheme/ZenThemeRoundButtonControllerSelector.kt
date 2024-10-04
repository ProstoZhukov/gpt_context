package ru.tensor.sbis.design.buttons.round.zentheme

import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.SbisTranslucentRoundButton
import ru.tensor.sbis.design.buttons.base.zentheme.ZenThemeAbstractButtonControllerSelector
import ru.tensor.sbis.design.buttons.base.zentheme.ZenThemeAbstractButtonController
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType
import ru.tensor.sbis.design.buttons.translucent.zentheme.ZenThemeTranslucentButtonController

/**
 * Общий контроллер Дзен темизации для круглых кнопок. В зависимости от параметров кнопки будет выбран соответствующий
 * контроллер Дзен темизации.
 *
 * @author ra.geraskin
 */
internal class ZenThemeRoundButtonControllerSelector : ZenThemeAbstractButtonControllerSelector<SbisRoundButton>() {

    /** @SelfDocumented */
    override val controllers: List<ZenThemeAbstractButtonController> by lazy(LazyThreadSafetyMode.NONE) {
        listOf(
            ZenThemeTranslucentButtonController(),
            ZenThemeFilledRoundButtonController(),
            ZenThemeTransparentRoundButtonController()
        )
    }

    /** @SelfDocumented */
    override fun getZenButtonController(button: SbisRoundButton): ZenThemeAbstractButtonController? {
        val controllerClass = when {
            button is SbisTranslucentRoundButton -> ZenThemeTranslucentButtonController::class.java
            button.type == SbisRoundButtonType.Filled -> ZenThemeFilledRoundButtonController::class.java
            button.type == SbisRoundButtonType.Transparent -> ZenThemeTransparentRoundButtonController::class.java
            else -> return null
        }
        return controllers.filterIsInstance(controllerClass).firstOrNull()
    }
}