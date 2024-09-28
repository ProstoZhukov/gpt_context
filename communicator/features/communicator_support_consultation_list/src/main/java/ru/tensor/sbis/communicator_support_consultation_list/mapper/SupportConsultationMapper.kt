package ru.tensor.sbis.communicator_support_consultation_list.mapper

import android.content.Context
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.declaration.crm.CrmRateInfoData
import ru.tensor.sbis.communicator.declaration.crm.prepareClosedIcon
import ru.tensor.sbis.communicator_support_consultation_list.R
import ru.tensor.sbis.consultations.generated.ConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationViewModel
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.binding.LayoutIdViewFactory
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Options
import java.util.Date
import java.util.UUID

/**
 * Фабрика для SupportConsultationMapper
 */
@AssistedFactory
internal interface SupportConsultationMapperFactory {
    fun create(
        context: Context,
        listDateFormatter: ListDateFormatter,
        clickAction: (ConsultationListElementModel) -> Unit
    ): SupportConsultationMapper
}

/**
 * Реализация ItemMapper дял crud3
 */
internal class SupportConsultationMapper @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val listDateFormatter: ListDateFormatter,
    @Assisted val clickAction: (ConsultationListElementModel) -> Unit
) : ItemInSectionMapper<ConsultationListElementModel, AnyItem> {

    override fun map(
        item: ConsultationListElementModel,
        defaultClickAction: (ConsultationListElementModel) -> Unit
    ): AnyItem {

        return if (item.value.isConsultationListSeparator()) {
            BindingItem(
                ConsultationListSeparatorModel(item.id, item.value.fieldConsultationListSeparator!!.text),
                DataBindingViewHolderHelper(
                    factory = LayoutIdViewFactory(R.layout.communicator_support_consultation_separator)
                ),
                options = Options(
                    clickAction = {
                        Unit
                    },
                    customBackground = true,
                    customSidePadding = true
                )
            )

        } else {
            BindingItem(
                create(
                    item.id,
                    item.value.fieldConsultationViewModel,
                    item.date,
                    listDateFormatter
                ),
                DataBindingViewHolderHelper(
                    factory = LayoutIdViewFactory(R.layout.communicator_support_consultation_item)
                ),
                options = Options(
                    clickAction = {
                        clickAction(item)
                    },
                    customBackground = true
                )
            )
        }
    }

    private fun create(
        id: UUID,
        consultationViewModel: ConsultationViewModel?,
        date: Date,
        listDateFormatter: ListDateFormatter
    ): ConsultationViewModelBindingModel =
        if (consultationViewModel == null) {
            empty()
        } else {
            val rateData: CrmRateInfoData? = consultationViewModel.rate?.let { CrmRateInfoData(it.type, it.value.toInt()) }
            val (closedIcon, closedIconColor) = prepareClosedIcon(rateData, context)
            val closedIconSize = if (consultationViewModel.rate == null) {
                context.getDimenPx(ru.tensor.sbis.design.R.attr.iconSize_m)
            } else {
                context.getDimenPx(ru.tensor.sbis.design.R.attr.iconSize_2xl)
            }
            ConsultationViewModelBindingModel(
                id = id,
                name = consultationViewModel.name,
                text = consultationViewModel.relevantMessage?.text,
                personData = consultationViewModel.relevantMessage?.personCollage?.let {
                    val initials = it.initials
                    val backgroundColorHex = it.backgroundColorHex
                    val initialsStubData = if (initials != null && backgroundColorHex != null) InitialsStubData(
                        initials,
                        backgroundColorHex
                    ) else null
                    PersonData(null, it.photoUrl, initialsStubData)
                },
                isClosed = consultationViewModel.isClosed,
                closedIcon = closedIcon,
                closedIconSize = closedIconSize,
                closedIconColor = closedIconColor,
                date = listDateFormatter.formatDate(date),
                unreadCounter = consultationViewModel.relevantMessage?.unreadCounter
            )
        }

    private fun empty(): ConsultationViewModelBindingModel = ConsultationViewModelBindingModel(
        id = UUID.randomUUID(),
        name = StringUtils.EMPTY,
        text = null,
        personData = null,
        isClosed = false,
        date = null,
        unreadCounter = null,
        closedIcon = StringUtils.EMPTY,
        closedIconSize = 0,
        closedIconColor = 0,
    )
}