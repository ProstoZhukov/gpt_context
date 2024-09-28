package ru.tensor.sbis.communicator.base_folders.list_section

/**
 * @author da.zhukov
 *
 * Базовые опции создания папок.
 */
data class CommunicatorBaseFolderOptions(
    val isExpandable: Boolean = false,
    val isShownCurrentFolder: Boolean = false
)
