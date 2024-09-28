package ru.tensor.sbis.communication_decl.communicator.media.data

/**
 * Декларативная модель информации о файле для медиа проигрывателя.
 * Является узкой совокупностью полей FileInfoViewModel контроллера вложений.
 *
 * @property attachId - см. FileInfoViewModel.attachId
 * @property localPath - см. FileInfoViewModel.localPath
 * @property previewParams - см. FileInfoViewModel.previewParams
 *
 * @author vv.chekura
 */
data class MediaPlayerFileInfo(
    val attachId: Long,
    val localPath: String?,
    val previewParams: String?
)