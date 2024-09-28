/**
 * Инструменты для подготовки view контента для "Ячейки-облачка"
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.cloud_view.content.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.annotation.Px
import ru.tensor.sbis.attachments.ui.view.clickhandler.AttachmentUploadActionsHandler
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.cloud_view.content.grant_access.GrantAccessActionListener
import ru.tensor.sbis.design.cloud_view.content.link.LinkClickListener
import ru.tensor.sbis.design.cloud_view.content.signing.SigningActionListener
import ru.tensor.sbis.design.cloud_view.model.AttachmentCloudContent
import ru.tensor.sbis.design.cloud_view.model.CloudContent
import ru.tensor.sbis.design.cloud_view.model.CloudViewData
import ru.tensor.sbis.design.cloud_view.model.ContainerCloudContent
import ru.tensor.sbis.design.cloud_view.model.EmptyCloudContent
import ru.tensor.sbis.design.cloud_view.model.GrantAccessButtonsCloudContent
import ru.tensor.sbis.design.cloud_view.model.QuoteCloudContent
import ru.tensor.sbis.design.cloud_view.model.ServiceCloudContent
import ru.tensor.sbis.design.cloud_view.model.SignatureCloudContent
import ru.tensor.sbis.design.cloud_view.model.SigningButtonsCloudContent
import ru.tensor.sbis.design.cloud_view.model.AudioMessageCloudContent
import ru.tensor.sbis.design.cloud_view.model.LinkCloudContent
import ru.tensor.sbis.design.cloud_view.model.TaskLinkedServiceCloudContent
import ru.tensor.sbis.design.theme.global_variables.FontSize

internal fun CloudContent.toContentView(
    cloudViewData: CloudViewData,
    isOutcome: Boolean,
    contentList: List<CloudContent>,
    messageClickListener: OnClickListener?,
    longClickListener: OnLongClickListener?,
    linkClickListener: LinkClickListener?,
    @Px topMargin: Int,
    @Px messageWidth: Int,
    viewPool: MessagesViewPool,
    maxVisibleAttachmentsCount: Int? = null,
    showAttachmentsUploadProgress: Boolean = false,
    attachmentUploadActionsHandler: AttachmentUploadActionsHandler? = null
): Pair<View, LayoutParams>? {
    val view: View
    val layoutParams: LayoutParams
    return when (this) {
        EmptyCloudContent, is LinkCloudContent, is QuoteCloudContent -> {
            // этот тип контента сейчас устанавливаются через RichText
            null
        }
        is AttachmentCloudContent -> {
            val allAttachments = contentList.filterIsInstance<AttachmentCloudContent>()
            // при встрече первого вложения создаётся коллаж, поэтому остальные игнорируем
            if (this != allAttachments.first()) return null
            view = setUpAttachmentCollageView(
                allAttachments,
                viewPool,
                maxVisibleAttachmentsCount,
                showAttachmentsUploadProgress,
                attachmentUploadActionsHandler
            )
            layoutParams = createLayoutParams(view, topMargin)
            view to layoutParams
        }
        is ServiceCloudContent -> {
            view = setUpServiceView(this, cloudViewData, viewPool)
            layoutParams = createLayoutParams(view, topMargin, messageWidth)
            view.setOnClickListener(messageClickListener)
            view.setOnLongClickListener(longClickListener)
            view to layoutParams
        }
        is TaskLinkedServiceCloudContent -> {
            view = setUpTaskLinkedServiceView(this, cloudViewData, viewPool)
            layoutParams = createLayoutParams(view, topMargin, messageWidth)
            view to layoutParams
        }
        is SignatureCloudContent -> {
            view = setUpCertificateView(this, viewPool)
            val signatureTopMargin =
                if (this == contentList.first { it is SignatureCloudContent }) {
                    view.resources.getDimensionPixelSize(R.dimen.cloud_view_certificate_view_margin_top)
                } else {
                    0
                }
            layoutParams = createLayoutParams(view, signatureTopMargin, LayoutParams.MATCH_PARENT)
            view.setOnClickListener(messageClickListener)
            view.setOnLongClickListener(longClickListener)
            view to layoutParams
        }
        is ContainerCloudContent -> {
            view = setUpContainerView(
                this,
                contentList,
                cloudViewData,
                isOutcome,
                linkClickListener,
                viewPool
            )
            layoutParams = createLayoutParams(view, topMargin)
            view to layoutParams
        }
        is SigningButtonsCloudContent -> {
            view = setUpSigningButtons(this, viewPool, messageClickListener)
            val signingButtonsTopMargin =
                view.resources.getDimensionPixelSize(R.dimen.cloud_view_signing_buttons_margin_top)
            layoutParams = createLayoutParams(view, signingButtonsTopMargin, LayoutParams.MATCH_PARENT)
            view to layoutParams
        }
        is GrantAccessButtonsCloudContent -> {
            view = setUpGrantAccessButtons(this, viewPool, messageClickListener)
            val signingButtonsTopMargin =
                view.resources.getDimensionPixelSize(R.dimen.cloud_view_signing_buttons_margin_top)
            layoutParams = createLayoutParams(view, signingButtonsTopMargin, LayoutParams.MATCH_PARENT)
            view to layoutParams
        }
        is AudioMessageCloudContent -> {
            view = setAudioMessageView(this, viewPool)
            layoutParams = createLayoutParams(view, topMargin, width = ViewGroup.LayoutParams.MATCH_PARENT)
            view to layoutParams
        }
    }
}

@JvmOverloads
fun createLayoutParams(
    view: View,
    marginTop: Int,
    @Px width: Int = LayoutParams.WRAP_CONTENT,
    @Px height: Int = LayoutParams.WRAP_CONTENT
): LayoutParams {
    val oldParams = view.layoutParams
    val params: LayoutParams
    if (oldParams is LayoutParams) {
        params = oldParams
        params.width = width
    } else {
        params = LayoutParams(width, height)
    }
    params.topMargin = marginTop
    return params
}

/** @SelfDocumented */
internal fun hasAttachments(cloudViewData: CloudViewData) = cloudViewData.content.any { it is AttachmentCloudContent }

