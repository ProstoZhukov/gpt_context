package ru.tensor.sbis.communicator_support_channel_list.mapper

import ru.tensor.sbis.communicator.design.icon
import ru.tensor.sbis.consultations.generated.SupportChatsViewModel
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.profile_decl.person.CompanyData
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData

internal data class SupportChatsViewDataBindingModel(
    val title: String?,
    val date: String?,
    val relevantMessageText: String?,
    val relevantMessageTitle:String?,
    val relevantMessagePhotoIcon: String?,
    val isClosed: Boolean,
    val unreadCount: Int?,
    val personData: PersonData?,
    val sabygetPhotoData: CompanyData?
) {

    companion object {
        fun create(
            listDateFormatter: ListDateFormatter,
            supportChatsViewModel: SupportChatsViewModel
        ): SupportChatsViewDataBindingModel =
            SupportChatsViewDataBindingModel(
                title = supportChatsViewModel.title,
                date = supportChatsViewModel.relevantConsultation?.date?.let { listDateFormatter.formatDate(it) },
                relevantMessageText = supportChatsViewModel.relevantConsultation?.relevantMessage?.text,
                relevantMessageTitle = supportChatsViewModel.relevantConsultation?.title,
                relevantMessagePhotoIcon = supportChatsViewModel.icon.iconName?.icon?.character?.toString() ?: "",
                isClosed = supportChatsViewModel.relevantConsultation?.isClosed ?: false,
                unreadCount = supportChatsViewModel.relevantConsultation?.relevantMessage?.unreadCounter,
                personData = supportChatsViewModel.relevantConsultation?.relevantMessage?.personCollage?.let {
                    val initials = it.initials
                    val backgroundColorHex = it.backgroundColorHex
                    val initialsStubData = if (initials != null && backgroundColorHex != null) InitialsStubData(
                        initials,
                        backgroundColorHex
                    ) else null
                    PersonData(null, it.photoUrl, initialsStubData)
                },
                sabygetPhotoData = if (supportChatsViewModel.icon.url.isNullOrEmpty() || supportChatsViewModel.icon.url == "null") null else CompanyData(null, supportChatsViewModel.icon.url)
            )
    }
}