package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter
import ru.tensor.sbis.base_components.adapter.OnItemClickListener
import ru.tensor.sbis.base_components.adapter.contacts.holder.OnContactPhotoClickListener
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel
import ru.tensor.sbis.communicator.core.views.contact_view.ContactView
import ru.tensor.sbis.communicator.core.views.contact_view.ContactViewModel
import ru.tensor.sbis.communicator.core.views.contact_view.highlightSpansFrom
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate
import ru.tensor.sbis.persons.util.formatName
import ru.tensor.sbis.design.R as RDesign

/**
 * Адаптер списка потенциальных контактов на добавление.
 *
 * @author da.zhukov
 */
internal class AddContactListAdapter : BaseTwoWayPaginationAdapter<AddContactModel>() {

    companion object {

        const val CONTACT_HOLDER = 0
    }

    private var onItemClickListener: OnItemClickListener<AddContactModel>? = null

    private var onPhotoClickListener: OnContactPhotoClickListener<AddContactModel>? = null

    private val styleParamsProvider = object : CanvasStylesProvider() {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<AddContactModel> {
        if (viewType == CONTACT_HOLDER) {
            val contactViewHolder = createContactViewHolder(parent)
            contactViewHolder.setOnContactClickListener(onItemClickListener)
            contactViewHolder.setOnContactPhotoClickListener(onPhotoClickListener)
            return contactViewHolder
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: AbstractViewHolder<AddContactModel?>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(getItem(position))
        if (holder.itemViewType == CONTACT_HOLDER) {
            (holder as AddContactViewHolder).showSeparator(position < mOffset + mContent.size - 1)
        }
    }

    override fun getItemType(dataModel: AddContactModel?): Int {
        return if (dataModel != null) {
            CONTACT_HOLDER
        } else {
            HOLDER_EMPTY
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        styleParamsProvider.isResourceCacheEnabled = true
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        styleParamsProvider.isResourceCacheEnabled = false
        styleParamsProvider.clearReferences()
    }

    fun setOnContactClickListener(listener: OnItemClickListener<AddContactModel>?) {
        onItemClickListener = listener
    }

    fun setOnContactPhotoClickListener(listener: OnContactPhotoClickListener<AddContactModel>?) {
        onPhotoClickListener = listener
    }

    private fun createContactViewHolder(parent: ViewGroup): AddContactViewHolder {
        return AddContactViewHolder(
            ContactView(parent.context, styleParamsProvider = styleParamsProvider.textStyleProvider)
        )
    }

    class AddContactViewHolder(
        private val contactView: ContactView
    ) : AbstractViewHolder<AddContactModel>(contactView) {

        private var contact: AddContactModel? = null

        private val searchColor: Int by lazy {
            ContextCompat.getColor(itemView.context, RDesign.color.text_search_highlight_color)
        }

        fun showSeparator(show: Boolean) {
            contactView.showSeparator(show)
        }

        override fun bind(contact: AddContactModel) {
            this.contact = contact
            super.bind(contact)
            val contactViewModel = ContactViewModel(
                photoData = createViewData(contact),
                title = contact.employee.name.formatName(PersonNameTemplate.SURNAME_NAME),
                subtitle = contact.employee.position.orEmpty(),
                subtitleSecond = contact.employee.companyOrDepartment,
                titleParamsConfigurator = {
                    highlights = getHighlights(contact.nameHighlight)
                },
                subtitleParamsConfigurator = {
                    maxLines = 2
                }
            )
            contactView.bindData(contactViewModel)
            contactView.personView.setHasActivityStatus(true)
        }

        fun setOnContactClickListener(listener: OnItemClickListener<AddContactModel>?) {
            itemView.setOnClickListener {
                contact?.let { listener?.onClickItem(it, bindingAdapterPosition) }
            }
        }

        fun setOnContactPhotoClickListener(listener: OnContactPhotoClickListener<AddContactModel>?) {
            contactView.personView.setOnClickListener {
                contact?.let { listener?.onContactPhotoClick(it) }
            }
            contactView.personView.setOnLongClickListener {
                contact?.let { listener?.onContactPhotoClick(it) }
                return@setOnLongClickListener true
            }
        }

        private fun getHighlights(highlights: List<Int>?): TextHighlights? {
            val highlightSpans = highlightSpansFrom(highlights) ?: return null
            return TextHighlights(highlightSpans, searchColor)
        }

        private fun createViewData(contact: AddContactModel): PersonData {
            return PersonData(contact.employee.uuid, contact.employee.photoUrl, contact.employee.initialsStubData)
        }
    }
}