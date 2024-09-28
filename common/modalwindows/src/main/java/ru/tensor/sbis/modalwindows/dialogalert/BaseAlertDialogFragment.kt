package ru.tensor.sbis.modalwindows.dialogalert

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.modalwindows.BuildConfig
import ru.tensor.sbis.modalwindows.R
import ru.tensor.sbis.modalwindows.dialogalert.AlertDialogCreator.TITLE_MAX_LINES_NOT_DEFINED
import timber.log.Timber
import java.io.Serializable

/**
 * Базовый класс для диалога
 */
abstract class BaseAlertDialogFragment : DialogFragment() {

    /**
     * Флаг, отвечающий за необходимость реализации в прикладном коде интерфейса для обработки событий
     */
    protected var processEvents = true

    /**
     * Флаг для проверки наличия заголовка в наследнике
     */
    protected var hasTitle = false

    /**@SelfDocumented*/
    protected lateinit var contentView: View
    protected var isUsingCustomDeviceDensity = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        arguments?.getBoolean(DIALOG_REQUIRE_LISTENER_ARG)
            ?.let { processEvents = it }
        if (processEvents) {
            checkListener()
        }

        val title = arguments?.getString(DIALOG_TITLE_ARG)
        val titleMaxLines = arguments?.getInt(DIALOG_TITLE_MAX_LINES_ARG, TITLE_MAX_LINES_NOT_DEFINED)
            ?: TITLE_MAX_LINES_NOT_DEFINED
        val negativeButtonText = arguments?.getString(DIALOG_NEGATIVE_BUTTON_TEXT_ARG)
        val positiveButtonText = arguments?.getString(DIALOG_POSITIVE_BUTTON_TEXT_ARG)
        val positiveButtonForRemoval = arguments?.getBoolean(
            DIALOG_POSITIVE_BUTTON_FOR_REMOVAL_ARG,
            false
        )
            ?: false

