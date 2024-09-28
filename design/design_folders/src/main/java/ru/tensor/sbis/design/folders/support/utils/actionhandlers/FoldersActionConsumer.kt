package ru.tensor.sbis.design.folders.support.utils.actionhandlers

import io.reactivex.functions.Consumer
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.model.FolderActionInfo
import ru.tensor.sbis.design.folders.data.model.FolderActionType
import ru.tensor.sbis.design.folders.support.FoldersViewModel
import ru.tensor.sbis.design.folders.support.utils.actionhandlers.FoldersActionConsumer.Companion.TERMINAL_EVENTS

/**
 * Подписка на действия над папками, которая закрывает шторку всех папок на события из
 * [TERMINAL_EVENTS]
 *
 * @author ma.kolpakov
 */
internal class FoldersActionConsumer(
    private val vm: FoldersViewModel,
    private val handler: FolderActionHandler
) : Consumer<FolderActionInfo> {

    override fun accept(actionInfo: FolderActionInfo) {
        val (action, folderId, folderName) = actionInfo
        handler.handleAction(action, folderId, folderName)
        if (action in TERMINAL_EVENTS) {
            vm.onHideFoldersPanel()
        }
    }

    private companion object {
        private val TERMINAL_EVENTS = arrayOf(FolderActionType.CLICK)
    }
}