package ru.tensor.sbis.wizard.decl.step

import android.os.Parcelable
import io.reactivex.Single
import ru.tensor.sbis.wizard.decl.result.StepResult

/**
 * Шаг мастера
 *
 * @author sa.nikitin
 */
interface Step : StepFragmentFactory, Parcelable {

    /**
     * [Single], испускающий результат работы шага
     */
    val result: Single<StepResult>
}