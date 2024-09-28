package ru.tensor.sbis.wizard.impl.state

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.wizard.decl.result.StepsResultImpl
import ru.tensor.sbis.wizard.decl.result.WizardResult
import ru.tensor.sbis.wizard.decl.step.Step

private const val STEP_TAG_PREFIX = "wizard_step_"

/**
 * Состояние презентера мастера
 *
 * @property result                 Результат работы мастера, т.е. результаты его шагов
 * @property currentStepMetadata    Метаданные текущего шага
 * @property wizardResult           Результат работы мастера
 *
 * @author sa.nikitin
 */
@Parcelize
internal class WizardPresenterState(
    val result: StepsResultImpl = StepsResultImpl(),
    var currentStepMetadata: StepMetadata? = null,
    var wizardResult: WizardResult? = null
) : Parcelable

/**
 * Метаданные шага
 *
 * @property step       Шаг
 * @property number     Порядковый номер шага
 * @property tag        Тэг шага (для фрагмента)
 *
 * @author sa.nikitin
 */
@Parcelize
internal data class StepMetadata(
    val step: Step,
    val number: Int
) : Parcelable {

    @IgnoredOnParcel
    val tag: String = STEP_TAG_PREFIX + step::class.java.simpleName
}