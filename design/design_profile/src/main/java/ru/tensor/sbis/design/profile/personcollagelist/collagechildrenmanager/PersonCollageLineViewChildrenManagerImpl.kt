package ru.tensor.sbis.design.profile.personcollagelist.collagechildrenmanager

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.design.profile.imageview.PersonImageView
import ru.tensor.sbis.design.profile.personcollagelist.PersonCollageLineView
import ru.tensor.sbis.design.profile.personcollagelist.util.PersonCollageLineViewPool
import ru.tensor.sbis.design.profile.util.BitmapResult
import ru.tensor.sbis.design.profile.util.loadBitmaps
import ru.tensor.sbis.design.profile.util.setBitmapFromCache
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.Shape

/**
 * Инструмент для управления дочерними элементами [PersonCollageLineView] (для обеспечения требуемого числа элементов,
 * их конфигурации и привязки данным к ним, а также для управления счётчиком).
 *
 * @author us.bessonov
 */
internal class PersonCollageLineViewChildrenManagerImpl(
    private val getBitmapsObservable: (Context, List<PhotoData>, Int) -> Observable<BitmapResult> = ::loadBitmaps,
    private val setBitmapFromCache: (PhotoData, Int, PersonImageView) -> Boolean = ::setBitmapFromCache
) : PersonCollageLineViewChildrenManager {

    private val disposable = SerialDisposable()

    override val children = mutableListOf<PersonImageView>()

    private lateinit var collageView: View

    private var viewPool: PersonCollageLineViewPool? = null

    @ColorInt
    private var initialsColor: Int = 0

    @Px
    private var itemSize = 0

    @Px
    private var initialsTextSize: Float? = null

    @Px
    private var itemOverlapPart = 0

    private var areSomeItemsHidden = false

    private var displayedItems: List<PhotoData>? = null

    /** @SelfDocumented */
    fun updateItemSize(@Px itemSize: Int, @Px itemOverlapPart: Int, @Px initialsTextSize: Float?) {
        this.itemSize = itemSize
        this.itemOverlapPart = itemOverlapPart
        this.initialsTextSize = initialsTextSize
    }

    /**
     * Настраивает View для отображения заданного числа элементов, а также обновляет видимость и значение счётчика.
     */
    fun updateChildren(visibleCount: Int, defaultHiddenCount: Int, displayedData: List<PhotoData>) {
        areSomeItemsHidden = visibleCount < displayedData.size || defaultHiddenCount > 0
        dropUnnecessaryViews(visibleCount)
        configureItems(displayedData, visibleCount)
        val hiddenCount = defaultHiddenCount + displayedData.size - visibleCount
        // Если есть скрытые элементы, то на последнем видимом фото отобразится счётчик, и оно войдёт в число скрытых
        val counterValue = if (hiddenCount > 0) hiddenCount + 1 else 0
        updateCounter(counterValue)
    }

    override fun setCollageView(view: View) {
        collageView = view
    }

    override fun setViewPool(pool: PersonCollageLineViewPool) {
        viewPool = pool
    }

    override fun setShape(shape: Shape) = getViewPool().setShape(shape)

    override fun setInitialsColor(@ColorInt color: Int) {
        initialsColor = color
    }

    override fun onDetachedFromWindow() {
        disposable.set(null)
    }

    override fun reloadImagesIfRecycled() {
        if (children.any { it.isBitmapRecycled() }) {
            loadImages()
        }
    }

    override fun isReLayoutRequired(newSize: Int, maxVisibleCount: Int, totalCount: Int): Boolean {
        if (areSomeItemsHidden) {
            if (newSize.coerceAtMost(maxVisibleCount) < children.size) return true
            val newHiddenCount = newSize - children.size + (totalCount - newSize).coerceAtLeast(0)
            return newHiddenCount < 2
        }

        return false
    }

    private fun dropUnnecessaryViews(visibleCount: Int) {
        repeat(children.size - visibleCount) {
            val removedView = children.last()
            children.removeLast()
            getViewPool().recycle(removedView)
        }
    }

    private fun configureItems(dataList: List<PhotoData>, visibleCount: Int) {
        (0 until dataList.size.coerceAtMost(visibleCount)).forEach { i ->
            val data = dataList[i]
            getOrPutChildViewAt(i, data.photoUrl).apply {
                configureItem(this, data)
            }
        }
        loadImages(dataList.take(visibleCount))
    }

    private fun loadImages(visibleItems: List<PhotoData>) {
        if (visibleItems == displayedItems) return
        displayedItems = visibleItems
        loadImages()
    }

    private fun loadImages() {
        val visibleItems = displayedItems ?: return
        val notCachedItems = mutableListOf<PhotoData>()
        val viewsForNotCachedItems = mutableListOf<PersonImageView>()
        visibleItems.forEachIndexed { i, it ->
            if (!setBitmapFromCache(it, itemSize, children[i])) {
                notCachedItems.add(it)
                viewsForNotCachedItems.add(children[i])
            }
        }
        if (notCachedItems.size != visibleItems.size) {
            collageView.invalidate()
        }
        loadImages(notCachedItems, viewsForNotCachedItems)
    }

    private fun loadImages(loadingItems: List<PhotoData>, views: List<PersonImageView>) {
        disposable.set(
            getBitmapsObservable(collageView.context, loadingItems, itemSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (views[it.index].setBitmap(it.bitmap)) {
                        collageView.invalidate()
                    }
                }
        )
    }

    private fun configureItem(view: PersonImageView, data: PhotoData) {
        view.setPhotoSize(itemSize)
        view.initialsTextSize = initialsTextSize
        view.initialsColor = initialsColor
        view.setData(data)
    }

    private fun updateCounter(hiddenCount: Int) {
        children.forEachIndexed { i, it ->
            if (i < children.lastIndex) {
                it.resetCounter()
            } else {
                it.resetCounter(hiddenCount)
            }
        }
    }

    private fun getViewPool() = viewPool ?: PersonCollageLineViewPool(collageView.context)
        .also { viewPool = it }

    private fun getOrPutChildViewAt(index: Int, key: String?): PersonImageView {
        return children.getOrNull(index)
            ?: getViewPool().get(key)
                .also(children::add)
    }
}