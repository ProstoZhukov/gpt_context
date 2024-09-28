package ru.tensor.sbis.entrypoint_guard.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityBehaviour
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityContentFactory
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.entrypoint_guard.activity.contract.controllers.impl.KeyEventProxy
import ru.tensor.sbis.entrypoint_guard.activity.contract.controllers.impl.MotionEventProxy

/**
 * Базовая [EntryPoint] активность.
 * Позволяет накручивать поведение через передачу [behaviourElements], которые будут выполнены в определенный момент ЖЦ.
 *
 * Примеры использования:
 *
 * ```kotlin
 * class NewsActivity : BaseEntryPointActivity<NewsActivity>(
 *     contentFactory = { act, frame, state ->
 *         frame.addView(...)
 *     }
 * ),
 *     UserActivityTrackable by trackableActivity<NewsActivity>(),
 *     AuthAware by authAware()
 *
 * class AdfsActivity : AloneFragmentEntryPointActivity<AdfsActivity>(
 *     aloneFragmentContentFactory = AloneFragmentContentFactory({ _, _ -> Fragment() })
 * ),
 *     UserActivityTrackable by trackableActivity<AdfsActivity>()
 *
 * class AnySwipeBackActivity(
 *     override val strategy: SwipeBackAware.Strategy = SwipeBackAware.Strategy.ENABLED
 * ) : BaseEntryPointActivity<AnySwipeBackActivity>(
 *     contentFactory = { act, frame, state ->
 *         frame.addView(...)
 *     },
 *     DefaultSwipeBackBehaviour()
 * ), SwipeBackAware
 *
 * class CustomSwipeBackActivity(
 *     override val strategy: SwipeBackAware.Strategy = SwipeBackAware.Strategy.ENABLED
 * ) : BaseEntryPointActivity<CustomSwipeBackActivity>(
 *     contentFactory = { act, frame, state ->
 *         frame.addView(...)
 *         var state = ...
 *
 *         act.lifecycle.addObserver(object : DefaultLifecycleObserver {
 *             override fun onStart(owner: LifecycleOwner) {
 *                 super.onStart(owner)
 *             }
 *         })
 *     },
 *     DefaultSwipeBackBehaviour { a, c ->
 *         SwipeBackCustomListener(a, c)
 *     }
 * ), SwipeBackAware {
 *
 *     class SwipeBackCustomListener(
 *         private val activity: CustomSwipeBackActivity,
 *         private val swipeBackController: SwipeBackController
 *     ) : SwipeBackLayout.SwipeBackListener {
 *         override fun onViewPositionChanged(fractionAnchor: Float, fractionScreen: Float) {
 *             swipeBackController.animateSwipe(fractionScreen)
 *         }
 *
 *         override fun onViewGoneBySwipe() {
 *             activity.finish()
 *
 *             //R.anim.instant_fade_out - моментальное изменение прозрачности до 0
 *             //Требуется, чтобы окно активности не моргнуло после свайпа
 *             activity.overridePendingTransition(0, R.anim.swipeback_instant_fade_out)
 *         }
 *     }
 * }
 *
 * class Custom2SwipeBackActivity(
 *     override val strategy: SwipeBackAware.Strategy = SwipeBackAware.Strategy.ENABLED
 * ) : BaseEntryPointActivity<Custom2SwipeBackActivity>(
 *     contentFactory = { act, frame, state ->
 *         frame.addView(TextView(act))
 *
 *         act.addOnNewIntentListener { intent ->  }
 *
 *         val swipeController = act.findSwipeBackController()
 *
 *         if (swipeController != null) {
 *             swipeController.swipeBackLayout.setOnSwipeBackListener(...)
 *         }
 *     }
 * ), SwipeBackAware
 * ````
 *  @author kv.martyshenko
 */
