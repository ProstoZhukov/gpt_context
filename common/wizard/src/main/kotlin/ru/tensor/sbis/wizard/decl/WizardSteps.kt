package ru.tensor.sbis.wizard.decl

import android.os.Parcelable
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.wizard.decl.result.FinalStepResultData
import ru.tensor.sbis.wizard.decl.result.PreviousStepResult
import ru.tensor.sbis.wizard.decl.result.StepResult
import ru.tensor.sbis.wizard.decl.result.StepsResult
import ru.tensor.sbis.wizard.decl.step.Step

/**
 * Шаги мастера
 *
 * @author sa.nikitin
 */
interface WizardSteps : Parcelable {

    /**
     * Запрос шага
     *
     * @param result                Результаты предыдущих шагов мастера
     *                              Если список результатов пуст, то это запрос первого шага
     * @param previousStepResult    Предыдущий результат последнего шага при возврате к нему
     *
     * Механика возврата к шагу на примере
     *  1. Были пройдены шаги A, B, C. Текущий шаг - D. Результат мастера содержит A-B-C
     *  2. D запрашивает у мастера возврат к предыдущему шагу посредством проброса [StepResult.Back]
     *  3. D удаляется из мастера
     *  4. Предыдущий результат C вытаскивается из [result]. Результат мастера теперь содержит A-B
     *  5. Запрашивается шаг с передачей предыдущего результата C через [previousStepResult]
     *
     * @return Шаг или null, если пройден последний шаг, и мастер должен завершить работу
     */
    fun step(result: StepsResult, previousStepResult: PreviousStepResult?): Step? =
        if (result.stepResults.lastOrNull() is FinalStepResultData) {
            null
        } else {
            illegalState { "Step not found. Results - ${result.stepResults}" }
            null
        }
}