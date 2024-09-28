package ru.tensor.sbis.wizard.decl.result

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Результат работы мастера
 *
 * @author sa.nikitin
 */
sealed class WizardResult : Parcelable {
    /**
     * Завершение работы при окончании всех шагов
     */
    @Parcelize
    class Complete(val result: Parcelable?) : WizardResult()

    /**
     * Завершение работы при запросе возврата назад от первого шага
     */
    @Parcelize
    class Back : WizardResult()

    /**
     * Завершение работы при запросе от шага отмены работы мастера
     */
    @Parcelize
    class Cancel : WizardResult()

    /**
     * Завершение работы при непредвиденной ошибке
     */
    @Parcelize
    class Error : WizardResult()
}