package ru.tensor.sbis.design_notification.popup.state_machine.util

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.doOnAttach
import androidx.core.view.doOnLayout
import curtains.Curtains
import curtains.OnRootViewsChangedListener
import curtains.phoneWindow
import ru.tensor.sbis.design.utils.extentions.doOnDetachedFromWindow
import ru.tensor.sbis.design_notification.R
import timber.log.Timber

internal typealias ViewFactory = (context: Context) -> View

/**
 * Управляет отображением и скрытием [View] панели-информера, обеспечивая её показ поверх любой [Activity] или диалога.
 * Фактически, отображает отдельный [View] панели для каждой [Activity], а также при появлении нового диалога.
 *
 * @author us.bessonov
 */
@SuppressLint("StaticFieldLeak")
internal class PopupWindowViewDisplayManager(
    private val statusBarColorHelper: StatusBarColorHelper = StatusBarColorHelper()
) : OnRootViewsChangedListener {

    init {
        Curtains.onRootViewsChangedListeners.add(this)
    }

    private var viewFactory: (ViewFactory)? = null
    private val displayedViews = mutableListOf<View>()
    private var animator: Animator? = null
    private var cleanUpAction: ((view: View) -> Unit)? = null


    /**
     * Отображает панель поверх каждой имеющейся [Activity] приложения и конфигурирует жизненный цикл всех [View]
     * панелей.
     */
    fun deployView(
        duration: DisplayDuration,
        handler: Handler,
        showViewAction: ShowViewAction,
        hideRunnable: Runnable,
        onDetachedFromWindow: () -> Unit,
        viewFactory: ViewFactory
    ) {
        this.viewFactory = viewFactory
        displayedViews.clear()
        cleanUpAction = { view ->
            displayedViews.remove(view)
            if (displayedViews.isEmpty()) {
                handler.removeCallbacks(hideRunnable)
                onDetachedFromWindow()
                resetAnimator()
                this.viewFactory = null
                cleanUpAction = null
            }
        }
        val views = Curtains.rootViews.filterNot(::isPopupView)
            .mapNotNull { getContext(it).getActivity() }
            .toSet()
            .mapNotNull { context ->
                createAndAddView(viewFactory, context)
            }

        doAtFirstLayout(views) {
            showViewAction.showViews(displayedViews)
            if (duration == DisplayDuration.Default) {
                handler.postDelayed(hideRunnable, SHOW_DURATION)
            }
        }
    }

    /**
     * Анимированно скрывает все имеющиеся [View] панели-информера.
     */
    fun hideViews(action: HideViewAction) {
        resetAnimator()
        val removedViews = displayedViews.toList()
        animator = action.hideViews(removedViews) {
            removedViews.plus(displayedViews).distinct()
                .forEach(::removeView)
        }
    }

    override fun onRootViewsChanged(view: View, added: Boolean) {
        if (isPopupView(view)) return
        if (added) {
            view.doOnAttach {
                viewFactory?.let { factory ->
                    createAndAddView(factory, getContext(view))
                }
            }
        } else if (view.isActivityFinishing()) {
            /*
            Если ушло окно какой-либо Activity, то удаляем связанные панели сразу. Иначе, если это происходит при смене
            ориентации экрана, то вызова onDetachedFromWindow у View панели не произойдёт.
             */
            displayedViews.filter { it.getActivity() == getActivityView(view)?.getActivity() }
                .forEach { removeView(it) }
        }
    }

    private fun View.isActivityFinishing() =
        phoneWindow?.context?.getActivity()?.isFinishing == true

    private fun resetAnimator() {
        animator?.apply {
            removeAllListeners()
            cancel()
        }
        animator = null
    }

    private fun createAndAddView(viewFactory: ViewFactory, context: Context): View? {
        val view = try {
            viewFactory.invoke(context)
        } catch (e: Exception) {
            Timber.e(
                "Cannot create popup notification using $context with theme ${context.theme}: ${e.message}"
            )
            return null
        }
        view.setTag(POPUP_VIEW_TAG, Unit)
        displayedViews.add(view)
        statusBarColorHelper.onShowPopup(view)
        view.doOnDetachedFromWindow {
            statusBarColorHelper.onPopupHidden(view)
            cleanUpAction?.invoke(it)
        }
        return if (addView(view)) {
            view
        } else {
            cleanUpAction?.invoke(view)
            null
        }
    }

    private fun addView(view: View): Boolean {
        val lp = WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION).apply {
            format = PixelFormat.TRANSLUCENT
            flags =
                flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            }
        }

        if (view.getActivity()?.isFinishing == false) {
            return addView(view, lp)
        }
        return false
    }

    private fun addView(view: View, lp: WindowManager.LayoutParams): Boolean {
        return try {
            getWindowManager(view).addView(view, lp)
            true
        } catch (e: Exception) {
            Timber.e("WindowManager is unable to add popup notification view")
            false
        }
    }

    private fun doAtFirstLayout(views: List<View>, action: () -> Unit) {
        var isDone = false
        views.forEach {
            it.doOnLayout {
                if (isDone) return@doOnLayout
                action()
                isDone = true
            }
        }
    }

    private fun removeView(view: View) = view.apply {
        if (isAttachedToWindow) {
            getWindowManager(this).removeViewImmediate(this)
        }
    }

    private fun getWindowManager(view: View) = view.context.getSystemService(Activity.WINDOW_SERVICE) as WindowManager

    private fun getActivityView(root: View) = (root as? ViewGroup)?.getChildAt(0)

    private fun getContext(root: View) = getActivityView(root)?.context
        ?: root.context

    private fun isPopupView(view: View) = view.getTag(POPUP_VIEW_TAG) != null

    private fun View.getActivity(): Activity? = context.getActivity()

    private tailrec fun Context.getActivity(): Activity? =
        when (this) {
            is Activity -> this
            is ContextWrapper -> baseContext.getActivity()
            else -> null
        }

}

private const val SHOW_DURATION = 3000L
private val POPUP_VIEW_TAG = R.id.design_notification_popup_view_tag