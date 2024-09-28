package ru.tensor.sbis.communicator.communicator_import_contacts.importcontactsconfirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.common.import_contacts.ContactsImportConfirmationListener
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_import_contacts.ImportContactsPlugin
import ru.tensor.sbis.communicator.communicator_import_contacts.R
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design.design_dialogs.R as RDesignDialogs

/**
 * Фрагмент импорта контактов
 *
 * @author rv.krohalev
 */
class ImportContactsConfirmationFragment : BottomSheetDialogFragment() {

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, RDesignDialogs.style.TransparentBottomSheetTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.communicator_import_contacts_confirmation_fragment, container, false)
        val panelContainer = view.findViewById<ViewGroup>(R.id.communicator_import_contacts_confirmation_content_container_id)
        inflater.inflate(R.layout.communicator_import_contacts_confirmation_content, panelContainer, true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.castTo<BottomSheetDialog>()?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }

        view.findViewById<MovablePanel>(R.id.communicator_import_contacts_confirmation_panel)?.apply {
            val expandedPeekHeight = MovablePanelPeekHeight.FitToContent()
            val hiddenPeekHeight = MovablePanelPeekHeight.Percent(0F)
            setPeekHeightList(listOf(hiddenPeekHeight, expandedPeekHeight), expandedPeekHeight)

            getPanelStateSubject().subscribe {
                if (it.castTo<MovablePanelPeekHeight.Percent>()?.value == 0.0f) {
                    dismiss()
                }
            }.storeIn(disposables)
        }
        view.findViewById<TextView>(R.id.communicator_import_contacts_confirmation_message_text)?.text = getCorrectlyText()

        view.findViewById<Button>(R.id.communicator_import_contacts_confirmation_confirm_button)?.setOnClickListener {
            parentFragment?.castTo<ContactsImportConfirmationListener>()?.contactsImportConfirmed()
            dismiss()
        }

        view.findViewById<Button>(R.id.communicator_import_contacts_confirmation_decline_button)?.setOnClickListener {
            parentFragment?.castTo<ContactsImportConfirmationListener>()?.contactsImportDeclined()
            dismiss()
        }
    }

    /**
     * Предоставляет текст с учетом названия приложения
     */
    private fun getCorrectlyText(): String {
        val appName = ImportContactsPlugin.customizationOptions.appName
            ?: throw IllegalStateException("Укажите имя приложения для использования импорта контактов")
        val currentText = getString(RCommunicatorDesign.string.communicator_contacts_import_message)
        return currentText.replace("AppName", appName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }
 }
