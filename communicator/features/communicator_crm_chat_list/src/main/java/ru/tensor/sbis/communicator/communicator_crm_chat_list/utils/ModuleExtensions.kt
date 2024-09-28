package ru.tensor.sbis.communicator.communicator_crm_chat_list.utils

import android.content.Context
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.communication_decl.crm.CrmChannelType
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.router.CRM_CONVERSATION_FRAGMENT_TAG
import ru.tensor.sbis.consultations.generated.ChannelGroupType
import ru.tensor.sbis.consultations.generated.ChannelHeirarchyItemType
import ru.tensor.sbis.consultations.generated.ChannelIconType
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.utils.extentions.getColorFrom
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment
import ru.tensor.sbis.design.R as RDesign

internal fun <T> CoroutineScope.launchAndCollect(
    flow: Flow<T>,
    collector: (T) -> Unit
) = launch { flow.collect(collector) }

internal fun ChannelIconType.icon(context: Context): Pair<String, Int> {
    fun color() = context.getColorFromAttr(RDesign.attr.secondaryIconColor)
    fun sabygetColor() = context.getColorFrom(RDesign.color.palette_color_red2)
    return when (this) {
        ChannelIconType.MOBILE_APP -> SbisMobileIcon.Icon.smi_PhoneCell1.character.toString() to color()
        ChannelIconType.SITE -> SbisMobileIcon.Icon.smi_WWW.character.toString() to color()
        ChannelIconType.SABY -> SbisMobileIcon.Icon.smi_sbisbird.character.toString() to color()
        ChannelIconType.SABYGET -> SbisMobileIcon.Icon.smi_sbisbird.character.toString() to sabygetColor()
        ChannelIconType.VK -> SbisMobileIcon.Icon.smi_VK2.character.toString() to color()
        ChannelIconType.TELEGRAM -> SbisMobileIcon.Icon.smi_Telegram.character.toString() to color()
        ChannelIconType.EMAIL -> SbisMobileIcon.Icon.smi_Email.character.toString() to color()
        ChannelIconType.VIBER -> SbisMobileIcon.Icon.smi_Viber.character.toString() to color()
        ChannelIconType.OK -> SbisMobileIcon.Icon.smi_odnoklassniki.character.toString() to color()
        ChannelIconType.WHATSAPP -> SbisMobileIcon.Icon.smi_Whatsapp.character.toString() to color()
        ChannelIconType.FACEBOOK -> SbisMobileIcon.Icon.smi_facebook.character.toString() to color()
        ChannelIconType.YANDEX -> SbisMobileIcon.Icon.smi_Yandex.character.toString() to color()
        ChannelIconType.INSTAGRAM -> SbisMobileIcon.Icon.smi_Instagram.character.toString() to color()
        ChannelIconType.AVITO -> SbisMobileIcon.Icon.smi_Avito.character.toString() to color()
        ChannelIconType.CHAT_WIDGET -> SbisMobileIcon.Icon.smi_ClientChat.character.toString() to color()
        ChannelIconType.UNKNOWN -> "" to color()
    }
}

internal fun ChannelHeirarchyItemType.toCrmChannelType(): CrmChannelType =
    when (this) {
        ChannelHeirarchyItemType.CHANNEL_FOLDER -> CrmChannelType.CHANNEL_FOLDER
        ChannelHeirarchyItemType.CHANNEL -> CrmChannelType.CHANNEL
        ChannelHeirarchyItemType.OPEN_LINE -> CrmChannelType.OPEN_LINE
        ChannelHeirarchyItemType.CONTACT -> CrmChannelType.CONTACT
        ChannelHeirarchyItemType.CHANNEL_FOLDER_GROUP -> CrmChannelType.CHANNEL_FOLDER_GROUP
        ChannelHeirarchyItemType.CHANNEL_GROUP_TYPE -> CrmChannelType.CHANNEL_GROUP_TYPE
    }

internal fun String.applySearchSpan(start: Int, end: Int, highlightsColorProvider: HighlightsColorProvider): CharSequence =
    SpannableString(this).apply {
        if (start in 0 until end && isNotEmpty() && end <= length - 1) {
            setSpan(
                BackgroundColorSpan(highlightsColorProvider.getHighlightsColor()),
                start,
                end,
                0
            )
        }
    }

internal fun ChannelGroupType.toChannelName(context: Context): String {
    fun getString(@StringRes stringId: Int): String =
        context.getString(stringId)

    return when (this) {
        ChannelGroupType.SUPPORT -> getString(R.string.communicator_crm_chat_list_channel_group_title_support)

        ChannelGroupType.SITE,
        ChannelGroupType.CHAT_WIDGET -> getString(R.string.communicator_crm_chat_list_channel_group_title_site)

        ChannelGroupType.SABYGET -> getString(R.string.communicator_crm_chat_list_channel_group_title_sabyget)

        ChannelGroupType.SABY -> getString(R.string.communicator_crm_chat_list_channel_group_title_saby)

        ChannelGroupType.MOBILE_APP -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_app)

        ChannelGroupType.VK -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_vk)

        ChannelGroupType.TELEGRAM -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_telegram)

        ChannelGroupType.EMAIL -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_email)

        ChannelGroupType.VIBER -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_viber)

        ChannelGroupType.OK -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_ok)

        ChannelGroupType.WHATSAPP -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_whatsapp)

        ChannelGroupType.FACEBOOK -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_facebook)

        ChannelGroupType.YANDEX -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_yandex)

        ChannelGroupType.INSTAGRAM -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_instagram)

        ChannelGroupType.AVITO -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_avito)

        ChannelGroupType.UNKNOWN -> getString(R.string.communicator_crm_chat_list_channel_group_title_mobile_unknown)
    }
}

/**
 * Удалить фрагмент/фрагменты переписки из backstack, с учетом логики открытия переписок через шторку истории в crm.
 * (Мы должны вернуться к первой открытой переписке, если открывали другие через шторку).
 */
internal fun FragmentManager.popCrmConversationFragmentFromBackStack() {
    for (i in 1 until backStackEntryCount) {
        if (getBackStackEntryAt(i).name == CRM_CONVERSATION_FRAGMENT_TAG) {
            if (i + 1 >= backStackEntryCount) {
                popBackStackImmediate()
            } else {
                popBackStackImmediate(
                    getBackStackEntryAt(i + 1).id,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
            break
        }
    }
}

/** Получить fragmentManager, который используется фрагментом для размещения других фрагментов. */
internal fun Fragment.getCurrentFragmentManager(): FragmentManager =
    if (isTablet) childFragmentManager else activity?.supportFragmentManager ?: childFragmentManager

internal fun FragmentManager.getLastOpenedFragment(): Fragment? =
    fragments.lastOrNull() ?: fragments.find { it.isVisible }

internal fun ContainerMovableDialogFragment.handleBackPress(): Boolean {
    val child = childFragmentManager.fragments.lastOrNull()
    val isHandled = child?.castTo<FragmentBackPress>()?.onBackPressed() == true
    if (!isHandled) dismiss()
    return true
}

