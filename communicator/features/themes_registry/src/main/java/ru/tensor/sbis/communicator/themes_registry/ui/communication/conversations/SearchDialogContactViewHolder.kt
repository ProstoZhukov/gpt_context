package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations

import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.base_components.adapter.OnItemClickListener
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.communicator.core.views.contact_view.ContactView
import ru.tensor.sbis.base_components.adapter.contacts.holder.OnContactPhotoClickListener
import ru.tensor.sbis.communicator.core.views.contact_view.ContactViewModel
import ru.tensor.sbis.communicator.core.views.contact_view.highlightSpansFrom
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as RDesign

/**
 * ViewHolder контакта при поиске в реестре диалогов.
 *
 * @author da.zhukov
 */
internal class SearchDialogContactViewHolder<T : ContactVM>(
    private val contactView: ContactView
) : AbstractViewHolder<T>(contactView) {
    private var contact: T? = null
    private val searchColor: Int by lazy {
        itemView.context.getThemeColorInt(RDesign.attr.textBackgroundColorDecoratorHighlight)
    }

    /**
     * Установить видимость разделителя.
     */
    fun showSeparator(show: Boolean) {
        contactView.showSeparator(show)
    }

    /** @SelfDocumented */
    fun setOnContactClickListener(listener: OnItemClickListener<T>?) {
        itemView.setOnClickListener {
            contact?.let { listener?.onClickItem(it, bindingAdapterPosition) }
        }
    }

    /** @SelfDocumented */
    fun setOnContactPhotoClickListener(listener: OnContactPhotoClickListener<T>?) {
        contactView.personView.setOnClickListener {
            contact?.let { listener?.onContactPhotoClick(it) }
        }
        contactView.personView.setOnLongClickListener {
            contact?.let { listener?.onContactPhotoClick(it) }
            return@setOnLongClickListener true
        }
    }

    override fun bind(contact: T) {
        this.contact = contact
        super.bind(contact)
        val contactViewModel = ContactViewModel(
            photoData = PersonData(contact.uuid, contact.rawPhoto, contact.initialsStubData),
            title = contact.renderedName.orEmpty(),
            subtitle = contact.data1.orEmpty(),
            subtitleSecond = contact.data2.orEmpty(),
            titleParamsConfigurator = {
                highlights = getHighlights(contact.nameHighlight)
            }
        )
        contactView.bindData(contactViewModel)
        contactView.personView.setHasActivityStatus(true)
    }

    private fun getHighlights(highlights: List<Int>?): TextHighlights? {
        val highlightSpans = highlightSpansFrom(highlights) ?: return null
        return TextHighlights(highlightSpans, searchColor)
    }
}