abstract class FlexibleEntryPointActivity<T : FlexibleEntryPointActivity<T>>(
    private val contentFactory: ActivityContentFactory<T>,
    vararg behaviours: ActivityBehaviour<T>
) : AppCompatActivity(),
    EntryPointGuard.EntryPoint {
    private val behaviourElements = behaviours

    final override fun attachBaseContext(newBase: Context?) {
        EntryPointGuard.activityAssistant
            .interceptAttachBaseContext(this, newBase) {
                super.attachBaseContext(it)
            }
    }

    final override fun onCreate(savedInstanceState: Bundle?) {
        EntryPointGuard.activityAssistant
            .interceptOnCreate(
                this,
                savedInstanceState,
                superMethod = {
                    setupControllers()
                    behaviourElements.forEach { el -> el.onPreCreate(this as T) }
                    super.onCreate(it)
                },
                onContainerInflated = {
                    behaviourElements.forEach { el -> el.onCreate(this as T) }
                },
                onReady = { activity, parent, savedState ->
                    contentFactory.create(activity as T, parent, savedState)
                    behaviourElements.forEach { el -> el.onReady(this as T, parent, savedState) }
                }
            )
    }

    @Deprecated("Нельзя использовать напрямую, чтобы не сломать механику проверки инициализации")
    final override fun setContentView(view: View?) {
        super.setContentView(view)
    }

    @Deprecated("Нельзя использовать напрямую, чтобы не сломать механику проверки инициализации")
    final override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }
    @Deprecated("Нельзя использовать напрямую, чтобы не сломать механику проверки инициализации")
    final override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
    }

    final override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    final override fun onStart() {
        super.onStart()
    }

    final override fun onResume() {
        super.onResume()
    }

    final override fun onPause() {
        super.onPause()
    }

    final override fun onStop() {
        super.onStop()
    }

    final override fun onDestroy() {
        super.onDestroy()
    }

    @SuppressLint("MissingSuperCall")
    final override fun onNewIntent(intent: Intent?) {
        EntryPointGuard.activityAssistant
            .interceptOnNewIntent(
                this,
                intent,
                superMethod = { },
                onReady = { _, intent ->
                    super.onNewIntent(intent)
                }
            )
    }

    final override fun onUserLeaveHint() {
        EntryPointGuard.activityAssistant
            .interceptOnUserLeaveHint(
                this,
                superMethod = {
                    super.onUserLeaveHint()
                }
            )
    }

    final override fun onUserInteraction() {
        EntryPointGuard.activityAssistant
            .interceptOnUserInteraction(
                this,
                superMethod = {
                    super.onUserInteraction()
                }
            )
    }

    final override fun onBackPressed() {
        super.onBackPressed()
    }

    final override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    final override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    final override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    // region KeyEvent
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val fallback = { ev: KeyEvent? -> super.dispatchKeyEvent(ev) }
        return findKeyEventController()?.dispatchKeyEvent(event, fallback) ?: fallback(event)
    }

    final override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        val fallback = { code: Int, ev: KeyEvent? -> super.onKeyLongPress(code, ev) }
        return findKeyEventController()?.onKeyLongPress(keyCode, event, fallback) ?: fallback(keyCode, event)
    }

    final override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        val fallback = { code: Int, ev: KeyEvent? -> super.onKeyUp(code, ev) }
        return findKeyEventController()?.onKeyUp(keyCode, event, fallback) ?: fallback(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val fallback = { code: Int, ev: KeyEvent? -> super.onKeyDown(code, ev) }
        return findKeyEventController()?.onKeyDown(keyCode, event, fallback) ?: fallback(keyCode, event)
    }

    final override fun onKeyMultiple(keyCode: Int, repeatCount: Int, event: KeyEvent?): Boolean {
        val fallback = { code: Int, count: Int,  ev: KeyEvent? -> super.onKeyMultiple(code, count, ev) }
        return findKeyEventController()?.onKeyMultiple(keyCode, repeatCount, event, fallback)
            ?: fallback(keyCode, repeatCount, event)
    }
    // endregion KeyEvent

    // region MotionEvent
    final override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val fallback = { value: MotionEvent -> super.dispatchTouchEvent(value) }
        return findMotionEventController()?.dispatchTouchEvent(ev, fallback) ?: fallback(ev)
    }

    final override fun onTouchEvent(event: MotionEvent): Boolean {
        val fallback = { value: MotionEvent -> super.onTouchEvent(value) }
        return findMotionEventController()?.onTouchEvent(event, fallback) ?: fallback(event)
    }
    // endregion MotionEvent

    private fun setupControllers() {
        controllersHolder().apply {
            keyEventController = KeyEventProxy()
            motionEventController = MotionEventProxy()

            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    reset()
                    owner.lifecycle.removeObserver(this)
                }
            })
        }
    }
}

private fun <T> T.controllersHolder(): FlexibleActivityControllersHolder where T : AppCompatActivity =
    ViewModelProvider(this)[FlexibleActivityControllersHolder::class.java]
