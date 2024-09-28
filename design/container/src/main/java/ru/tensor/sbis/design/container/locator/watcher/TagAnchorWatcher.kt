package ru.tensor.sbis.design.container.locator.watcher

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import kotlinx.parcelize.Parcelize

/**
 * Наблюдатель для обычных вью (Не в составе списка)
 * @author ma.kolpakov
 */
@Parcelize
internal class TagAnchorWatcher(
    private var anchorTag: String,
    private val parentTag: String? = null
) : AnchorWatcher() {

    override fun setAnchorView(view: View) = Unit

    override fun getAnchor(root: View): Single<Rect> {
        return Single.create {
            root.post {
                var parentView = root
                parentTag?.let {
                    parentView = root.findViewWithTag(parentTag)
                }
                val anchorView = parentView.findViewWithTag<View>(anchorTag)
                // отключаем обработку жестов у родителя
                if (anchorView != null) {
                    anchorView.parent.requestDisallowInterceptTouchEvent(true)
                    it.onSuccess(getAnchorRect(anchorView, root))
                    dimUpdater?.onAnchorUpdate(anchorView.rootView as ViewGroup, anchorView)
                } else {
                    dimUpdater?.onAnchorUpdate(root as ViewGroup, root)
                }
            }
        }
    }

}