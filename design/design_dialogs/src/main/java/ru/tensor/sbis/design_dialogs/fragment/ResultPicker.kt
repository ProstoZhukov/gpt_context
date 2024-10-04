package ru.tensor.sbis.design_dialogs.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.design.design_dialogs.R

import ru.tensor.sbis.design.text_span.util.asRoboto
import ru.tensor.sbis.design.util.checkStatusBarShouldBeShown

/**
 * Диалог для отображения заголовка и списка строк, а также получения выбранного элемента.
 * Например, в справочнике сотрудников данный диалог используется для отображения
 * списка телефонов, по которым ему можно познонить.
 */
class ResultPicker : DialogFragment() {

    internal var dialogCode: Int = 0
    private lateinit var items: ArrayList<String>

    companion object {

        private val DIALOG_CODE_STATE = ResultPicker::class.java.canonicalName + ".dialog_code_state"
        private val TITLE_STATE = ResultPicker::class.java.canonicalName + ".title_state"
        private val ITEMS_STATE = ResultPicker::class.java.canonicalName + ".items_state"

        fun newInstance(dialogCode: Int, title: String, items: ArrayList<String>): ResultPicker {
            val dialog = ResultPicker()
            val args = Bundle()
            args.putInt(DIALOG_CODE_STATE, dialogCode)
            args.putString(TITLE_STATE, title)
            args.putStringArrayList(ITEMS_STATE, items)
            dialog.arguments = args
            return dialog
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(TITLE_STATE)
        dialogCode = arguments?.getInt(DIALOG_CODE_STATE)!!
        items = arguments?.getStringArrayList(ITEMS_STATE)!!

        val builder = AlertDialog.Builder(context!!, R.style.SbisAlertDialogTheme)

        builder.setTitle(asRoboto(context!!, title))
        builder.setItems(items.toTypedArray()) {
            _, which ->
            getCheckResultListener()?.onItem(dialogCode, items[which])
        }

        val dialog = builder.create()
        isCancelable = true
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkResultListener(context)
    }

    override fun onResume() {
        super.onResume()
        checkStatusBarShouldBeShown()
    }

    private fun checkResultListener(context: Context?) {
        if (context !is ResultListener &&
            (parentFragment == null || parentFragment !is ResultListener) &&
            (targetFragment == null || targetFragment !is ResultListener)
        ) {
            when {
                targetFragment != null ->
                    throw ClassCastException("$targetFragment must implement ${ResultListener::class.java.simpleName}")
                parentFragment != null ->
                    throw ClassCastException("$parentFragment must implement ${ResultListener::class.java.simpleName}")
                else -> throw ClassCastException("$context must implement ${ResultListener::class.java.simpleName}")
            }
        }
    }

    private fun getCheckResultListener(): ResultListener? {
        if (activity == null && parentFragment == null && targetFragment == null) {
            return null
        }
        if (parentFragment is ResultListener) {
            return parentFragment as ResultListener
        }
        if (targetFragment is ResultListener) {
            return targetFragment as ResultListener
        }
        if (activity is ResultListener) {
            return activity as ResultListener
        }
        throw ClassCastException("$activity must implement ${ResultListener::class.java.simpleName}")
    }

    interface ResultListener {

        fun onItem(dialogCode: Int, item: String)

    }

}