@file:Suppress("DEPRECATION")

package ru.tensor.sbis.base_components

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.android_ext_decl.AndroidComponent
import ru.tensor.sbis.common.util.ViewHolderUtil
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * Базовый диалог фрагмент
 */
class BaseProgressDialogFragment : DialogFragment(), AndroidComponent {

    private var mTitle: String? = null
    private var mMessage: String? = null
    private var mMessageView: SbisTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = arguments != null && requireArguments().getBoolean(CANCELABLE_ARGUMENT_KEY)

        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(TITLE_STATE)
            mMessage = savedInstanceState.getString(MESSAGE_STATE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.base_components_progress_dialog, container, false).apply {
            findViewById<SbisTextView>(R.id.base_components_progress_dialog_title)?.apply {
                ViewHolderUtil.setSbisTextWithVisibility(this, mTitle)
            }
            mMessageView = findViewById<SbisTextView>(R.id.base_components_progress_dialog_message)?.apply {
                ViewHolderUtil.setSbisTextWithVisibility(this, mMessage)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mMessageView = null
    }

    /** Обновляет сообщение в диалоге. */
    fun updateMessage(newMessage: String) {
        mMessage = newMessage
        ViewHolderUtil.setSbisTextWithVisibility(mMessageView, newMessage)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (parentFragment is ProgressDialogCallbacks) {
            (parentFragment as ProgressDialogCallbacks).onProgressDialogDismiss()
        } else if (context is ProgressDialogCallbacks) {
            (context as ProgressDialogCallbacks).onProgressDialogDismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if (parentFragment is ProgressDialogCallbacks) {
            (parentFragment as ProgressDialogCallbacks).onProgressDialogCancel()
        } else if (context is ProgressDialogCallbacks) {
            (context as ProgressDialogCallbacks).onProgressDialogCancel()
        }
    }

    /**@SelfDocumented*/
    fun init(title: String?, message: String) {
        mTitle = title
        mMessage = message
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TITLE_STATE, mTitle)
        outState.putString(MESSAGE_STATE, mMessage)
    }

    override fun getSupportFragmentManager(): FragmentManager {
        return requireFragmentManager()
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        if (!isAdded) {
            manager.beginTransaction()
                    .add(this, tag)
                    .commitNow()
        }
    }

    override fun getFragment() = this

    companion object {

        private val TITLE_STATE = BaseProgressDialogFragment::class.java.canonicalName!! + ".title_state"
        private val MESSAGE_STATE = BaseProgressDialogFragment::class.java.canonicalName!! + ".message_state"
        private val CANCELABLE_ARGUMENT_KEY = BaseProgressDialogFragment::class.java.canonicalName!! + ".cancelable_key"

        @JvmStatic
        fun newInstance(cancelable: Boolean): BaseProgressDialogFragment {
            val progressDialogFragment = BaseProgressDialogFragment()
            val arguments = Bundle()
            arguments.putBoolean(CANCELABLE_ARGUMENT_KEY, cancelable)
            progressDialogFragment.arguments = arguments
            return progressDialogFragment
        }
    }
}
