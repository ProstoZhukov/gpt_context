package ru.tensor.sbis.message_panel.view

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import ru.tensor.sbis.base_components.BaseProgressDialogFragment
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.common.util.ResourceProvider
import java.lang.ref.WeakReference

/**
 * @author Subbotenko Dmitry
 */
data class ProgressDialogLiveData(
        val shown: Boolean = false,
        val textResId: Int = 0,
        val cancellable: Boolean = true
)

/**
 * @author Subbotenko Dmitry
 */
class ProgressDialogDelegate(fragment: Fragment, private val resourceProvider: ResourceProvider) {

    companion object {
        private val PROGRESS_DIALOG_FRAGMENT_TAG = ProgressDialogDelegate::class.java.simpleName + ".progress_dialog_fragment"
    }

    private val fragmentRef = WeakReference(fragment)
    private var progressDialogFragment =
        fragment.activity?.supportFragmentManager?.findFragmentByTag(PROGRESS_DIALOG_FRAGMENT_TAG) as? BaseProgressDialogFragment?

    @CheckReturnValue
    fun bind(liveData: Observable<RxContainer<ProgressDialogLiveData>>) = liveData
            .subscribe {
                val dialogLiveData = it.value
                if (dialogLiveData?.shown == true) showProgressDialog(dialogLiveData.textResId, dialogLiveData.cancellable)
                else hideProgressDialog()
            }

    private fun showProgressDialog(@StringRes textResId: Int, cancellable: Boolean) {
        fragmentRef.get()?.fragmentManager?.let { fragmentManager ->
            progressDialogFragment = fragmentManager
                .findFragmentByTag(PROGRESS_DIALOG_FRAGMENT_TAG) as? BaseProgressDialogFragment?
            if (progressDialogFragment != null) return
            progressDialogFragment = BaseProgressDialogFragment.newInstance(cancellable)
            progressDialogFragment!!.init(null, resourceProvider.getString(textResId))
            progressDialogFragment!!.show(fragmentManager, PROGRESS_DIALOG_FRAGMENT_TAG)
        }
    }

    private fun hideProgressDialog() {
        progressDialogFragment?.let {
            if (it.isAdded) {
                it.dismiss()
                progressDialogFragment = null
            }
        }
    }
}