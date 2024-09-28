package ru.tensor.sbis.design.gallery.impl.viewer

import ru.tensor.sbis.viewer.decl.viewer.ImageSource
import ru.tensor.sbis.viewer.decl.viewer.ImageViewerArgs
import ru.tensor.sbis.viewer.decl.viewer.LocalVideoViewerArgs

/**
 * @author ia.nikitin
 */

/** @SelfDocumented */
class GalleryImageViewerArgs(
    override val imageSource: ImageSource,
    override val id: String = imageSource.id,
    override var title: String? = null,
    val fileSize: Int?
) : ImageViewerArgs(imageSource, id, title)

/** @SelfDocumented */
class GalleryVideoViewerArgs(
    override val localUri: String,
    override var title: String?,
    override val id: String = localUri,
    val fileSize: Int?
) : LocalVideoViewerArgs(localUri, title, id)
