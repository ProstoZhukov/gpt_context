package ru.tensor.sbis.business_card_list.presentation.holder

import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractListAdapter
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.person_decl.profile.model.ProfileContact

/**@SelfDocumented*/
internal class ContactsAdapter :
    AbstractListAdapter<ProfileContact, AbstractViewHolder<ProfileContact>>() {

    /**@SelfDocumented*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<ProfileContact> =
        ContactItemHolder(parent, content.lastIndex)

    /**@SelfDocumented*/
    override fun onBindViewHolder(holder: AbstractViewHolder<ProfileContact>, position: Int) {
        holder.bind(content[position])
    }
}