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
 * Горизонтальный локатор
 * @author ma.kolpakov
 */
sealed class HorizontalLocator(internal val locator: Locator) : Locator by locator, Parcelable

/**
 * @see [ScreenLocator]
 */
@Parcelize
class ScreenHorizontalLocator(
    private val alignment: HorizontalAlignment = HorizontalAlignment.CENTER,
    @IdRes private val boundsViewId: Int = View.NO_ID
) : HorizontalLocator(ScreenLocator(alignment.toLocatorAlignment(), boundsViewId).apply { isVertical = false })

/**
 * @see [AnchorLocator]
 */
@Parcelize
class AnchorHorizontalLocator internal constructor(
    internal val alignment: HorizontalAlignment,
    @IdRes internal val boundsViewId: Int = View.NO_ID,
    internal val force: Boolean = true,
    internal val innerPosition: Boolean = false,
    @DimenRes internal val offsetRes: Int = ResourcesCompat.ID_NULL,
    internal var anchorWatcher: AnchorWatcher? = null

) : HorizontalLocator(
    AnchorLocator(
        alignment.toLocatorAlignment(),
        boundsViewId,
        force,
        innerPosition,
        offsetRes,
        anchorWatcher
    ).apply { isVertical = false }
) {

    constructor(
        alignment: HorizontalAlignment,
        @IdRes boundsViewId: Int = View.NO_ID,
        force: Boolean = true,
        innerPosition: Boolean = false,
        @DimenRes offsetRes: Int = ResourcesCompat.ID_NULL
    ) : this(alignment, boundsViewId, force, innerPosition, offsetRes, null)

    @IgnoredOnParcel
    lateinit var anchorView: View

    // TODO: 18.05.2021 ввести иерархию интерфейсов что бы избавиться сеттера https://online.sbis.ru/opendoc.html?guid=02f1e075-6e54-4598-ba11-7b6730f9debb
    internal fun setWatcher(anchorWatcher: AnchorWatcher?, needSaveOverlayView: Boolean = false) {
        this.anchorWatcher?.dispose(needSaveOverlayView)
        this.anchorWatcher = anchorWatcher
        (locator as AnchorLocator).anchorWatcher = anchorWatcher
    }
}

/**
 * Локатор определяющий якорь по тегам
 * @param anchorLocator @see [AnchorLocator]
 * @param anchorTag тег, которым помечена вью.
 * @param parentTag тег родительского представления, опционально
 */
@Parcelize
class TagAnchorHorizontalLocator(
    val anchorLocator: AnchorHorizontalLocator,
    val anchorTag: String,
    val parentTag: String? = null,
) : HorizontalLocator(
    AnchorLocator(
        anchorLocator.alignment.toLocatorAlignment(),
        anchorLocator.boundsViewId,
        anchorLocator.force,
        anchorLocator.innerPosition,
        anchorLocator.offsetRes,
        anchorLocator.anchorWatcher
    )
) {
    internal fun setWatcher(anchorWatcher: AnchorWatcher?) {
        anchorLocator.anchorWatcher?.dispose()
        anchorLocator.anchorWatcher = anchorWatcher
        (locator as AnchorLocator).anchorWatcher = anchorWatcher
    }
}

/** @SelfDocument */
enum class HorizontalAlignment {
    LEFT,
    CENTER,
    RIGHT;

    internal fun toLocatorAlignment() = when (this) {
        LEFT -> LocatorAlignment.START
        CENTER -> LocatorAlignment.CENTER
        RIGHT -> LocatorAlignment.END
    }
}