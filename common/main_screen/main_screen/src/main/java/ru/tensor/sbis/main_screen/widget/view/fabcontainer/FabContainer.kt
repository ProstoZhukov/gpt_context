package ru.tensor.sbis.main_screen.widget.view.fabcontainer

import android.app.Activity
import ru.tensor.sbis.design.buttons.SbisRoundButton

/**
 * Предоставляет view плавающих кнопок для главного экрана.
 *
 * @author us.bessonov
 */
interface FabContainer {

    /**
     * Основная кнопка (плюсик).
     */
    val mainFab: SbisRoundButton

    /**
     * Кнопка с текущей датой.
     */
    val todayExtraFab: SbisRoundButton?

    /** @SelfDocumented */
    val extraFab: SbisRoundButton?

    /** @SelfDocumented */
    val extraFab2: SbisRoundButton?

    /** @SelfDocumented */
    val extraFab3: SbisRoundButton?

    /** @SelfDocumented */
    val extraFab4: SbisRoundButton?

    /**
     * [mainFab], значение которого не инициализируется при запросе.
     */
    val peekMainFab: SbisRoundButton?

    /**
     * [todayExtraFab], значение которого не инициализируется при запросе.
     */
    val peekTodayExtraFab: SbisRoundButton?

    /**
     * [extraFab], значение которого не инициализируется при запросе.
     */
    val peekExtraFab: SbisRoundButton?

    /**
     * [extraFab2], значение которого не инициализируется при запросе.
     */
    val peekExtraFab2: SbisRoundButton?

    /**
     * [extraFab3], значение которого не инициализируется при запросе.
     */
    val peekExtraFab3: SbisRoundButton?

    /**
     * [extraFab4], значение которого не инициализируется при запросе.
     */
    val peekExtraFab4: SbisRoundButton?

    /** @SelfDocumented */
    fun setActivity(activity: Activity)

    /**
     * Сбросить все ссылки на view кнопок.
     */
    fun clear()
}