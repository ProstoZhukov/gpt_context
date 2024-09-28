package ru.tensor.sbis.edo_decl.passage.mass_passage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.edo_decl.passage.config.ControllerFactory
import ru.tensor.sbis.edo_decl.passage.config.PassageDI
import ru.tensor.sbis.edo_decl.passage.mass_passage.creator.MassPassagesOperationCreator
import ru.tensor.sbis.mobile.docflow.generated.IPassage

/**
 * Конфигурация компонента массовых переходов
 *
 * @property iPassageFactory            Фабрика [IPassage]
 *
 * @property executionType              Тип выполнения массовых переходов
 *
 * @property resultPopupConfigProvider  Поставщик конфига popup-сообщения о результате выполнения массовых переходов.
 *
 * @author sa.nikitin
 */
@Parcelize
data class MassPassagesConfig(
    val iPassageFactory: ControllerFactory<IPassage>,
    val executionType: MassPassagesExecutionType,
    val resultPopupConfigProvider: MassPassagesResultPopupConfigProvider,
) : Parcelable {

    @Deprecated("Используйте первичный конструктор")
    constructor(
        di: PassageDI,
        massPassagesOperationCreator: MassPassagesOperationCreator,
        docListFragmentFactory: MassPassagesDocListFragmentFactory,
        resultPopupConfigProvider: MassPassagesResultPopupConfigProvider
    ) : this(
        di.let(PassageDI::IPassageFactory),
        MassPassagesExecutionType.ByOperation(massPassagesOperationCreator, docListFragmentFactory),
        resultPopupConfigProvider
    )
}