package ru.tensor.sbis.wizard.decl.result

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Результирующие данные последнего шага
 * Интерфейс является маркерным, т.е. лишь сигналом, что завершившийся шаг последний. Не обязателен к использованию
 *
 * @author sa.nikitin
 */
interface FinalStepResultData : Parcelable

/**
 * @SelfDocumented
 *
 * @author sa.nikitin
 */
@Parcelize
class FinalStepResultDataImpl : FinalStepResultData