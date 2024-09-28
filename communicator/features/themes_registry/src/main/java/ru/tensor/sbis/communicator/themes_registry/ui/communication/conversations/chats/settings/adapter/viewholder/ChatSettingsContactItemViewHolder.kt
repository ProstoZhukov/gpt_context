package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.base.ChatSettingsItemViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.text_span.TextViewWithPostfix
import ru.tensor.sbis.design.utils.preventViewFromDoubleClickWithDelay
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.api.menu.IconWithLabelItem
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeIcon
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeItemStyle
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import java.util.*

/**
 * Холдер контактов в настройках чата.
 *
 * @author dv.baranov
 */
internal class ChatSettingsContactItemViewHolder(
    parentView: ViewGroup,
) : ChatSettingsItemViewHolder<ChatSettingsContactItem>(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.communicator_item_list_chat_settings_contact, parentView, false),
) {

    private val title = itemView.findViewById<TextViewWithPostfix>(R.id.communicator_contact_title)
    private val subtitle = itemView.findViewById<SbisTextView>(R.id.communicator_contact_subtitle)
    private val subtitleSecondLine = itemView.findViewById<SbisTextView>(R.id.communicator_contact_subtitle_second_line)
    private val personPhotoView = itemView.findViewById<PersonView>(R.id.communicator_person_photo)
    private val contentContainer = itemView.findViewById<RelativeLayout>(R.id.communicator_content_container)
    val swipeableLayout: SwipeableLayout = itemView.findViewById(R.id.communicator_chat_settings_contact_swipe_layout)

    override fun bind() {
        with(item.participant) {
            val firstAndLastName = employeeProfile.name.lastName + " " + employeeProfile.name.firstName
            title.text = firstAndLastName
            subtitle.text = employeeProfile.companyOrDepartment

            personPhotoView.setSize(PhotoSize.M)
            personPhotoView.setData(createViewData(this))
            personPhotoView.setHasActivityStatus(true)

            preventViewFromDoubleClickWithDelay(DOUBLE_CLICK_DELAY) { item.onItemClick(employeeProfile.uuid) }.let {
                contentContainer.setOnClickListener(it)
                personPhotoView.setOnClickListener(it)
            }

            val items = LinkedList<SwipeMenuItem>()
            if (!swipeableLayout.isDragLocked) {
                IconWithLabelItem(
                    SwipeIcon(SbisMobileIcon.Icon.smi_SwipeUnTuning),
                    ru.tensor.sbis.swipeable_layout.R.string.design_swipe_menu_remove_admin,
                    SwipeItemStyle.GREY,
                ) { item.onRemoveAdminClick(this) }.let {
                    it.isLabelSingleLine = false
                    items.add(it)
                }
            }

            swipeableLayout.setMenu(items)
            swipeableLayout.invalidate()
        }
        hideSecondLine()
    }

    /**
     * Скрыть вторую линию дополнительной информации о контакте.
     */
    private fun hideSecondLine() {
        subtitleSecondLine.visibility = View.GONE
    }

    private fun createViewData(model: ThemeParticipant): PhotoData =
        PersonData(model.employeeProfile.uuid, model.employeeProfile.photoUrl, model.initialsStubData)
}

private const val DOUBLE_CLICK_DELAY = 1000L
