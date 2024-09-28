package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.adapter.holder

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.core.views.contact_view.ContactView
import ru.tensor.sbis.communicator.core.views.contact_view.ContactViewModel
import ru.tensor.sbis.communicator.generated.ParticipantRole
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams.TextStyle
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParamsProvider
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.swipeablelayout.*
import ru.tensor.sbis.swipeablelayout.api.menu.IconWithLabelItem
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeIcon
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeItemStyle
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.util.SwipeMenuItemFactory
import java.lang.IllegalArgumentException
import java.util.*
import ru.tensor.sbis.communicator.core.R as RCommunicatorCore
import ru.tensor.sbis.swipeable_layout.R as RSwipeable

/**
 * ViewHolder участников диалога/чата.
 *
 * @param itemView - view item'а.
 * @param onItemClick - действие по нажатию на item.
 * @param onItemPhotoClick - действие по нажатию на фото item'а.
 * @param onChangeAdminStatusClick - действие по нажатию на кнопку изменения статуса администратора для участника чата.
 * @param onRemoveParticipantClick - действие по удалению участника из чата.
 * @param onStartConversationClick - действие по нажатию на кнопку сообщения.
 * @param onStartVideoCallClick - действие по нажатию на кнопку видеозвонка.
 * @param needShowContactIcon - необходимо ли показывать иконки нового сообщения и звонка.
 *
 * @author rv.krohalev
 */
