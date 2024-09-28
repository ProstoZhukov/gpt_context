package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations

import ru.tensor.sbis.design.folders.FoldersView

/**
 * Интерфейс вспомогательного класса для привязки/отвязки вью-модели папок к холдеру
 *
 * @author rv.krohalev
 */
internal interface FoldersViewHolderHelper {

    /** @SelfDocumented */
    fun attachFoldersView(view: FoldersView)

    /** @SelfDocumented */
    fun detachFoldersView(view: FoldersView)
}