package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.files_filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.core.view.children
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilter
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.design.checkbox.SbisCheckboxView
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxContent
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxSize
import ru.tensor.sbis.design.checkbox.utils.setText
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable

internal class ConversationInformationFilesFilterFragment : BaseFragment(), Content {

    /**
     * Создатель экземпляра контента.
     * Используется контейнером для создания экземпляра фрагмента, содержащего отображаемый контент.
     */
    @Parcelize
    internal class Creator(private val selectedFilterTypes: List<ConversationInformationFilter>) :
        ContentCreatorParcelable {

        override fun createFragment(): Fragment =
            ConversationInformationFilesFilterFragment().withArgs {
                putParcelableArrayList(SELECTED_FILTER_TYPES, selectedFilterTypes.asArrayList())
            }
    }
    private var selectedFilters: MutableList<ConversationInformationFilter> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedFilters = savedInstanceState?.getSelectedFilters()
            ?: (arguments?.getSelectedFilters() ?: mutableListOf())
    }

    private fun Bundle.getSelectedFilters() =
        getParcelableArrayList<ConversationInformationFilter>(SELECTED_FILTER_TYPES)
            ?.castTo<MutableList<ConversationInformationFilter>>() ?: mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.communicator_fragment_conversation_information_files_filter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val allFilter = selectedFilters.firstOrNull()?.getAllFilters() ?: emptyList()
        view.findViewById<LinearLayout>(R.id.communicator_conversation_information_files_filters_container).apply {
            for (conversationInformationFilter in allFilter) {
                addView(
                    SbisCheckboxView(view.context).apply {
                        size = SbisCheckboxSize.SMALL
                        setText(view.context.getString(conversationInformationFilter.caption))
                        setCheckedByFilterType(conversationInformationFilter)
                        setOnClickListener(conversationInformationFilter)
                    },
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                        setMargins(0, Offset.XL.getDimenPx(view.context), 0, 0)
                    }
                )
            }
        }
        if (selectedFilters.size == 1) {
            disableLastCheckBox()
        }
    }

    private fun SbisCheckboxView.setCheckedByFilterType(type: ConversationInformationFilter) {
        isCheckBoxChecked = selectedFilters.contains(type)
    }

    private fun SbisCheckboxView.setOnClickListener(type: ConversationInformationFilter) = setOnClickListener {
        if (isCheckBoxChecked) {
            selectedFilters.add(type)
            enableAllCheckBox()
        } else {
            selectedFilters.remove(type)
            if (selectedFilters.size == 1) {
                disableLastCheckBox()
            }
        }
    }

    private fun disableLastCheckBox() {
        selectedFilters.firstOrNull()?.caption?.let {
            val selectedText = requireContext().getString(it)
            getCheckboxesContainer().children.find { checkbox ->
                checkbox.castTo<SbisCheckboxView>()
                    ?.content?.castTo<SbisCheckboxContent.TextContent>()
                    ?.text == selectedText
            }?.isEnabled = false
        }
    }

    private fun enableAllCheckBox() {
        getCheckboxesContainer().children.forEach {
            it.castTo<SbisCheckboxView>()?.isEnabled = true
        }
    }

    private fun getCheckboxesContainer(): LinearLayout =
        requireView().findViewById(R.id.communicator_conversation_information_files_filters_container)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(SELECTED_FILTER_TYPES, selectedFilters.asArrayList())
    }

    override fun onBackPressed(): Boolean = false

    override fun onCloseContent() {
        super.onCloseContent()
        parentFragment?.parentFragmentManager?.setFragmentResult(
            FILTER_SELECTION_RESULT_KEY,
            Bundle().apply { putParcelableArrayList(SELECTED_FILTER_TYPES, selectedFilters.asArrayList()) }
        )
    }
}

internal const val SELECTED_FILTER_TYPES = "SELECTED_FILTER_TYPES"
internal const val FILTER_SELECTION_RESULT_KEY = "FILTER_SELECTION_RESULT_KEY"
