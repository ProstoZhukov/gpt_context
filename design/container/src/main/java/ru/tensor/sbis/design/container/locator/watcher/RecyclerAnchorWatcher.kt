package ru.tensor.sbis.design.container.locator.watcher

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.View.NO_ID
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Single
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.utils.checkNotNullSafe

/**
 * Класс для отслеживания обновлений якоря внутри списочного элемента
 *
 * @param anchorItemId позиция элемента списка в котором находится якорь
 * @param anchorId view id якоря
 * @param recyclerId view id списка
 *
 * @author ma.kolpakov
 */
@Parcelize
internal class RecyclerAnchorWatcher(
    private var anchorItemId: String? = null,
    @IdRes private var anchorId: Int = NO_ID,
    @IdRes private var recyclerId: Int = NO_ID,
) : AnchorWatcher() {

    @IgnoredOnParcel
    private var recyclerView: RecyclerView? = null

    @IgnoredOnParcel
    private val decorator: RecyclerView.ItemDecoration = object : RecyclerView.ItemDecoration() {
        /**
         * Обновляем якорь после перерисовки
         */
        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            dimUpdater?.onAnchorUpdate(parent, findAnchor(parent))
        }
    }

    /**
     * Устанавливает новый якорь, для отслеживания. Якорь обязательно должен иметь ID, и быть частью элемента внутри списка [RecyclerView]
     *
     * @throws IllegalArgumentException если у якоря не указан id
     * @throws IllegalArgumentException если якоря не является частью элемента списка
     */
    override fun setAnchorView(view: View) {
        check(view.id != NO_ID) { "Anchor view must have an id" }
        anchorId = view.id
        val itemRoot = checkNotNull(getRecyclerItemRoot(view)) { "Anchor view must be in RecyclerView" }
        val recycler: RecyclerView = itemRoot.parent as RecyclerView
        check(recycler.id != NO_ID) { "RecyclerView  must have an id" }
        recyclerId = recycler.id
        anchorItemId = detectId(itemRoot, recycler)
    }

    override fun getAnchor(root: View): Single<Rect> {
        check(anchorId != NO_ID) { "Can't restore container, not enough Anchor id" }
        check(recyclerId != NO_ID) { "Can't restore container, not enough Recycler id" }
        recyclerView =
            checkNotNullSafe(root.findViewById(recyclerId)) { "RecyclerView must be attached to parent view " }
        dimUpdater?.let {
            // TODO: 29.04.2021 Избавиться по возможности от странного кода или задокументировать https://online.sbis.ru/opendoc.html?guid=6779e02d-86cf-459b-b716-90ac96b76982
            recyclerView?.let {
                it.removeItemDecoration(decorator)
                it.addItemDecoration(decorator)
            }
        }
        // TODO: 27.04.2021 Разобраться с последовательностью вызовов, постараться  избавиться от post{} https://online.sbis.ru/opendoc.html?guid=6779e02d-86cf-459b-b716-90ac96b76982
        return Single.create { emitter ->
            recyclerView ?: emitter.onError(IllegalStateException("RecyclerView must be attached to parent view "))
            recyclerView?.apply {
                post {
                    val anchorView = findAnchor(this)
                    // отключаем обработку жестов у родителя
                    anchorView?.parent?.requestDisallowInterceptTouchEvent(true)

                    dimUpdater?.onAnchorUpdate(this, anchorView)
                    if (anchorView != null) {
                        emitter.onSuccess(getAnchorRect(anchorView, root))
                    }
                }
            }
        }
    }

    /**
     * Ищем корневой элемент списка от якоря
     */
    private tailrec fun getRecyclerItemRoot(view: View): View? {
        val parent: View = checkNotNull(view.parent) { "Anchor view is not have RecyclerView parent" } as View
        return if (parent is RecyclerView) {
            view
        } else {
            getRecyclerItemRoot(parent)
        }
    }

    override fun dispose(needSaveOverlayView: Boolean) {
        super.dispose(needSaveOverlayView)
        recyclerView?.removeItemDecoration(decorator)
    }

    /**
     * Ищем якорь внутри списка
     *
     * @return 'null' если якорь прокрутился и уже не присоединен к RecyclerView
     */
    private fun findAnchor(recyclerView: RecyclerView): View? {
        if (!recyclerView.isAttachedToWindow) return null
        if (anchorItemId != null) {
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                val itemId = detectId(child, recyclerView)
                if (itemId == anchorItemId) {
                    return child.findViewById(anchorId)
                }
            }
        }
        return null
    }

    /**
     * Получить уникальный идентификатор записи в списке
     */
    private fun detectId(item: View, recycler: RecyclerView): String {
        val childViewHolder = recycler.getChildViewHolder(item)
        return if (childViewHolder is ItemIdProvider) {
            childViewHolder.getId()
        } else {
            return childViewHolder.adapterPosition.toString()
        }
    }

}
