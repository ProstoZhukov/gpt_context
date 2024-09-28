package ru.tensor.sbis.wizard.decl.result

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Результат работы шага
 *
 * @author sa.nikitin
 */
sealed class StepResult : Parcelable {

    /**
     * Шаг завершён с некоторыми данными
     * Если шаг последний, то в качестве [data] можно использовать [FinalStepResultData]
     */
    @Parcelize
    class Success(val data: Parcelable) : StepResult()

    /**
     * Шаг завершён с запросом возврата к предыдущему шагу
     * Следует использовать при событиях перехода "назад" либо по кнопке в интерфейсе, либо по системной кнопке "назад"
     *
     * @property backInitiatorStepData  Данные с шага, инициирующего возврат
     *                                  Т.е. шаг, с которого был произведен возврат,
     *                                  может выбросить полезные данные для шага, на который происходит возврат
     *                                  См. [PreviousStepResult.backInitiatorStepData]
     */
    @Parcelize
    class Back(val backInitiatorStepData: Parcelable? = null) : StepResult()

    /**
     * Шаг завершён с запросом отмены работы мастера
     * Следует использовать при событиях, по которым нужно прервать работу мастера, либо при непредвиденных ошибках
     * Не следует использовать при завершении последнего шага, для этого используйте [Success]
     */
    @Parcelize
    class Cancel : StepResult()
}