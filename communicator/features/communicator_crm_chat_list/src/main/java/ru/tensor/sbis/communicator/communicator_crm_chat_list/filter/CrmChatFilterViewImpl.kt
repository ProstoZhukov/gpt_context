package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.databinding.CommunicatorCrmChatFilterFragmentBinding
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CrmChatFilterView.Event
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CrmChatFilterView.Model
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.models.CheckableFilterItem
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.models.OpenableFilterItem
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.models.SelectableFilterItem
import ru.tensor.sbis.design.utils.getDimenPx
import java.util.UUID
import ru.tensor.sbis.design.R as DesignR

/**
 * @author da.zhukov
 */
internal class CrmChatFilterViewImpl(
    fragment: Fragment,
    private val binding: CommunicatorCrmChatFilterFragmentBinding
) : BaseMviView<Model, Event>(), CrmChatFilterView, FragmentResultListener {

    override val renderer: ViewRenderer<Model> =
        diff {
            diff(
                get = Model::contentItems,
                set = { adapter.reload(it) }
            )
            diff(
                get = Model::isChanged,
                set = {
                    binding.communicatorCrmChatListFiltersReset.isVisible = it
                }
            )
            diff(
                get = Model::contentIsOpen,
                set = ::onContentChange
            )
            diff(
                get = Model::headerTitle,
                set = {
                    binding.communicatorCrmChatListFiltersTitle.setText(it)
                }
            )
        }

    private val adapter = object : ViewModelAdapter() {
        init {
            cell<CheckableFilterItem>(
                layoutId = R.layout.communicator_crm_chat_checkable_filter_item,
                areItemsTheSame = { a, b -> a.type == b.type },
                areContentsTheSame = { a, b -> a.isSelected == b.isSelected },
            )
            cell<SelectableFilterItem>(
                layoutId = R.layout.communicator_crm_chat_selectable_filter_item,
                areItemsTheSame = { a, b -> a.type == b.type },
                areContentsTheSame = { a, b -> a.isSelected == b.isSelected },
            )
            cell<OpenableFilterItem>(
                layoutId = R.layout.communicator_crm_chat_openable_filter_item,
                areItemsTheSame = { a, b -> a.type == b.type },
                areContentsTheSame = { a, b -> a.value == b.value }
            )
        }
    }

    init {
        with(binding) {
            communicatorCrmChatListFiltersList.run {
                layoutManager = LinearLayoutManager(context)
                adapter = this@CrmChatFilterViewImpl.adapter
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val viewAdapterPosition = parent.getChildAdapterPosition(view)
                        if (viewAdapterPosition != 0) {
                            outRect.top = context.getDimenPx(DesignR.attr.offset_m)
                        } else {
                            outRect.setEmpty()
                        }
                    }
                })
            }
            communicatorCrmChatListFiltersReset.setOnClickListener {
                dispatch(Event.ResetClick)
            }
            communicatorCrmChatListFiltersAccept.setOnClickListener {
                dispatch(Event.ApplyClick)
            }
            communicatorCrmChatListFiltersBack.setOnClickListener {
                dispatch(Event.BackClick)
            }
        }

        fragment.lifecycleScope.launchWhenStarted {
            launch {
                CRMChatListPlugin.recipientSelectionFeatureProvider.get()
                    .getRecipientSelectionResultManager()
                    .getSelectionResultObservable().asFlow().collect {
                        dispatch(
                            Event.ContentItemIsSelected(
                                it.data.allPersonsUuids.asArrayList(),
                                it.data.allPersons.map { it.name.fullName }.asArrayList()
                            )
                        )
                    }
            }
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            CrmChatFilterFragment.REQUEST -> {
                val ids = result.getSerializableUniversally<ArrayList<UUID>>(CrmChatFilterFragment.RESULT_UUIDS)
                val names = result.getSerializableUniversally<ArrayList<String>>(CrmChatFilterFragment.RESULT_NAMES)
                if (!(ids.isNullOrEmpty() || names.isNullOrEmpty())) {
                    dispatch(Event.ContentItemIsSelected(ids, names))
                }
            }
        }
    }

    private fun onContentChange(contentIsOpen: Boolean) {
        binding.communicatorCrmChatListFiltersList.isVisible = !contentIsOpen
        binding.communicatorCrmChatListFilterContainerId.isVisible = contentIsOpen
        binding.communicatorCrmChatListFiltersBack.isVisible = contentIsOpen
    }
}