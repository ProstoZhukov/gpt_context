package ru.tensor.sbis.mvp.adapter.sectioned.lifecycle

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSection
import ru.tensor.sbis.base_components.adapter.sectioned.lifecycle.LifecycleSimulator
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationPresenter

/**
 * Класс для привязки жизненого цикла вью с методами презентера.
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
open class PresenterLifecycleBinder<V>(
    lifecycle: Lifecycle,
    private val presenter: BaseTwoWayPaginationPresenter<V>,
    private val view: V
) {

    @Suppress("unused")
    private val lifecycleSimulator = object : LifecycleSimulator(lifecycle) {
        override fun onStart() {
            this@PresenterLifecycleBinder.onAttachView()
            this@PresenterLifecycleBinder.onStart()
        }

        override fun onResume() {
            this@PresenterLifecycleBinder.onResume()
        }

        override fun onPause() {
            this@PresenterLifecycleBinder.onPause()
        }

        override fun onStop() {
            this@PresenterLifecycleBinder.onStop()
            this@PresenterLifecycleBinder.onDetachView()
        }
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onAttachView() {
        presenter.attachView(view)
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onStart() {
        presenter.viewIsStarted()
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onResume() {
        presenter.viewIsResumed()
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onPause() {
        presenter.viewIsPaused()
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onStop() {
        presenter.viewIsStopped()
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onDetachView() {
        presenter.detachView()
    }
}

@Deprecated("Устаревший подход, переходим на mvi_extension")
open class PresenterFragmentBinder<V>(
    fragment: Fragment,
    private val presenter: BaseTwoWayPaginationPresenter<V>,
    private val view: V
) {

    init {
        fragment.viewLifecycleOwnerLiveData.observeForever {
            it?.lifecycle?.let { lifecycle ->
                object : LifecycleSimulator(lifecycle) {
                    override fun onCreate() {
                        this@PresenterFragmentBinder.onAttachView()
                    }

                    override fun onStart() {
                        this@PresenterFragmentBinder.onStart()
                    }

                    override fun onResume() {
                        this@PresenterFragmentBinder.onResume()
                    }

                    override fun onPause() {
                        this@PresenterFragmentBinder.onPause()
                    }

                    override fun onStop() {
                        this@PresenterFragmentBinder.onStop()
                    }

                    override fun onDestroy() {
                        super.onDestroy()
                        this@PresenterFragmentBinder.onDetachView()
                    }
                }
            }
        }
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onAttachView() {
        presenter.attachView(view)
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onStart() {
        presenter.viewIsStarted()
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onResume() {
        presenter.viewIsResumed()
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onPause() {
        presenter.viewIsPaused()
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onStop() {
        presenter.viewIsStopped()
    }

    /**
     * @SelfDocumented
     */
    @CallSuper
    open fun onDetachView() {
        presenter.detachView()
    }
}

@Deprecated("Устаревший подход, переходим на mvi_extension")
object PresenterLifecycleSectionBinder {

    /**
     * Присоединяет экземпляр [PresenterLifecycleBinder] к секции [section].
     */
    @JvmStatic
    fun <V> bind(
        section: ListSection<*, *, *>,
        lifecycle: Lifecycle,
        presenter: BaseTwoWayPaginationPresenter<V>,
        view: V
    ) {
        object : PresenterLifecycleBinder<V>(lifecycle, presenter, view) {
            override fun onAttachView() {
                if (section.isAttachedToView) { //в противном случае произошел detach секции и мы не должны реагировать на методы жизненного цикла
                    super.onAttachView()
                }
            }

            override fun onDetachView() {
                if (section.isAttachedToView) {
                    super.onDetachView()
                }
            }
        }
    }

    /**
     * Присоединяет экземпляр [PresenterFragmentBinder] к секции [section].
     */
    @JvmStatic
    fun <V> bind(
        section: ListSection<*, *, *>,
        fragment: Fragment,
        presenter: BaseTwoWayPaginationPresenter<V>,
        view: V
    ) {
        object : PresenterFragmentBinder<V>(fragment, presenter, view) {
            override fun onAttachView() {
                if (section.isAttachedToView) { //в противном случае произошел detach секции и мы не должны реагировать на методы жизненного цикла
                    super.onAttachView()
                }
            }

            override fun onDetachView() {
                if (section.isAttachedToView) {
                    super.onDetachView()
                }
            }
        }
    }
}