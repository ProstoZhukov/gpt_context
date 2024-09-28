package ru.tensor.sbis.design.container.locator.watcher

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.View.NO_ID
import android.view.ViewGroup
import androidx.annotation.IdRes
import io.reactivex.Single
import kotlinx.parcelize.Parcelize
import java.lang.IllegalStateException

/**
 * Наблюдатель для обычных вью (Не в составе списка)
 * @author ma.kolpakov
 */
@Parcelize
internal class BaseAnchorWatcher(@IdRes private var anchorId: Int = NO_ID) : AnchorWatcher() {

    override fun setAnchorView(view: View) {
        anchorId = view.id
    }

    override fun getAnchor(root: View): Single<Rect> {
        // TODO: 27.04.2021 Разобраться с последовательностью вызовов, постараться  избавиться от post{} https://online.sbis.ru/opendoc.html?guid=6779e02d-86cf-459b-b716-90ac96b76982
        return Single.create {
            root.post {
                val anchorView = root.findViewById<View>(anchorId)
                // отключаем обработку жестов у родителя
                if (anchorView != null) {
                    anchorView.parent.requestDisallowInterceptTouchEvent(true)
                    it.onSuccess(getAnchorRect(anchorView, root))
                    dimUpdater?.onAnchorUpdate(anchorView.rootView as ViewGroup, anchorView)
                } else {
                    dimUpdater?.onAnchorUpdate(root as ViewGroup, root)
                    val idName = try {
                        root.context.resources.getResourceName(anchorId)
                    } catch (e: Resources.NotFoundException) {
                        "Resource with id [$anchorId] not found"
                    }
                    it.onError(IllegalStateException("Anchor with view id[$idName] not found"))
                }

            }
        }
    }

}