        hasTitle = title != null
        contentView = AlertDialogCreator.createContainer(requireActivity(),
                                                           hasTitle,
                                                           positiveButtonText,
                                                           negativeButtonText,
                                                           positiveButtonForRemoval,
                                                           { onPositiveButtonClick() },
                                                           { onNegativeButtonClick() })
        title?.let { AlertDialogCreator.addTitle(contentView, it, titleMaxLines) }
        addContent(contentView)
        val builder = AlertDialog.Builder(requireContext())
        arguments?.let { hasTitle = it.containsKey(DIALOG_TITLE_ARG) }
        builder.setView(contentView)
        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        if (dialog?.context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE)
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Если в приложении поддерживается изменение масштаба, то диалог в портретном режиме
        // в крупном масштабе выезжает за границы экрана, поэтому фиксируем ширину.
        if (isUsingCustomDeviceDensity && resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    /**@SelfDocumented*/
    protected abstract fun addContent(container: View)

    /**
     * Проверяем, присутствует ли слушатель для обработки результатов. В случаях, когда слушатель
     * необходим, его отсутствие считается ошибкой.
     */
    private fun checkListener() {
        if (!hasListener()) {
            if (BuildConfig.DEBUG) {
                throw ClassCastException("Родительский компонент должен реализовывать интерфейс для обработки выбранного элемента")
            } else {
                SbisPopupNotification.pushToast(requireContext(), R.string.modalwindows_action_cant_be_done_error)
                dismiss()
            }
        }
    }

    /**
     * Проверка на наличие слушателя, необходимого для обработки результата работы с диалоговым окном
     */
    protected abstract fun hasListener(): Boolean

    /**
     * Получение слушателя, реализующего интерфейс [T]
     */
    protected inline fun <reified T : Any> getListener(): T? {
        val outerListenerOrNull = getOuterListenerOrNull()
        return when {
            outerListenerOrNull is T -> outerListenerOrNull
            targetFragment is T -> (targetFragment as T)
            parentFragment is T -> (parentFragment as T)
            activity is T -> (activity as T)
            else -> null
        }
    }

    /**@SelfDocumented*/
    protected fun getOuterListenerOrNull(): Any? {
        val listenerFactory = arguments?.getSerializable(DIALOG_LISTENER_FACTORY_ARG)
        if (listenerFactory == null || listenerFactory !is ListenerFactory) return null

        try {
            return listenerFactory(this)
        } catch (e: Throwable) {
            Timber.w(e, "Unable to create dialog's listener. Dialog arguments: %s", arguments)
        }
        return null
    }

    /**@SelfDocumented*/
    open fun requestTitle(title: String): BaseAlertDialogFragment {
        getOrCreateArguments().putString(
            DIALOG_TITLE_ARG,
            title
        )
        return this
    }

    /**@SelfDocumented*/
    open fun requestTitleMaxLines(maxLines: Int): BaseAlertDialogFragment {
        getOrCreateArguments().putInt(
            DIALOG_TITLE_MAX_LINES_ARG,
            maxLines
        )
        return this
    }

    /**@SelfDocumented*/
    open fun requestPositiveButton(
        buttonText: String,
        forRemoval: Boolean = false
    ): BaseAlertDialogFragment {
        getOrCreateArguments().putString(
            DIALOG_POSITIVE_BUTTON_TEXT_ARG,
            buttonText
        )
        getOrCreateArguments().putBoolean(
            DIALOG_POSITIVE_BUTTON_FOR_REMOVAL_ARG,
            forRemoval
        )
        return this
    }

    /**@SelfDocumented*/
    open fun requestNegativeButton(buttonText: String): BaseAlertDialogFragment {
        getOrCreateArguments().putString(
            DIALOG_NEGATIVE_BUTTON_TEXT_ARG,
            buttonText
        )
        return this
    }

    /**
     * Установка флага, отвечающего за необходимость обработки в вызывающем коде таких событий
     * диалогового окна как нажатия на кнопки, клики по элементам списка и т.п.
     */
    open fun setEventProcessingRequired(required: Boolean): BaseAlertDialogFragment {
        getOrCreateArguments().putBoolean(
            DIALOG_REQUIRE_LISTENER_ARG,
            required
        )
        return this
    }

    /**@SelfDocumented*/
    protected fun getOrCreateArguments(): Bundle {
        if (arguments == null) {
            arguments = Bundle()
        }
        return requireArguments()
    }

    /**
     * Произвольный слышатель событий, который может быть установлен после создания фрагмента.
     * Ссылка будет почищена при удалении фрагмента диалога.
     */
    @Suppress("unused")
    fun setListenerFactory(listenerFactory: ListenerFactory) : BaseAlertDialogFragment {
        getOrCreateArguments().putSerializable(
            DIALOG_LISTENER_FACTORY_ARG,
            listenerFactory
        )
        return this
    }

    /**@SelfDocumented*/
    protected abstract fun onPositiveButtonClick()

    /**@SelfDocumented*/
    protected abstract fun onNegativeButtonClick()

    companion object {
        private const val DIALOG_TITLE_ARG = "DIALOG_TITLE_ARG"
        private const val DIALOG_TITLE_MAX_LINES_ARG = "DIALOG_TITLE_MAX_LINES_ARG"
        private const val DIALOG_POSITIVE_BUTTON_TEXT_ARG = "DIALOG_POSITIVE_BUTTON_TEXT_ARG"
        private const val DIALOG_POSITIVE_BUTTON_FOR_REMOVAL_ARG = "DIALOG_POSITIVE_BUTTON_FOR_REMOVAL_ARG"
        private const val DIALOG_NEGATIVE_BUTTON_TEXT_ARG = "DIALOG_NEGATIVE_BUTTON_TEXT_ARG"
        private const val DIALOG_REQUIRE_LISTENER_ARG = "DIALOG_REQUIRE_LISTENER_ARG"
        private const val DIALOG_LISTENER_FACTORY_ARG = "DIALOG_LISTENER_FACTORY_ARG"
    }

    /**@SelfDocumented*/
    interface ListenerFactory: (BaseAlertDialogFragment) -> Any?, Serializable
}