package ru.tensor.sbis.business_card_list.presentation.holder

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.business_card_list.databinding.BusinessCardContactItemViewBinding
import ru.tensor.sbis.design.text_span.text.masked.formatter.phone.formatPhone
import ru.tensor.sbis.employee_common.utils.iconRes
import ru.tensor.sbis.person_decl.profile.model.ProfileContact
import ru.tensor.sbis.person_decl.profile.model.ProfileContactType.MOBILE_PHONE

/**@SelfDocumented*/
internal class ContactItemHolder private constructor(
    binding: BusinessCardContactItemViewBinding,
    private val listLastIndex: Int
) :
    AbstractViewHolder<ProfileContact>(binding.root) {

    constructor(parent: ViewGroup, listLastIndex: Int) : this(
        BusinessCardContactItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        listLastIndex
    )

    private val iconView = binding.businessCardContactItemIcon
    private val valueView = binding.businessCardContactItemValue

    /**@SelfDocumented*/
    override fun bind(model: ProfileContact) {
        super.bind(model)
        model.type.iconRes?.let {
            iconView.text = itemView.resources.getString(it)
        }

        valueView.text = if (model.type == MOBILE_PHONE) {
            formatPhone(model.info)
        } else {
            model.info
        }

        (itemView as LinearLayoutCompat).gravity =
            if (listLastIndex == absoluteAdapterPosition && listLastIndex % 2 == 0) Gravity.CENTER_HORIZONTAL else Gravity.LEFT
    }
}