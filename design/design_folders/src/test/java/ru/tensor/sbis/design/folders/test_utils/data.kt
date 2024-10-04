/**
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.folders.test_utils

import ru.tensor.sbis.design.folders.data.model.*

internal fun folder(
    id: String = "0",
    title: String = "",
    type: FolderType = FolderType.DEFAULT,
    depthLevel: Int = 0,
    totalContentCount: Int = 0,
    unreadContentCount: Int = 0,
) = Folder(
    id = id,
    title = title,
    type = type,
    depthLevel = depthLevel,
    totalContentCount = totalContentCount,
    unreadContentCount = unreadContentCount,
)

internal fun command(
    title: String = "",
    type: AdditionalCommandType = AdditionalCommandType.DEFAULT,
) = AdditionalCommand(
    title = title,
    type = type,
)
