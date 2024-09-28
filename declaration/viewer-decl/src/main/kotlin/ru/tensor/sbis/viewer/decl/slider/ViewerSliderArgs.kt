package ru.tensor.sbis.viewer.decl.slider

import android.os.Parcelable
import androidx.annotation.DimenRes
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.viewer.decl.slider.source.ViewerArgsSource
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs

/**
 * Аргументы слайдера просмотрщиков
 *
 * @author sa.nikitin
 */
@Parcelize
data class ViewerSliderArgs @JvmOverloads constructor(
    val source: ViewerArgsSource,
    val thumbnailListDisplayArgs: ThumbnailListDisplayArgs = ThumbnailListDisplayArgs(),
    val swipeBackEnabled: Boolean = true,
    val trackingIsNeeded: Boolean = true,
    val checkAuthorization: Boolean = true
) : Parcelable {

    @JvmOverloads
    constructor(
        viewerArgsList: ArrayList<ViewerArgs>,
        selectedPagePosition: Int = 0,
        thumbnailListDisplayArgs: ThumbnailListDisplayArgs = ThumbnailListDisplayArgs(),
        swipeBackEnabled: Boolean = true,
        trackingIsNeeded: Boolean = true,
        checkAuthorization: Boolean = true
    ) : this(
        ViewerArgsSource.Fixed(viewerArgsList, selectedPagePosition),
        thumbnailListDisplayArgs,
        swipeBackEnabled,
        trackingIsNeeded,
        checkAuthorization
    )

    @JvmOverloads
    constructor(
        viewerArgs: ViewerArgs,
        swipeBackEnabled: Boolean = true,
        trackingIsNeeded: Boolean = true
    ) : this(
        arrayListOf(viewerArgs),
        swipeBackEnabled = swipeBackEnabled,
        trackingIsNeeded = trackingIsNeeded
    )
}

/**
 * Аргументы отображения списка миниатюр в слайдере просмотрщиков
 *
 * @author sa.nikitin
 */
@Parcelize
data class ThumbnailListDisplayArgs(
    val visible: Boolean = false,
    @DimenRes val itemHorizontalOffsetResId: Int? = null,
    val itemDisplayArgs: ThumbnailDisplayArgs? = null,
    val centralItemDisplayArgs: ThumbnailDisplayArgs? = itemDisplayArgs
) : Parcelable

/**
 * Аргументы отображения миниатюры в слайдере просмотрщиков
 *
 * @author sa.nikitin
 */
@Parcelize
data class ThumbnailDisplayArgs(
    @DimenRes val sizeResId: Int,
    @DimenRes val cornersRadiusResId: Int = -1
) : Parcelable