private fun setUpAttachmentCollageView(
    attachments: List<AttachmentCloudContent>,
    viewPool: MessagesViewPool,
    maxVisibleAttachmentsCount: Int? = null,
    showAttachmentsUploadProgress: Boolean = false,
    uploadActionsHandler: AttachmentUploadActionsHandler? = null
): View = viewPool.getAttachmentView(attachments.map { it.attachment }).apply {
    maxVisibleAttachmentsCount?.let { count -> setMaxVisibleCount(count) }
    setUploadProgressEnabled(isEnabled = showAttachmentsUploadProgress)
    setUploadActionsHandler(uploadActionsHandler)
    setCollageData(attachments.toVmList(context))
    setAttachmentClickListener(AttachmentCollageItemClickHandler(context, attachments))
}

private fun setUpServiceView(
    content: ServiceCloudContent,
    cloudViewData: CloudViewData,
    viewPool: MessagesViewPool
): View = viewPool.getTextView(content.text).apply {
    visibility = View.VISIBLE
    text = content.text
    setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSize.M.getScaleOffDimen(context))
    if (cloudViewData.isDisabledStyle) {
        setTextColor(viewPool.getTextColor(DISABLED_TEXT))
    } else {
        setTextColor(content.textColor)
    }
}

private fun setUpTaskLinkedServiceView(
    content: TaskLinkedServiceCloudContent,
    cloudViewData: CloudViewData,
    viewPool: MessagesViewPool
): View = viewPool.getTextView(content.text).apply {
    visibility = View.VISIBLE
    text = content.text
    if (cloudViewData.isDisabledStyle) {
        setTextColor(viewPool.getTextColor(DISABLED_TEXT))
    } else {
        setTextColor(content.textColor)
    }
}

private fun setUpCertificateView(
    content: SignatureCloudContent,
    viewPool: MessagesViewPool
): View = viewPool.getCertificateView(content.signature).apply {
    setCertificate(content.signature)
}

private fun setUpContainerView(
    content: ContainerCloudContent,
    contentList: List<CloudContent>,
    cloudViewData: CloudViewData,
    isOutcome: Boolean,
    linkClickListener: LinkClickListener?,
    viewPool: MessagesViewPool
): View = viewPool.getContainerView().apply {
    setViewPool(viewPool)
    setLinkClickListener(linkClickListener)
    setMessage(cloudViewData, isOutcome)
    setMessageEntitiesList(contentList, content.children, LayoutParams.WRAP_CONTENT)
}

private fun setUpSigningButtons(
    content: SigningButtonsCloudContent,
    viewPool: MessagesViewPool,
    messageClickListener: OnClickListener?
): View = viewPool.getSigningButtonView().apply {
    val listener = if (content.actionListener == null && messageClickListener != null) {
        object : SigningActionListener {
            override fun onAcceptClicked() {
                messageClickListener.onClick(this@apply)
            }

            override fun onDeclineClicked() {
                messageClickListener.onClick(this@apply)
            }
        }
    } else {
        content.actionListener
    }
    setButtonClickListener(listener)
    showRejectProgress(false)
}

private fun setUpGrantAccessButtons(
    content: GrantAccessButtonsCloudContent,
    viewPool: MessagesViewPool,
    messageClickListener: OnClickListener?
): View = viewPool.getGrantAccessButtonView().apply {
    val listener = if (content.actionListener == null && messageClickListener != null) {
        object : GrantAccessActionListener {
            override fun onGrantAccessClicked(sender: View) {
                messageClickListener.onClick(this@apply)
            }

            override fun onDenyAccessClicked() {
                messageClickListener.onClick(this@apply)
            }
        }
    } else {
        content.actionListener
    }
    setButtonClickListener(listener)
    showRejectProgress(false)
    showAcceptProgress(false)
}

private fun setAudioMessageView(
    content: AudioMessageCloudContent,
    viewPool: MessagesViewPool
): View = viewPool.getAudioMessageView().apply {
    data = content.data
    actionListener = content.actionListener
}

private fun List<AttachmentCloudContent>.toVmList(context: Context) = mapIndexed { i, content ->
    val previewSize = if (i == 0 && (size == 1 || size == 3)) {
        R.dimen.cloud_view_attachments_collage_placeholder_size_big
    } else {
        R.dimen.cloud_view_attachments_collage_placeholder_size_small
    }
    getCollageAttachmentCardVmMapper(context).map(
        content.attachment,
        previewSize,
        previewSize,
        optimizedSearchPreviews = true
    )
}