package ru.tensor.sbis.communicator.communicator_crm_chat_list.mapper

import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.HighlightsColorProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.applySearchSpan
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu.CRMChatSwipeMenuProvider
import ru.tensor.sbis.communicator.declaration.crm.CrmRateInfoData
import ru.tensor.sbis.communicator.declaration.crm.prepareClosedIcon
import ru.tensor.sbis.consultations.generated.ChannelIconType
import ru.tensor.sbis.consultations.generated.ConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationSearchResult
import ru.tensor.sbis.consultations.generated.ConsultationViewModel
import ru.tensor.sbis.consultations.generated.PersonPhotoData
import ru.tensor.sbis.consultations.generated.SearchResultPlace
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.binding.LayoutIdViewFactory
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.list.view.section.SectionOptions
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVm
import java.util.Date
import java.util.UUID
import ru.tensor.sbis.design.R as RDesign

/**
 * Реализация ItemMapper для crud3.
 *
 * @author da.zhukov.
 */
internal class CRMChatListMapper(
    private val context: SbisThemedContext,
    private val listDateFormatter: ListDateFormatter.DateTimeWithoutTodayStandard,
    private val highlightsColorProvider: HighlightsColorProvider,
    private val crmChatSwipeMenuProvider: CRMChatSwipeMenuProvider,
    private val isHistoryMode: Boolean = false,
    val clickAction: (ConsultationListElementModel) -> Unit = {}
) : ItemInSectionMapper<ConsultationListElementModel, AnyItem> {

    override fun map(
        item: ConsultationListElementModel,
        defaultClickAction: (ConsultationListElementModel) -> Unit
    ): AnyItem = if (item.value.isConsultationListSeparator()) {
        BindingItem(
            CRMChatListSeparatorModel(item.id, item.value.fieldConsultationListSeparator!!.text),
            DataBindingViewHolderHelper(
                factory = LayoutIdViewFactory(R.layout.communicator_crm_chat_separator)
            ),
            options = Options(
                clickAction = {
                    clickAction(item)
                },
                customBackground = true,
                customSidePadding = true
            )
        )
    } else {
        BindingItem(
            createCRMChatViewModelBindingModel(
                item.id,
                item.value.fieldConsultationViewModel,
                item.date
            ),
            DataBindingViewHolderHelper(
                factory = LayoutIdViewFactory(R.layout.communicator_crm_chat_item)
            ),
            options = Options(
                clickAction = {
                    defaultClickAction(item)
                },
                customBackground = true,
                customSidePadding = true
            )
        )
    }

    override fun mapSection(item: ConsultationListElementModel): SectionOptions {
        return ru.tensor.sbis.list.view.section.Options(
            hasDividers = false,
            hasTopMargin = false
        )
    }

    private fun createCRMChatViewModelBindingModel(
        id: UUID,
        consultationViewModel: ConsultationViewModel?,
        date: Date
    ): CRMChatViewModelBindingModel =
        if (consultationViewModel == null) stubCRMChatViewModelBindingModel()
        else {
            val rateData: CrmRateInfoData? = consultationViewModel.rate?.let { CrmRateInfoData(it.type, it.value.toInt()) }
            val (closedIcon, closedIconColor) = prepareClosedIcon(rateData, context)
            val closedIconSize = if (consultationViewModel.rate == null) {
                context.getDimenPx(RDesign.attr.iconSize_m)
            } else {
                context.getDimenPx(RDesign.attr.iconSize_2xl)
            }
            CRMChatViewModelBindingModel(
                id = id,
                sourceName = consultationViewModel.source?.name,
                sourceIcon = consultationViewModel.source?.icon?.icon(),
                isSabyGetIcon = consultationViewModel.source?.icon?.isSabyGetIcon() ?: false,
                name = consultationViewModel.name.applySpan(
                    consultationViewModel.searchResult,
                    SearchResultPlace.CONSULTATION_NAME
                ),
                text = consultationViewModel.relevantMessage?.text?.applySpan(
                    consultationViewModel.searchResult,
                    SearchResultPlace.CONSULTATION_MESSAGE
                ),
                authorName = consultationViewModel.authorName?.applySpan(
                    consultationViewModel.searchResult,
                    SearchResultPlace.AUTHOR
                ),
                authorPersonData = consultationViewModel.authorCollage?.let {
                    createPersonData(it)
                },
                subtitle = consultationViewModel.subTitle?.applySpan(
                    consultationViewModel.searchResult,
                    SearchResultPlace.SUB_TITLE
                ),
                messagePersonData = consultationViewModel.relevantMessage?.personCollage?.let {
                    createPersonData(it)
                },
                operatorPersonData = consultationViewModel.operatorCollage?.let {
                    createPersonData(it)
                },
                isClosed = consultationViewModel.isClosed,
                closedIcon = closedIcon,
                closedIconSize = closedIconSize,
                closedIconColor = closedIconColor,
                date = listDateFormatter.formatDate(date),
                unreadCounter = consultationViewModel.relevantMessage?.unreadCounter,
                needShowOperator = consultationViewModel.operatorCollage != null && !consultationViewModel.isClosed,
                isHistoryMode = isHistoryMode,
                isExpired = consultationViewModel.isExpired
            ).apply {
                swipeableVm = SwipeableVm(
                    uuid = consultationViewModel.id.toString(),
                    menu = crmChatSwipeMenuProvider.getSwipeMenu(consultationViewModel),
                    isDragLocked = isHistoryMode
                )
            }
        }

    private fun stubCRMChatViewModelBindingModel(): CRMChatViewModelBindingModel = CRMChatViewModelBindingModel(
        id = UUID.randomUUID(),
        sourceName = null,
        sourceIcon = null,
        name = StringUtils.EMPTY,
        text = null,
        authorName = null,
        authorPersonData = null,
        subtitle = null,
        messagePersonData = null,
        operatorPersonData = null,
        isClosed = false,
        closedIcon = StringUtils.EMPTY,
        closedIconSize = 0,
        closedIconColor = 0,
        date = null,
        unreadCounter = null,
        needShowOperator = false,
        isHistoryMode = false,
        isExpired = false
    )

    private fun createPersonData(personPhotoData: PersonPhotoData): PersonData {
        val initials = personPhotoData.initials
        val backgroundColorHex = personPhotoData.backgroundColorHex
        val initialsStubData = if (initials != null && backgroundColorHex != null) InitialsStubData(
            initials,
            backgroundColorHex
        ) else null

        return PersonData(null, personPhotoData.photoUrl, initialsStubData)
    }

    private fun String.applySpan(
        searchResults: ArrayList<ConsultationSearchResult>,
        place: SearchResultPlace
    ): CharSequence {
        if (searchResults.isEmpty()) {
            return this
        } else {
            val currentResult = searchResults.find { it.place == place } ?: return SpannableString(this)
            return this.applySearchSpan(currentResult.start, currentResult.end, highlightsColorProvider)
        }
    }

    private fun ChannelIconType.icon(): String {
        return when(this) {
            ChannelIconType.MOBILE_APP -> SbisMobileIcon.Icon.smi_SabyApp.character.toString()
            ChannelIconType.SITE -> SbisMobileIcon.Icon.smi_WWW.character.toString()
            ChannelIconType.SABY, ChannelIconType.SABYGET -> SbisMobileIcon.Icon.smi_sbisbird.character.toString()
            ChannelIconType.VK -> SbisMobileIcon.Icon.smi_VK2.character.toString()
            ChannelIconType.TELEGRAM -> SbisMobileIcon.Icon.smi_Telegram.character.toString()
            ChannelIconType.EMAIL -> SbisMobileIcon.Icon.smi_Email.character.toString()
            ChannelIconType.VIBER -> SbisMobileIcon.Icon.smi_Viber.character.toString()
            ChannelIconType.OK -> SbisMobileIcon.Icon.smi_odnoklassniki.character.toString()
            ChannelIconType.WHATSAPP -> SbisMobileIcon.Icon.smi_Whatsapp.character.toString()
            ChannelIconType.FACEBOOK -> SbisMobileIcon.Icon.smi_facebook.character.toString()
            ChannelIconType.YANDEX -> SbisMobileIcon.Icon.smi_Yandex.character.toString()
            ChannelIconType.INSTAGRAM -> SbisMobileIcon.Icon.smi_Instagram.character.toString()
            ChannelIconType.AVITO -> SbisMobileIcon.Icon.smi_Avito.character.toString()
            ChannelIconType.CHAT_WIDGET -> SbisMobileIcon.Icon.smi_ClientChat.character.toString()
            ChannelIconType.UNKNOWN -> ""
        }
    }

    private fun ChannelIconType.isSabyGetIcon(): Boolean = this == ChannelIconType.SABYGET
}
