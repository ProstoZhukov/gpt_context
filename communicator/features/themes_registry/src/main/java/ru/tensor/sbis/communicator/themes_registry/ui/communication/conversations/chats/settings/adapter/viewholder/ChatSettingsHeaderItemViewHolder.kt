package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.common.util.PreviewerUrlUtil
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.base.ChatSettingsItemViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.drawable.ChatAvatarStubDrawable
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.view.input.base.ValidationStatus
import ru.tensor.sbis.design.view.input.text.TextInputView
import ru.tensor.sbis.fresco_view.util.superellipse.SuperEllipsePostprocessor
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.profile.R as RDesignProfile

/**
 * Холдер верхней части экрана настроек чата.
 * Изменение названия и фото чата, добавление новых администраторов.
 *
 * @author dv.baranov
 */
internal class ChatSettingsHeaderItemViewHolder(
    parentView: ViewGroup,
) : ChatSettingsItemViewHolder<ChatSettingsHeaderItem>(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.communicator_chat_settings_header_item, parentView, false),
) {
    private val avatarView: SimpleDraweeView = itemView.findViewById(R.id.communicator_chat_settings_header_avatar_photo)
    private val editChatNameView: TextInputView = itemView.findViewById(R.id.communicator_chat_settings_header_text_input)
    private val personListTitleView: SbisTextView = itemView.findViewById(R.id.communicator_chat_settings_header_person_list_title)
    private val addPersonsButton: Button = itemView.findViewById(R.id.communicator_chat_settings_header_add_person)

    private val avatarSideSize = itemView.resources.getDimensionPixelSize(RDesignProfile.dimen.design_profile_person_photo_view_size)
    private val postprocessor: SuperEllipsePostprocessor = SuperEllipsePostprocessor(
        itemView.context,
        RDesign.drawable.super_ellipse_mask,
    )

    override fun bind() {
        bindAvatarView()
        bindEditChatNameView()
        personListTitleView.setText(item.personListTitleViewTextRes)
        bindAddPersonButton()
    }

    private fun bindAvatarView() {
        avatarView.background = ChatAvatarStubDrawable(itemView.context)
        avatarView.setOnClickListener { item.onAvatarViewClick() }
        avatarView.setOnLongClickListener {
            item.onAvatarViewLongClick()
            true
        }
        updateAvatar()
    }

    private fun bindEditChatNameView() {
        editChatNameView.imeOptions = EditorInfo.IME_ACTION_DONE
        editChatNameView.onValueChanged = { _, name ->
            item.chatSettingsEditChatNameData.onValueChanged(name)
            changeValidationStatus()
        }
        editChatNameView.value = item.chatSettingsEditChatNameData.value
        changeValidationStatus()
        editChatNameView.isEnabled = item.chatSettingsEditChatNameData.isEnabled
        editChatNameView.readOnly = !item.chatSettingsEditChatNameData.isEnabled
    }

    private fun changeValidationStatus() {
        editChatNameView.validationStatus = item.chatSettingsEditChatNameData.validationErrorStatus?.let {
            if (editChatNameView.value.isEmpty()) it else ValidationStatus.Default(EMPTY)
        } ?: ValidationStatus.Default(EMPTY)
    }

    private fun bindAddPersonButton() {
        addPersonsButton.setOnClickListener { item.onAddButtonClick() }
        addPersonsButton.visibility = if (item.isAddButtonVisible) View.VISIBLE else View.INVISIBLE
    }

    private fun updateAvatar() {
        avatarView.setImageURI(item.avatarUrl)
        item.avatarUrl?.let {
            val uriString =
                if (UrlUtils.isSBISUrl(it)) {
                    PreviewerUrlUtil.replacePreviewerUrlPartWithCheck(
                        it,
                        avatarSideSize,
                        avatarSideSize,
                        PreviewerUrlUtil.ScaleMode.RESIZE,
                    )
                } else {
                    it
                }
            val imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uriString))
                .setPostprocessor(postprocessor)
                .build()
            val controller = Fresco.newDraweeControllerBuilder()
                .setOldController(avatarView.controller)
                .setImageRequest(imageRequest)
                .build()
            avatarView.controller = controller
        }
    }

    override fun recycle() {
        item.onRecycleHeaderItem()
        super.recycle()
    }
}
