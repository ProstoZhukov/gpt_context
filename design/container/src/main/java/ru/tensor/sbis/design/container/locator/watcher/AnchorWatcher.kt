package ru.tensor.sbis.design.container.locator.watcher

import android.graphics.Rect
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import kotlinx.parcelize.IgnoredOnParcel
import ru.tensor.sbis.design.container.locator.getRectDescendantParent

/**
 * Наблюдатель за якорем - позволяет получить вью после восстановления при повороте
 * @author ma.kolpakov
 */
internal abstract class AnchorWatcher : Parcelable {

    @IgnoredOnParcel
    internal var dimUpdater: DimUpdater? = null

    /**
     * Устанавливает новый якорь, для отслеживания. Якорь обязательно должен иметь id
     *
     * @throws IllegalArgumentException если у якоря не указан id
     */
    abstract fun setAnchorView(view: View)

    /**
     * Получить подписку на обновление прямоугольника якоря
     */
    internal abstract fun getAnchor(root: View): Single<Rect>

    /**@SelfDocumented */
    internal open fun dispose(needSaveOverlayView: Boolean = false) {
        // Требуется иметь такой аргумент из-за наличия переопределённого метода в RecyclerAnchorWatcher,
        // в котором при любом условии необходимо выполнить открепление декоратора от RecyclerView.
        if (needSaveOverlayView) return
        dimUpdater?.close()
        dimUpdater = null
    }

    internal fun getAnchorRect(anchor: View, root: View): Rect {
        val rectDescendantParent = anchor.getRectDescendantParent(root as ViewGroup)
        rectDescendantParent.left += getTranslationX(anchor).toInt()
        rectDescendantParent.top += getTranslationY(anchor).toInt()
        return rectDescendantParent
    }

    private tailrec fun getTranslationX(view: View, prevTranslation: Float = 0f): Float {
        val parent = view.parent
        val translation = prevTranslation + view.translationX
        return if (parent == null || parent !is View) {
            translation
        } else {
            getTranslationX(parent, translation)
        }
    }

    private tailrec fun getTranslationY(view: View, prevTranslation: Float = 0f): Float {
        val parent = view.parent
        val translation = prevTranslation + view.translationY
        return if (parent == null || parent !is View) {
            translation
        } else {
            getTranslationY(parent, translation)
        }
    }
}
