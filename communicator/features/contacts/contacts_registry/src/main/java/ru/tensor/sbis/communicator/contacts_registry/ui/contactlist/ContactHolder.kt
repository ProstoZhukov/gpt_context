package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist

import androidx.core.content.ContextCompat
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListAdapter.ContactListActionsListener
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsModel
import ru.tensor.sbis.communicator.core.views.contact_view.ContactViewModel
import ru.tensor.sbis.communicator.core.views.contact_view.highlightSpansFrom
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.utils.preventViewFromDoubleClickWithDelay
import ru.tensor.sbis.persons.util.formatName
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.api.Dismissed
import ru.tensor.sbis.swipeablelayout.api.DismissedWithoutMessage
import ru.tensor.sbis.swipeablelayout.api.SwipeItemDismissType
import ru.tensor.sbis.swipeablelayout.api.menu.IconWithLabelItem
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeIcon
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeItemStyle
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.util.SwipeMenuItemFactory
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.swipeable_layout.R as RSwipeable

/**
 * ViewHolder контакта для реестра контактов.
 *
 * @author vv.chekurda
 */
internal class ContactHolder constructor(
    private val view: ContactRegistryItemView,
    swipeMenuViewPool: SwipeMenuViewPool,
    private val actionsListener: ContactListActionsListener,
    contactItemsClickHandler: ContactItemsClickHandler
) : AbstractViewHolder<ContactsModel?>(view) {

    private var model: ContactsModel? = null

    val swipeableLayout: SwipeableLayout = view.swipeableLayout

    private val searchColor: Int by lazy {
        ContextCompat.getColor(itemView.context, R.color.text_search_highlight_color)
    }

    private val contractSubtitleColor = view.context.getThemeColorInt(R.attr.labelContrastTextColor)
    private val subtitleColor = view.context.getThemeColorInt(R.attr.readonlyTextColor)
    private val subtitleTextSize = view.context.getDimen(R.attr.fontSize_m_scaleOn)

    init {
        swipeableLayout.setMenuItemViewPool(swipeMenuViewPool)
        val profileClickListener = preventViewFromDoubleClickWithDelay(DOUBLE_CLICK_DELAY) {
            contactItemsClickHandler.onContactPhotoClicked(model!!)
        }
        view.contactItemContainer.setOnClickListener {
            if (view.isCheckModeEnabled) {
                contactItemsClickHandler.onContactItemClicked(model!!)
            } else {
                profileClickListener.onClick(view)
            }
        }
        view.contactItemContainer.setOnLongClickListener {
            contactItemsClickHandler.onContactItemLongClicked(model!!)
            true
        }
        view.personView.setOnClickListener(profileClickListener)
    }

    override fun updateCheckState(checked: Boolean, animate: Boolean) {
        view.updateCheckState(checked)
    }

    override fun updateSelectionState(selected: Boolean) {
        itemView.isSelected = selected
    }

    fun setCheckModeEnabled(enabled: Boolean) {
        view.isCheckModeEnabled = enabled
    }

    fun configureSeparator(visible: Boolean) {
        view.isTopSeparatorVisible = visible
    }

    fun configureSegmentDivider(segmentDividerVisibility: Boolean) {
        view.changeFindMoreVisibility(segmentDividerVisibility)
    }

    override fun bind(contact: ContactsModel?) {
        super.bind(contact!!)
        model = contact
        view.let {
            val isCheckModeEnabled = it.isCheckModeEnabled
                    && !model!!.contact.isMyAccountManager
                    && !model!!.contact.changesIsForbidden

            it.bindData(contact.viewModel)
            it.updateCheckState(isCheckModeEnabled)
            it.setFormattedDateTime(contact.formatterDateTime)
            it.showSeparatorTopMargin(!contact.formatterDateTime?.date.isNullOrEmpty())
            view.setHasActivityStatus()
        }

        swipeableLayout.let { layout ->
            if (view.isCheckModeEnabled && (swipeableLayout.isMenuOpening() || swipeableLayout.isMenuOpen())) {
                layout.close()
            }
            layout.isDragLocked = view.isCheckModeEnabled
            layout.setMenu(createMenuItems())

            layout.itemDismissType = SwipeItemDismissType.CANCELLABLE
            layout.itemUuid = contact.contact.uuid.toString()
            layout.addSwipeEventListener<Dismissed> { actionsListener.onDismissed(UUIDUtils.fromString(it.uuid)) }
            layout.addSwipeEventListener<DismissedWithoutMessage> { actionsListener.onDismissedWithoutMessage(it.uuid) }
        }
    }

    private val ContactsModel.viewModel: ContactViewModel
        get() {
            val isContactWithComment = !contact.comment.isNullOrEmpty()
            val contactName = contact.name.formatName(PersonNameTemplate.SURNAME_NAME)

            val title: String
            val subtitle: String
            val subtitleSecond: String

            val nameHighlights = highlightSpansFrom(contact.nameHighlight)?.let {
                TextHighlights(it, searchColor)
            }
            val commentHighlights = highlightSpansFrom(emptyList())?.let {
                TextHighlights(it, searchColor)
            }
            val titleHighlights: TextHighlights?
            val subtitleHighlights: TextHighlights?

            if (isContactWithComment) {
                title = contact.comment.orEmpty()
                subtitle = contactName
                subtitleSecond = contact.position ?: contact.companyOrDepartment
                titleHighlights = nameHighlights
                subtitleHighlights = commentHighlights
            } else {
                title = contactName
                subtitle = contact.position.orEmpty()
                subtitleSecond = contact.companyOrDepartment
                titleHighlights = nameHighlights
                subtitleHighlights = commentHighlights
            }

            return ContactViewModel(
                photoData = PersonData(contact.uuid, contact.photoUrl, contact.person.initialsStubData),
                title = title,
                subtitle = subtitle,
                subtitleSecond = subtitleSecond,
                titleParamsConfigurator = {
                    highlights = titleHighlights
                },
                subtitleParamsConfigurator = {
                    paint.color = if (isContactWithComment) contractSubtitleColor else subtitleColor
                    paint.textSize = subtitleTextSize
                    highlights = subtitleHighlights
                }
            )
        }

    private fun createMenuItems(): List<SwipeMenuItem> {
        val res = swipeableLayout.context.resources
        val items = mutableListOf<SwipeMenuItem>()

        IconWithLabelItem(
            SwipeIcon(SbisMobileIcon.Icon.smi_SwipeMessage),
            res.getString(RSwipeable.string.design_swipe_menu_label_message),
            SwipeItemStyle.BLUE
        ) { actionsListener.onSwipeSendMessageClicked(model!!) }
            .let {
                it.isClickPostponedUntilMenuClosed = true
                items.add(it)
            }

        val changesIsAllowed = model!!.contact.isInMyContacts && !model!!.contact.changesIsForbidden
        swipeableLayout.itemDismissType = SwipeItemDismissType.DISMISS_IMMEDIATE

        if (changesIsAllowed) {
            IconWithLabelItem(
                SwipeIcon(SbisMobileIcon.Icon.smi_moveToFolder),
                res.getString(RCommunicatorDesign.string.communicator_check_panel_move),
                SwipeItemStyle.ORANGE
            ) { actionsListener.onSwipeMoveToFolderClicked(model!!) }
                .let {
                    it.isClickPostponedUntilMenuClosed = true
                    items.add(it)
                }

            if (!model!!.contact.isMyAccountManager) {
                SwipeMenuItemFactory.createDeleteItem(RCommunicatorDesign.string.communicator_check_panel_delete) {
                    actionsListener.onSwipeRemoveClicked(model!!)
                }.let { items.add(it) }
            }
        }

        return items
    }

    interface ContactItemsClickHandler {
        fun onContactItemClicked(model: ContactsModel)
        fun onContactPhotoClicked(model: ContactsModel)
        fun onContactItemLongClicked(model: ContactsModel)
    }

    companion object {
        private const val DOUBLE_CLICK_DELAY: Long = 1000
    }
}