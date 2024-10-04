package ru.tensor.sbis.design.selection.ui.list.items.multi.share.dialog

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.BASELINE
import androidx.constraintlayout.widget.ConstraintSet.TOP
import androidx.core.view.isVisible
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.databinding.SelectionListDialogItemBinding
import ru.tensor.sbis.design.selection.ui.list.items.single.DefaultSingleSelectorViewHolder
import ru.tensor.sbis.design.selection.ui.model.share.dialog.DialogSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogMessageSyncStatus
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogRelevantMessageType.*
import ru.tensor.sbis.edo_decl.document.DocumentType.DISC_FOLDER
import java.util.*
import kotlin.math.min
import ru.tensor.sbis.design.R as RDesign

/**
 * Реализация [DefaultSingleSelectorViewHolder] для отображения диалогов в селекторе.
 * Упрощенная реализация оригинального [ru.tensor.sbis.communicator.ui.communication.conversations.dialogs.dialoglist.DialogViewHolder].
 * Общие визуальные изменения и логику отображения views в холдере диалога необходимо поддерживать в обеих реализациях.
 *
 * @author vv.chekurda
 */
internal class DialogMultiSelectorItemViewHolder(
    view: View,
    private val colorsProvider: DialogColorsProvider
) : DefaultSingleSelectorViewHolder<DialogSelectorItemModel>(view) {

    private val binding = SelectionListDialogItemBinding.bind(view)

    private val constraintSet = ConstraintSet()

    override fun bind(data: DialogSelectorItemModel) {
        super.bind(data)
        bindTitle()
        bindCollageView()
        bindRelevantMessage()
    }

    private fun bindTitle() {
        bindDialogName()
        bindPersonCompanyIcon()
        bindTimeHeader()
        bindUnreadIcon()
    }

    private fun bindRelevantMessage() {
        bindMessage()
        bindServiceMessage()
        bindDocument()
        bindAttachment()
        binding.subtitle.apply {
            if (text.isNullOrEmpty() && visibility != View.GONE) {
                visibility = View.GONE
            }
        }
        setUnreadCountBaseline()
    }

    private fun setUnreadCountBaseline() {
        constraintSet.let {
            it.clone(binding.selectionMessageInfoContainer)
            val isDocumentContainerVisible = binding.selectionDocumentContainer.isVisible
            val isAttachmentPreviewsVisible = binding.selectionAttachmentPreviews.isVisible
            if (!isDocumentContainerVisible || (isDocumentContainerVisible && isAttachmentPreviewsVisible)) {
                it.clear(R.id.selection_unread_message_count, TOP)
                it.clear(R.id.selection_document_container, ConstraintSet.END)
                it.connect(
                    R.id.selection_unread_message_count,
                    TOP,
                    R.id.selection_document_container,
                    ConstraintSet.BOTTOM
                )
                it.connect(
                    R.id.selection_document_container,
                    ConstraintSet.END,
                    R.id.selection_message_info_container,
                    ConstraintSet.END
                )
            } else {
                it.clear(R.id.selection_unread_message_count, TOP)
                it.connect(
                    R.id.selection_unread_message_count,
                    BASELINE,
                    R.id.selection_master_message_container,
                    BASELINE
                )
            }
            it.applyTo(binding.selectionMessageInfoContainer)
        }
    }

    private fun bindDialogName() {
        with(binding.selectionPersonInfoContainer.title) {
            val recipientsCount: Int = data.participantsCount
            val personName: Spannable = SpannableString(data.title)
            personName.setSpan(
                TextAppearanceSpan(context, RDesign.style.MediumStyle),
                0,
                data.title.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (data.isOutgoing && recipientsCount > 1) {
                setTextWithHighlight(personName, data.nameHighlights, "(+" + (recipientsCount - 1) + ")")
            } else if (data.isSocnetEvent && data.participantsCount > 1) {
                setTextWithHighlight(personName, data.nameHighlights, "(+" + (data.participantsCount - 1) + ")")
            } else {
                setTextWithHighlight(personName, data.nameHighlights)
            }
        }
    }

    private fun bindPersonCompanyIcon() {
        binding.selectionPersonInfoContainer.selectionPersonCompanyIcon.visibility =
            if (!data.messagePersonCompany.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    private fun bindTimeHeader() {
        binding.selectionPersonInfoContainer.selectionDialogItemDateHeader.run {
            visibility = View.VISIBLE
            updateHeader(Date(data.timestamp))
            highlightTime(false)
        }
    }

    private fun bindMessage() {
        binding.selectionMessageIAmAuthor.visibility = if (data.isOutgoing) View.VISIBLE else View.GONE
        binding.subtitle.run {
            val messageText: Spannable = data.messageText
            if (TextUtils.isEmpty(messageText)) {
                text = null
                visibility = View.GONE
            } else {
                if (!data.isSocnetEvent) {
                    setTextWithHighlight(messageText, data.searchHighlights)
                }
                visibility = View.VISIBLE
            }
        }
    }

    private fun bindServiceMessage() {
        val serviceText: Spannable? = data.serviceText
        val socnetLinesCount = if (data.isSocnetEvent) {
            setSocnetEventText(data.messageText)
        } else {
            0
        }

        binding.selectionServiceType.run {
            visibility = if (!data.isSocnetEvent &&
                serviceText.isNullOrEmpty() ||
                data.isSocnetEvent &&
                socnetLinesCount < 2
            ) {
                View.GONE
            } else {
                View.VISIBLE
            }
            if (!data.isSocnetEvent) text = serviceText
        }
        setTextMaxLines(socnetLinesCount)
    }

    private fun setTextMaxLines(socnetLinesCount: Int) {
        val messageTextInSingleLine = data.let {
            socnetLinesCount >= 2 ||
                !it.externalEntityTitle.isNullOrEmpty() ||
                it.messageText.isNotEmpty() &&
                !it.serviceText.isNullOrEmpty() ||
                it.attachmentCount != 0 ||
                it.unreadCount > 1
        }
        binding.run {
            subtitle.maxLines = if (messageTextInSingleLine) 1 else DEFAULT_TEXT_MAX_LINES
            selectionServiceType.maxLines = if (messageTextInSingleLine) 1 else DEFAULT_TEXT_MAX_LINES
            selectionSocnetThirdLine.visibility = if (socnetLinesCount == 3) View.VISIBLE else View.GONE
        }
    }

    private fun bindDocument() {
        with(binding) {
            val dialogTitle = data.dialogTitle
            val dialogTitleExists = !dialogTitle.isNullOrEmpty()
            val externalEntityTitle: CharSequence? = data.externalEntityTitle
            val externalEntityExists = !externalEntityTitle.isNullOrEmpty()
            if (!data.isSocnetEvent && (externalEntityExists || dialogTitleExists)) {
                selectionDocumentName.let {
                    if (dialogTitleExists) {
                        it.setTextColor(colorsProvider.dialogTitle)
                        it.setTextWithHighlight(dialogTitle, data.docsHighlights)
                    } else {
                        it.setTextWithHighlight(externalEntityTitle, data.docsHighlights)
                    }
                    it.visibility = View.VISIBLE
                }
                selectionDocumentIcon.let {
                    it.text = it.resources.getString(
                        when {
                            data.isPrivateChat -> RDesign.string.design_mobile_icon_menu_messages
                            data.isChatForOperations -> RDesign.string.design_mobile_icon_message_contour
                            data.documentType == DISC_FOLDER -> RDesign.string.design_mobile_icon_folder_solid
                            else -> RDesign.string.design_mobile_icon_document
                        }
                    )
                    // высота иконок для чатов меньше заглавной буквы сообщения, поэтому для них увеличиваем размер (решение проектирования)
                    val iconSizeRes = if (data.isPrivateChat || data.isChatForOperations) {
                        RDesign.dimen.size_body1_scaleOff
                    } else {
                        RDesign.dimen.size_caption1_scaleOff
                    }
                    it.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.resources.getDimension(iconSizeRes))
                    if (data.documentUuid != null) {
                        it.visibility = View.VISIBLE
                    }
                }

            } else if (dialogTitleExists) {
                selectionDocumentName.let {
                    it.setTextColor(colorsProvider.dialogTitle)
                    it.setTextWithHighlight(dialogTitle, data.docsHighlights)
                    it.visibility = View.VISIBLE
                }
            } else {
                selectionDocumentName.visibility = View.GONE
                selectionDocumentIcon.visibility = View.GONE
            }
        }
    }

    private fun bindCollageView() {
        binding.selectionPersonInfoContainer.selectionPhotoCollageView.setDataList(data.participantsCollage)
    }

    private fun bindAttachment() {
        binding.selectionAttachmentPreviews.run {
            visibility = if (data.attachmentCount > 0) {
                submitList(data.attachments)
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun bindUnreadIcon() {
        if (data.isRead && data.isReadByMe) {
            setReadState()
        } else {
            setUnreadState()
        }
    }

    private fun setUnreadState() {
        var showUnreadView = true
        @DimenRes var iconSize = RDesign.dimen.size_body2_scaleOff
        @StringRes var iconStringRes = -1
        @ColorInt val unreadViewColor: Int

        when (data.messageType) {
            DRAFT -> {
                iconStringRes = RDesign.string.design_mobile_icon_edited
                unreadViewColor = colorsProvider.incomingUnreadIconColor
            }
            SENDING -> {
                iconStringRes = RDesign.string.design_mobile_icon_clock
                iconSize = RDesign.dimen.size_caption2_scaleOff
                unreadViewColor = colorsProvider.outgoingIconColor
            }
            MESSAGE -> {
                if (data.syncStatus == SelectionDialogMessageSyncStatus.ERROR) {
                    iconStringRes = RDesign.string.design_mobile_icon_alert_null
                    iconSize = RDesign.dimen.size_caption1_scaleOff
                    unreadViewColor = colorsProvider.errorIconColor
                } else {
                    if (data.isOutgoing && !data.isForMe && data.isReadByMe) {
                        iconStringRes = RDesign.string.design_mobile_icon_message_was_read_lower
                        unreadViewColor = colorsProvider.outgoingIconColor
                    } else {
                        unreadViewColor = colorsProvider.incomingUnreadIconColor
                        showUnreadView = false
                    }
                }
            }
            else ->
                if (data.isOutgoing && !data.isForMe && data.isReadByMe) {
                    iconStringRes = RDesign.string.design_mobile_icon_message_was_read_lower
                    unreadViewColor = colorsProvider.outgoingIconColor
                } else {
                    unreadViewColor = colorsProvider.incomingUnreadIconColor
                    showUnreadView = false
                }
        }
        setUnreadIcon(showUnreadView, iconStringRes, iconSize, unreadViewColor)
        bindUnreadViews()
    }

    private fun bindUnreadViews() {
        val messageCountVisible: Boolean
        if (data.isOutgoing && !data.isForMe) {
            messageCountVisible = false
        } else {
            messageCountVisible = if (data.unreadCount >= 1) {
                binding.selectionUnreadMessageCount.counter = data.unreadCount
                true
            } else {
                false
            }
        }
        binding.run {
            selectionUnreadMessageCount.visibility = if (messageCountVisible) View.VISIBLE else View.GONE
        }
    }

    private fun setUnreadIcon(
        isVisible: Boolean,
        @StringRes iconRes: Int,
        @DimenRes iconSize: Int,
        @ColorInt viewColor: Int
    ) {
        binding.selectionPersonInfoContainer.selectionUnreadIcon.run {
            if (isVisible) {
                text = resources.getString(iconRes)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(iconSize))
                setTextColor(viewColor)
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }
    }

    private fun setReadState() {
        binding.run {
            selectionPersonInfoContainer.selectionUnreadIcon.visibility = View.GONE
            selectionUnreadMessageCount.visibility = View.GONE
        }
    }

    /**
     * Установка сервисного сообщения соц. сети
     * @return Количество строк в итоге
     */
    private fun setSocnetEventText(messageText: CharSequence): Int {
        val lines: List<CharSequence> = splitToSpannableLines(messageText)
        binding.subtitle.setSimpleText(lines[0])
        if (lines.size > 1) binding.selectionServiceType.text = lines[1]
        if (lines.size > 2) binding.selectionSocnetThirdLine.text = lines[2]
        return lines.size
    }

    private fun splitToSpannableLines(source: CharSequence, limit: Int = 3): List<CharSequence> {
        val strings = source.split("\n", limit = limit)
        return if (strings.size > 1) {
            val result = mutableListOf<CharSequence>()
            var index = 0
            for (i in 0 until min(limit, strings.size)) {
                result.add(source.subSequence(index, index + strings[i].length))
                index += strings[i].length + 1
            }
            result
        } else {
            listOf(source)
        }
    }
    //endregion
}

private const val DEFAULT_TEXT_MAX_LINES = 2