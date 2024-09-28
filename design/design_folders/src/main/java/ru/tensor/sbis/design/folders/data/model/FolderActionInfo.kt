package ru.tensor.sbis.design.folders.data.model

import ru.tensor.sbis.design.folders.support.utils.actionhandlers.FoldersActionConsumer

/**
 * Модель, хранящая данные необходимые для выполнения событий [FoldersActionConsumer]
 *
 * @author da.zolotarev
 */
data class FolderActionInfo(
    val actionType: FolderActionType,
    val folderId: String,
    val folderName: String?
)
