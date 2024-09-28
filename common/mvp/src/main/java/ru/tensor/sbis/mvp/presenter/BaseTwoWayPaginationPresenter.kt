package ru.tensor.sbis.mvp.presenter

import androidx.lifecycle.LifecycleObserver

/**
 * Legacy-код
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface BaseTwoWayPaginationPresenter<V> : BasePresenter<V>, LifecycleObserver {

    /** @SelfDocumented */
    fun onScroll(dy: Int, firstVisibleItemPosition: Int, lastVisibleItemPosition: Int, computeVerticalScrollOffset: Int)

    /** @SelfDocumented */
    fun onRefresh()

    /** @SelfDocumented */
    fun forceReloadDataList()

    /** @SelfDocumented */
    fun viewIsStarted()

    /** @SelfDocumented */
    fun viewIsStopped()

    /** @SelfDocumented */
    fun viewIsResumed()

    /** @SelfDocumented */
    fun viewIsPaused()

}
