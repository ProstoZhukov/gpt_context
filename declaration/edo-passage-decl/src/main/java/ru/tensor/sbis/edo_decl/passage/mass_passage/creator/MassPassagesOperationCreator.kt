package ru.tensor.sbis.edo_decl.passage.mass_passage.creator

import android.os.Parcelable
import ru.tensor.sbis.mobile.docflow.generated.MassPassagesOperation

/**
 * Интерфейс фабрики [MassPassagesOperation].
 * Прикладная реализация должна забирать экземпляр [MassPassagesOperation] у прикладного контроллера.
 *
 * @author sa.nikitin
 */
interface MassPassagesOperationCreator : Parcelable {

    /**
     * Метод должен вернуть готовый [MassPassagesOperation]
     */
    suspend fun create(): MassPassagesOperation
}