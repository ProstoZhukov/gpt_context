package ru.tensor.sbis.design.container.locator

import android.os.Parcelable
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.container.locator.watcher.AnchorWatcher

/**
 * @author ma.kolpakov
 */
sealed class VerticalLocator(internal val locator: Locator) : Locator by locator, Parcelable

/**
 * @see [ScreenLocator]
 */
@Parcelize
class ScreenVerticalLocator(
    private val alignment: VerticalAlignment = VerticalAlignment.CENTER,
    @IdRes private val boundsViewId: Int = View.NO_ID
) : VerticalLocator(
    ScreenLocator(alignment.toLocatorAlignment(), boundsViewId).apply {
        isVertical = true
    }
)

/**
 * @see [AnchorLocator]
 */
@Parcelize
class AnchorVerticalLocator internal constructor(
    internal val alignment: VerticalAlignment,
    @IdRes internal val boundsViewId: Int = View.NO_ID,
    internal val force: Boolean = true,
    internal val innerPosition: Boolean = false,
    @DimenRes internal val offsetRes: Int = ResourcesCompat.ID_NULL,
    internal var anchorWatcher: AnchorWatcher? = null
) : VerticalLocator(
    AnchorLocator(
        alignment.toLocatorAlignment(),
        boundsViewId,
        force,
        innerPosition,
        offsetRes,
        anchorWatcher
    ).apply { isVertical = true }
) {

    constructor(
        alignment: VerticalAlignment,
        @IdRes boundsViewId: Int = View.NO_ID,
        force: Boolean = true,
        innerPosition: Boolean = false,
        @DimenRes offsetRes: Int = ResourcesCompat.ID_NULL
    ) : this(alignment, boundsViewId, force, innerPosition, offsetRes, null)

    @IgnoredOnParcel
    lateinit var anchorView: View

    internal fun setWatcher(anchorWatcher: AnchorWatcher?, needSaveOverlayView: Boolean = false) {
        this.anchorWatcher?.dispose(needSaveOverlayView)
        this.anchorWatcher = anchorWatcher
        (locator as AnchorLocator).anchorWatcher = anchorWatcher
    }
}

@Parcelize
class TagAnchorVerticalLocator(
    val anchorLocator: AnchorVerticalLocator,
    val anchorTag: String,
    val parentTag: String? = null,
) : VerticalLocator(
    AnchorLocator(
        anchorLocator.alignment.toLocatorAlignment(),
        anchorLocator.boundsViewId,
        anchorLocator.force,
        anchorLocator.innerPosition,
        anchorLocator.offsetRes,
        anchorLocator.anchorWatcher
    ).apply { isVertical = true }
) {
    internal fun setWatcher(anchorWatcher: AnchorWatcher?) {
        anchorLocator.anchorWatcher?.dispose()
        anchorLocator.anchorWatcher = anchorWatcher
        (locator as AnchorLocator).anchorWatcher = anchorWatcher
    }
}

/** @SelfDocument */
enum class VerticalAlignment {
    TOP,
    CENTER,
    BOTTOM;

    internal fun toLocatorAlignment() = when (this) {
        TOP -> LocatorAlignment.START
        CENTER -> LocatorAlignment.CENTER
        BOTTOM -> LocatorAlignment.END
    }
}