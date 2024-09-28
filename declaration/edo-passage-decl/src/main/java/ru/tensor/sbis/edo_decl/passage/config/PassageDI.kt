package ru.tensor.sbis.edo_decl.passage.config

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.mobile.docflow.generated.IPassage

/**
 * Внедрение зависимостей для компонента переходов
 * Реализация не должна захватывать что-либо несериализуемое
 *
 * @author sa.nikitin
 */
@Deprecated("Используйте ControllerFactory")
interface PassageDI : Parcelable {

    @Deprecated("Используйте ControllerFactory напрямую, без PassageDI")
    @Parcelize
    class IPassageFactory(private val passageDI: PassageDI) : ControllerFactory<IPassage> {

        override fun createController(): IPassage = passageDI.iPassage().value
    }

    /**
     * Предоставить прикладной экземпляр [IPassage]
     * Непосредственный источник этого экземпляра - прикладной контроллер
     *
     * @see IPassage.instance
     */
    fun iPassage(): Lazy<IPassage>
}

