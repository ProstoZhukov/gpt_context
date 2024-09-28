package ru.tensor.sbis.wizard.decl.result

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.wizard.decl.WizardSteps

/**
 * Результат шагов мастера
 *
 * @property stepResults    Результаты завершившихся шагов
 *                          Если список пуст, то ещё ни один шаг не был пройден
 *
 * Результат шага с приведением к определённому типу можно получить с помощью [stepResult] и [stepResultOrNull]
 *
 * Если нужно удалить результат шага, то используйте [removeStepResult]
 * Это пригодиться при двойном возврате назад, т.е. с 3 шага вернуться на 1, минуя 2
 * Для этого в [WizardSteps.step] нужно вернуть 1 шаг и удалить предыдущий его результат через [removeStepResult]
 *
 * @author sa.nikitin
 */
interface StepsResult : Parcelable {

    val stepResults: List<Parcelable>

    fun addStepResult(stepResult: Parcelable): Boolean

    fun removeStepResult(stepResult: Parcelable): Boolean
}

/** @SelfDocumented */
@Parcelize
class StepsResultImpl(override val stepResults: MutableList<Parcelable> = mutableListOf()) : StepsResult {

    override fun addStepResult(stepResult: Parcelable) = stepResults.add(stepResult)

    override fun removeStepResult(stepResult: Parcelable) = stepResults.remove(stepResult)
}

/** @SelfDocumented */
inline fun <reified DATA> StepsResult.stepResult(): DATA =
    stepResultOrNull() ?: throw IllegalStateException("Step result with type ${DATA::class.java} does not exist")

/** @SelfDocumented */
inline fun <reified DATA> StepsResult.stepResultOrNull(): DATA? =
    stepResults.find { it is DATA } as DATA?