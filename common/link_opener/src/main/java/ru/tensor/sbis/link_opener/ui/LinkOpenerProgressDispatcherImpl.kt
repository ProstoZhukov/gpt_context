package ru.tensor.sbis.link_opener.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.base_components.BaseProgressDialogFragment
import ru.tensor.sbis.common.lifecycle.AbstractActivityLifecycleCallbacks
import ru.tensor.sbis.link_opener.R
import ru.tensor.sbis.link_opener.contract.LinkOpenerFeatureConfiguration
import javax.inject.Inject

/**
 * Реализация [LinkOpenerProgressDispatcher].
 *
 * @param configuration конфигурация использования компонента.
 *
 * @author as.chadov
 */
internal class LinkOpenerProgressDispatcherImpl @Inject constructor(
    private val context: Context,
    private val configuration: LinkOpenerFeatureConfiguration,
) : LinkOpenerProgressDispatcher,
    AbstractActivityLifecycleCallbacks() {

    private var isRegistered = false
    private var requiredProgress = false

    /** Текущий компонент. */
    private var component: Activity? = null
    private val application get() = context.applicationContext as Application

    /** [LinkOpenerProgressDispatcher.register] */
    override fun register() {
        if (!configuration.showProgress) return
        if (!isRegistered) {
            isRegistered = true
            application.registerActivityLifecycleCallbacks(this)
        }
    }

    /** [LinkOpenerProgressDispatcher.unregister] */
    override fun unregister() {
        if (!configuration.showProgress) return
        component?.let(::removeProgressDialog)
        requiredProgress = false
        component = null
        if (isRegistered) {
            isRegistered = false
            application.unregisterActivityLifecycleCallbacks(this)
        }
    }

    /** [LinkOpenerProgressDispatcher.showProgress] */
    override fun showProgress() {
        if (!configuration.showProgress) return
        requiredProgress = true
        component?.let(::addProgressDialog)
    }

    // region AbstractActivityLifecycleCallbacks
    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        component = activity
        if (requiredProgress) {
            addProgressDialog(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)
        component = null
        removeProgressDialog(activity)
    }
    // endregion AbstractActivityLifecycleCallbacks

    /** @SelfDocumented */
    private fun addProgressDialog(activity: Activity) {
        if (activity.findProgressDialog() != null) {
            return
        }
        if (activity is FragmentActivity) {
            BaseProgressDialogFragment.newInstance(true).apply {
                init(null, activity.getString(R.string.link_opener_please_wait))
                showNow(activity.supportFragmentManager, PROGRESS_DIALOG_FRAGMENT_TAG)
            }
        }
    }

    /** @SelfDocumented */
    private fun removeProgressDialog(activity: Activity) {
        val dialog = activity.findProgressDialog() ?: return
        if (dialog.isAdded) {
            dialog.dismiss()
        }
    }

    /** @SelfDocumented */
    private fun Activity.findProgressDialog(): DialogFragment? {
        if (this !is FragmentActivity) {
            return null
        }
        return supportFragmentManager.findFragmentByTag(PROGRESS_DIALOG_FRAGMENT_TAG) as? DialogFragment
    }

    private companion object {
        const val PROGRESS_DIALOG_FRAGMENT_TAG = "link_opener_progress_dialog_fragment"
    }
}