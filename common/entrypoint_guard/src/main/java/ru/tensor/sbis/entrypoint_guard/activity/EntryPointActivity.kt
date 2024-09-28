package ru.tensor.sbis.entrypoint_guard.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard

/**
 * Базовый класс для визуальных [EntryPointGuard.EntryPoint].
 *
 * @author kv.martyshenko
 */
abstract class EntryPointActivity : AppCompatActivity(),
    EntryPointGuard.EntryPoint {

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
                superMethod = { super.onCreate(it) },
                onReady = this::onCreate
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

    final override fun onNewIntent(intent: Intent?) {
        EntryPointGuard.activityAssistant
            .interceptOnNewIntent(
                this,
                intent,
                superMethod = { super.onNewIntent(it) },
                onReady = this::onIntent
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

    final override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    final override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    protected abstract fun onCreate(activity: AppCompatActivity, parent: FrameLayout, savedInstanceState: Bundle?)

    protected open fun onIntent(activity: AppCompatActivity, intent: Intent?) = Unit

}