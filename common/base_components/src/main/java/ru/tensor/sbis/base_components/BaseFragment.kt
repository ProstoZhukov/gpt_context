package ru.tensor.sbis.base_components

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.android_ext_decl.AndroidComponent
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.BuildConfig
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.common.R as CommonR
import ru.tensor.sbis.design.swipeback.SwipeBackFragment
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.events_tracker.EventTrackerPlugin
import ru.tensor.sbis.events_tracker.EventsTracker
import timber.log.Timber

/**
 * Базовый фрагмент
 */
@UiThread
abstract class BaseFragment : SwipeBackFragment(), FragmentBackPress, AndroidComponent {

    companion object {
        /**
         * константа флага добавления отступа под статус-бар на телефоне в портретном режиме
         *
         * при переходе на SingleActivity нужно проработать и зарефакторить решение
         * когда MainActivity отдает пространство всего экрана (в т.ч. под статус-баром)
         * для того чтобы не работать с цветом статус бара,
         * а с цветом бэкграунда фрагмента
         * TODO https://online.sbis.ru/opendoc.html?guid=7e2793d5-ce7c-4914-a7ac-f534626ab463&client=3
         * */
        const val ARG_ADD_PADDING: String = "needAddDefaultTopPadding"
    }

    protected var isRunning = true
        private set

    @VisibleForTesting
    protected lateinit var mEventsTracker: EventsTracker

    protected open val isNeedToBeTracked: Boolean
        get() = true

    /**
     * контейнер фрагмента для добавления отступа под статус-бар
     * используется в случае передачи аргумента ARG_ADD_PADDING
     * TODO https://online.sbis.ru/opendoc.html?guid=7e2793d5-ce7c-4914-a7ac-f534626ab463&client=3
    */
    protected open var rootContainerForTopPadding: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTracking()
    }

    @VisibleForTesting
    protected open fun setupTracking() {
        if (isNeedToBeTracked) {
            mEventsTracker = try {
                EventTrackerPlugin.eventsTracker
            } catch (e: Exception) {
                EventsTracker(requireContext())
            }
            mEventsTracker.trackScreen(requireActivity(), javaClass.simpleName)
            pushScreen()
        }
    }

    override fun onStart() {
        super.onStart()
        isRunning = true
    }

    override fun onStop() {
        isRunning = false
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.getBoolean(ARG_ADD_PADDING, false)) addDefaultTopPadding(view)
        }
    }

    override fun onDestroyView() {
        if (isNeedToBeTracked) {
            popScreen()
        }
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (isNeedToBeTracked) {
            popScreen()
        }
        super.onDestroy()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun getSupportFragmentManager(): FragmentManager {
        return requireFragmentManager()
    }

    override fun getFragment(): Fragment? {
        return this
    }

    protected fun calculateHeightForFakeStatusBar(view: View): Int {
        return if (isTablet || DeviceConfigurationUtils.isLandscape(view.context)) 0
        else getStatusBarHeightByInsets(view)
    }

    //TODO https://online.sbis.ru/opendoc.html?guid=7e2793d5-ce7c-4914-a7ac-f534626ab463&client=3
    private fun addDefaultTopPadding(view: View) {
        rootContainerForTopPadding?.let {
            view.findViewById<View>(it).run {
                setPadding(
                    paddingLeft,
                    calculateHeightForFakeStatusBar(view),
                    paddingRight,
                    paddingBottom
                )
            }
        }
    }

    /**
     * Получить высоту статус бара
     * @return высота статус бара
     * TODO https://online.sbis.ru/opendoc.html?guid=7e2793d5-ce7c-4914-a7ac-f534626ab463&client=3
     */
    @Px
    private fun getStatusBarHeightByInsets(view: View) =
        ViewCompat.getRootWindowInsets(view)?.getInsets(WindowInsetsCompat.Type.statusBars())?.top
            ?: getStatusBarHeight()

    /**
     * Получить высоту статус бара
     * @return высота статус бара
     * TODO https://online.sbis.ru/opendoc.html?guid=7e2793d5-ce7c-4914-a7ac-f534626ab463&client=3
     */
    private fun getStatusBarHeight(): Int {
        var statusBarHeight = 0
        val statusBarResId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (statusBarResId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(statusBarResId)
        }
        return statusBarHeight
    }

    /**
     * Used to show error in view
     * public access made to be overridden by Contract of view with same method signature.
     *
     * @param errorMessage message of error to show
     */
    @Deprecated("using this method occurs memory leaks", ReplaceWith("showToast(String/resId, length)"))
    open fun showError(errorMessage: CharSequence) {
        showToast(errorMessage)
    }

    /**
     * Показать [Toast] уведомление
     *
     * @param [messageResId] ссылка на строковый ресурс с текстом сообщения
     * @param [length] длительность показа уведомления
     */
    open fun showToast(@StringRes messageResId: Int, length: Int) {
        if (messageResId != 0) {
            context?.let { fragmentContext ->
                showToast(fragmentContext.getString(messageResId), length)
            }
        } else {
            Timber.e("messageResId = 0")
        }
    }

    /**
     * Показать [Toast] уведомление
     *
     * @param [messageResId] ссылка на строковый ресурс с текстом сообщения. Длительность показа
     * уведомления - [Toast.LENGTH_LONG].
     */
    open fun showToast(@StringRes messageResId: Int) {
        showToast(messageResId,  Toast.LENGTH_LONG)
    }

    /**
     * Показать [Toast] уведомление.
     *
     * @param [message] текст сообщения
     * @param [length] длительность показа уведомления, по-умолчанию [Toast.LENGTH_LONG]
     */
    open fun showToast(message: CharSequence, length: Int) {
        val context = context
        if (context != null) {
            SbisPopupNotification.pushToast(context, message)
        } else {
            if (BuildConfig.DEBUG) {
                throw IllegalStateException("Don't call view's methods after it is detached from context")
            } else {
                Timber.e("Attempt to call view method while fragment is detached from context")
            }
        }
    }

    /**
     * Показать [Toast] уведомление.
     *
     * @param [message] текст сообщения.
     * Длительность показа уведомления, по-умолчанию [Toast.LENGTH_LONG]
     */
    open fun showToast(message: CharSequence) {
        showToast(message, Toast.LENGTH_LONG)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun pushScreen() {
        mEventsTracker.pushScreen(javaClass.simpleName)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun popScreen() {
        mEventsTracker.popScreen(javaClass.simpleName)
    }

}