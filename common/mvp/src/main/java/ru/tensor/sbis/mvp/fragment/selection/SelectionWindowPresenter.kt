package ru.tensor.sbis.mvp.fragment.selection

/**
 * Created by aa.mironychev on 10.05.2018.
 */
/**
 * Базовая реализация презентера для окна выбора.
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class SelectionWindowPresenter<V : SelectionWindowContract.View> : SelectionWindowContract.Presenter<V> {

    @JvmField
    @Deprecated("Появление контента с анимацией реализовано в ContainerBottomSheet")
    var showAppearAnimationWhenAttached = true

    @Suppress("MemberVisibilityCanBePrivate")
    var isAppearAnimationShown = false

    /**
     * Вью реализующая SelectionWindowContract.View
     */
    protected var view: V? = null

    override fun attachView(view: V) {
        this.view = view
        if (showAppearAnimationWhenAttached) {
            showAppearAnimationIfNotShownBefore()
        }
    }

    @Deprecated("Появление контента с анимацией реализовано в ContainerBottomSheet")
    protected fun showAppearAnimationIfNotShownBefore() {
        if (!isAppearAnimationShown) {
            view?.showAppearAnimation()
            isAppearAnimationShown = true
        }
    }

    override fun detachView() {
        view = null
    }

    override fun onCloseClick() {
        view?.closeWindow()
    }

    override fun onBackPressed(): Boolean {
        view?.closeWindow()
        return true
    }

}