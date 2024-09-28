package ru.tensor.sbis.red_button.feature

import androidx.activity.ComponentActivity
import io.reactivex.Single
import ru.tensor.sbis.red_button.data.RedButtonStubType
import ru.tensor.sbis.red_button.interactor.RedButtonPreferencesInteractor
import ru.tensor.sbis.red_button.interactor.RedButtonReRunAppInteractor
import ru.tensor.sbis.red_button.interactor.RedButtonStatesInteractor
import ru.tensor.sbis.red_button.interactor.RedButtonStubInteractor
import ru.tensor.sbis.red_button.utils.RedButtonOpenHelper

/**
 * Реализация фич модуля "Красной Кнопки".
 *
 * @author ra.stepanov
 */
class RedButtonFeatureImpl(
    private val openHelper: RedButtonOpenHelper,
    private val preferencesInteractor: RedButtonPreferencesInteractor,
    private val reRunAppInteractor: RedButtonReRunAppInteractor,
    private val stubInteractor: RedButtonStubInteractor,
    private val statesInteractor: RedButtonStatesInteractor
) : RedButtonFeature {

    override fun subscribeOnRedButtonControllerCallback() {
        reRunAppInteractor.subscribeOnAppReRun()
    }

    override fun isRedButtonActivated(): Single<Boolean> = statesInteractor.isButtonActivated()

    override fun isLockedUi(): Single<Boolean> =
        preferencesInteractor.getStubPreference().map { it != RedButtonStubType.NO_STUB }

    override fun openStubIfNeeded(activity: ComponentActivity, noStubHandler: () -> Unit) {
        stubInteractor.openStubIfNeedOrRunCode(
            activity,
            { openHelper.openRedButtonStub(activity, it) },
            noStubHandler
        )
    }
}