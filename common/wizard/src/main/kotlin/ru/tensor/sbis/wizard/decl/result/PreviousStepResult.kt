package ru.tensor.sbis.wizard.decl.result

import android.os.Parcelable

/**
 * Предыдущий результат шага
 *
 * @property value                  Значение результата
 * @property backInitiatorStepData  Данные с шага, инициирующего возврат, см. [StepResult.Back.backInitiatorStepData]
 *                                  Т.е. шаг, с которого был произведен возврат,
 *                                  может выбросить полезные данные для шага, на который происходит возврат
 */
class PreviousStepResult(val value: Parcelable, val backInitiatorStepData: Parcelable?)