internal class ThemeParticipantsViewHolder(
    itemView: View,
    private val onItemClick: (profileUuid: UUID) -> Unit,
    private val onItemPhotoClick: (profileUuid: UUID) -> Unit,
    private val onChangeAdminStatusClick: (chatParticipant: ThemeParticipantListItem.ThemeParticipant) -> Unit,
    private val onRemoveParticipantClick: (profileUuid: UUID, isByDismiss: Boolean) -> Unit,
    private val onStartConversationClick: (profileUuid: UUID) -> Unit,
    private val onStartVideoCallClick: (profileUuid: UUID) -> Unit,
    private val needShowContactIcon: Boolean,
) : AbstractViewHolder<ThemeParticipantListItem>(itemView) {

    /** Доступен ли сейчас видеозвонок - если false, то скрываем иконку звонка у item'а списка */
    private val isVideoCallAvailable: Boolean
        get() = themesRegistryDependency.callStateProviderFeature?.let { !it.isCallRunning() } ?: false

    /** UUID текущего пользователя приложения */
    var currentUserUuid: UUID? = null

    /** Доступно ли свайп-меню [SwipeableLayout] для конкретного item'а списка */
    var isSwipeEnabled: Boolean = false

    private val contactView: ContactView
        get() = itemView as? ContactView ?: itemView.findViewById(RCommunicatorCore.id.communicator_contact_view_id)

    /** @SelfDocumented */
    constructor(
        parent: ViewGroup,
        onItemClick: (profileUuid: UUID) -> Unit,
        onItemPhotoClick: (profileUuid: UUID) -> Unit,
        onChangeAdminStatusClicked: (chatParticipant: ThemeParticipantListItem.ThemeParticipant) -> Unit,
        onRemoveParticipantClicked: (profileUuid: UUID, isByDismiss: Boolean) -> Unit,
        isSwipePossible: Boolean,
        styleParamsProvider: StyleParamsProvider<TextStyle>? = null,
        onStartConversationClick: (profileUuid: UUID) -> Unit,
        onStartVideoCallClick: (profileUuid: UUID) -> Unit,
        needShowContactIcon: Boolean = false,
    ) : this(
        if (isSwipePossible) {
            SwipeableLayout(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundColor(parent.context.getThemeColorInt(com.google.android.material.R.attr.backgroundColor))
                ContactView(parent.context, styleParamsProvider = styleParamsProvider).also {
                    addView(it)
                }
            }
        } else {
            ContactView(parent.context, styleParamsProvider = styleParamsProvider)
        },
        onItemClick,
        onItemPhotoClick,
        onChangeAdminStatusClicked,
        onRemoveParticipantClicked,
        onStartConversationClick,
        onStartVideoCallClick,
        needShowContactIcon,
    )

    /** @SelfDocumented */
    override fun bind(model: ThemeParticipantListItem) {
        super.bind(model)
        with(model.castTo<ThemeParticipantListItem.ThemeParticipant>() ?: throw IllegalArgumentException()) {
            val contactViewModel = ContactViewModel(
                photoData = createViewData(this),
                title = "${employeeProfile.name.lastName} ${employeeProfile.name.firstName}".trim(),
                subtitle = employeeProfile.companyOrDepartment,
                roleIcon = getRoleIcon(role),
                needShowContactIcon = needShowContactIcon,
                needShowVideoCallIcon = isVideoCallAvailable,
            )

            contactView.apply {
                bindData(contactViewModel)

                setOnClickListener { onItemClick(employeeProfile.uuid) }
                personView.setOnClickListener { onItemPhotoClick(employeeProfile.uuid) }
                contactVideoCallIcon.setOnClickListener { _, _ -> onStartVideoCallClick(employeeProfile.uuid) }
                contactConversationIcon.setOnClickListener { _, _ -> onStartConversationClick(employeeProfile.uuid) }
                personView.setOnLongClickListener {
                    onItemPhotoClick(employeeProfile.uuid)
                    return@setOnLongClickListener true
                }
                personView.setHasActivityStatus(true)
            }

            val swipeableLayout: SwipeableLayout = itemView as? SwipeableLayout ?: return
            swipeableLayout.itemUuid = employeeProfile.uuid.toString()
            swipeableLayout.isDragLocked = !isSwipeEnabled

            val items = LinkedList<SwipeMenuItem>()
            if (!swipeableLayout.isDragLocked) {
                if (role == ParticipantRole.ADMIN) {
                    IconWithLabelItem(
                        SwipeIcon(SbisMobileIcon.Icon.smi_SwipeUnTuning),
                        RSwipeable.string.design_swipe_menu_remove_admin,
                        SwipeItemStyle.GREY,
                    ) { handleChangeAdminStatusClick(this) }.let {
                        it.isLabelSingleLine = false
                        items.add(it)
                    }
                } else {
                    IconWithLabelItem(
                        SwipeIcon(SbisMobileIcon.Icon.smi_SwipeTuning),
                        RSwipeable.string.design_swipe_menu_add_admin,
                        SwipeItemStyle.GREY,
                    ) { handleChangeAdminStatusClick(this) }.let {
                        it.isLabelSingleLine = false
                        items.add(it)
                    }
                }

                val removeButtonVisible = !UUIDUtils.equals(employeeProfile.uuid, currentUserUuid)
                if (removeButtonVisible) {
                    SwipeMenuItemFactory.createDeleteItem(RSwipeable.string.design_swipe_menu_label_remove) {
                        onRemoveParticipantClick(employeeProfile.uuid, false)
                        closeSwipeableLayout()
                    }.let(items::add)
                    @Suppress("DEPRECATION")
                    swipeableLayout.dismissListener = object : SwipeableLayout.DismissListener {
                        override fun onDismissed() {
                            onRemoveParticipantClick(employeeProfile.uuid, true)
                        }
                    }
                }

                swipeableLayout.setMenu(items)
                swipeableLayout.invalidate()
            }
        }
    }

    private fun createViewData(model: ThemeParticipantListItem.ThemeParticipant): PhotoData =
        PersonData(model.employeeProfile.uuid, model.employeeProfile.photoUrl, model.initialsStubData)

    private fun getRoleIcon(currentRole: ParticipantRole): String = when (currentRole) {
        ParticipantRole.ADMIN -> SbisMobileIcon.Icon.smi_CrownFill.character.toString()
        ParticipantRole.OTHER -> SbisMobileIcon.Icon.smi_Profile.character.toString()
        else -> ""
    }

    private fun handleChangeAdminStatusClick(participantModel: ThemeParticipantListItem.ThemeParticipant) {
        onChangeAdminStatusClick(participantModel)
        closeSwipeableLayout()
    }

    private fun closeSwipeableLayout() {
        val swipeableLayout: SwipeableLayout = itemView as? SwipeableLayout ?: return
        swipeableLayout.close(true)
    }

    /**
     * Отображать ли линию-разделитель под конкретным item'ом
     * @param show - true, если нужно показать
     */
    fun showSeparator(show: Boolean) {
        contactView.showSeparator(show)
    }
}