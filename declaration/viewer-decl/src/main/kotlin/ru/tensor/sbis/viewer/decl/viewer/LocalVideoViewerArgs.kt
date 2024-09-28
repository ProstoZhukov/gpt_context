package ru.tensor.sbis.viewer.decl.viewer

import kotlinx.parcelize.Parcelize

/**
 * Параметры просмотрщика локального видео
 *
 * @author sa.nikitin
 */
@Parcelize
open class LocalVideoViewerArgs(
    open val localUri: String,
    override var title: String?,
    override val id: String = localUri
) : ViewerArgs