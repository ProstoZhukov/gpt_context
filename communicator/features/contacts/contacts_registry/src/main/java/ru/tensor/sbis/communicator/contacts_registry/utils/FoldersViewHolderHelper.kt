package ru.tensor.sbis.communicator.contacts_registry.utils

import ru.tensor.sbis.design.folders.FoldersView

/**
 * Интерфейс вспомогательного класса для привязки/отвязки вью-модели папок к холдеру
 *
 * @author vv.chekurda
 */
internal interface FoldersViewHolderHelper {

    /** @SelfDocumented */
    fun attachFoldersView(view: FoldersView)

    /** @SelfDocumented */
    fun detachFoldersView(view: FoldersView)
}