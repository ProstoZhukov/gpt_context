package ru.tensor.sbis.main_screen.widget.view.fabcontainer

import android.app.Activity
import android.view.ViewGroup
import ru.tensor.sbis.design.buttons.SbisRoundButton

/**
 * Реализация [FabContainer].
 *
 * @param getContainerView Лямбда для получения view контейнера для кнопок.
 *
 * @author us.bessonov
 */
internal class FabContainerImpl(
    private val getContainerView: (Activity) -> ViewGroup,
    createMainFab: (ViewGroup) -> SbisRoundButton,
    createTodayExtraFab: (ViewGroup) -> SbisRoundButton?,
    createExtraFab: (ViewGroup) -> SbisRoundButton?,
    createExtraFab2: (ViewGroup) -> SbisRoundButton?,
    createExtraFab3: (ViewGroup) -> SbisRoundButton?,
    createExtraFab4: (ViewGroup) -> SbisRoundButton?
) : FabContainer {

    private lateinit var activity: Activity

    private val container: ViewGroup
        get() = getContainerView(activity)

    private val mainFabLateInit = LateInitSbisRoundButton { createMainFab(container) }

    private val todayExtraFabLateInit = LateInitSbisRoundButton { createTodayExtraFab(container) }

    private val extraFabLateInit = LateInitSbisRoundButton { createExtraFab(container) }

    private val extraFab2LateInit = LateInitSbisRoundButton { createExtraFab2(container) }

    private val extraFab3LateInit = LateInitSbisRoundButton { createExtraFab3(container) }

    private val extraFab4LateInit = LateInitSbisRoundButton { createExtraFab4(container) }

    override val mainFab: SbisRoundButton
        get() = mainFabLateInit.button!!

    override val todayExtraFab: SbisRoundButton?
        get() = todayExtraFabLateInit.button

    override val extraFab: SbisRoundButton?
        get() = extraFabLateInit.button

    override val extraFab2: SbisRoundButton?
        get() = extraFab2LateInit.button

    override val extraFab3: SbisRoundButton?
        get() = extraFab3LateInit.button

    override val extraFab4: SbisRoundButton?
        get() = extraFab4LateInit.button

    override val peekMainFab: SbisRoundButton?
        get() = mainFabLateInit.peekButton

    override val peekTodayExtraFab: SbisRoundButton?
        get() = todayExtraFabLateInit.peekButton

    override val peekExtraFab: SbisRoundButton?
        get() = extraFabLateInit.peekButton

    override val peekExtraFab2: SbisRoundButton?
        get() = extraFab2LateInit.peekButton

    override val peekExtraFab3: SbisRoundButton?
        get() = extraFab3LateInit.peekButton

    override val peekExtraFab4: SbisRoundButton?
        get() = extraFab4LateInit.peekButton

    override fun setActivity(activity: Activity) {
        this.activity = activity
    }

    override fun clear() {
        listOf(
            mainFabLateInit,
            todayExtraFabLateInit,
            extraFabLateInit,
            extraFab2LateInit,
            extraFab3LateInit,
            extraFab4LateInit
        ).forEach { it.clear() }
    }
}