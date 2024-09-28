package ru.tensor.sbis.design.cloud_view.content.utils

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.WorkerThread
import ru.tensor.sbis.attachments.models.AttachmentModel
import ru.tensor.sbis.attachments.ui.utils.refresh.VisibleAttachmentsInteractor
import ru.tensor.sbis.attachments.ui.view.collage.AttachmentCollageView
import ru.tensor.sbis.attachments.ui.view.collage.card.AttachmentCollageCardView
import ru.tensor.sbis.attachments.ui.view.collage.util.getCollageAttachmentPoolKey
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.audio_player_view.view.message.AudioMessageView
import ru.tensor.sbis.design.cloud_view.CloudViewPlugin
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.cloud_view.content.MessageBlockView
import ru.tensor.sbis.design.cloud_view.content.certificate.CertificateView
import ru.tensor.sbis.design.cloud_view.content.certificate.Signature
import ru.tensor.sbis.design.cloud_view.content.container.ContainerView
import ru.tensor.sbis.design.cloud_view.content.grant_access.GrantAccessButtonsView
import ru.tensor.sbis.design.cloud_view.content.signing.SigningButtonsView
import ru.tensor.sbis.design.cloud_view.thread.CloudThreadView
import ru.tensor.sbis.design.utils.RecentlyUsedViewPool
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import timber.log.Timber
import java.util.LinkedList
import ru.tensor.sbis.design.R as RDesign

/**
 * Пул view элементов содержимого ячейки-облака, обеспечивающий возможность их повторного использования
 *
 * @author ma.kolpakov
 */
class MessagesViewPool(
    private val context: Context,
    private val messageResourcesHolder: MessageResourcesHolder
) {
    private val textViewPool = RecentlyUsedViewPool<TextView, String>(viewFactory = ::createTextView)
    private val attachmentViewPool =
        RecentlyUsedViewPool<AttachmentCollageView, Int>(viewFactory = ::createAttachmentView)
    private val certificateViewPool =
        RecentlyUsedViewPool<CertificateView, String>(viewFactory = ::createCertificateView)
    private val signingButtonViewList = LinkedList<SigningButtonsView>()
    private val grantAccessButtonsViewList = LinkedList<GrantAccessButtonsView>()
    private val containerViewList = LinkedList<ContainerView>()
    private val audioMessageViewList = LinkedList<AudioMessageView>()
    private val cloudThreadViewList = LinkedList<CloudThreadView>()

    private val attachmentCollageCardViewPool = RecentlyUsedViewPool<AttachmentCollageCardView, Int> {
        AttachmentCollageCardView(context).apply {
            visibleAttachmentsInteractor?.also(::initAutoRefresh)
        }
    }

    private val visibleAttachmentsInteractor: VisibleAttachmentsInteractor? =
        CloudViewPlugin.visibleAttachmentsInteractor

    /**
     * Возвращает прикладной цвет, соответствующий [CloudViewTextColorType]
     */
    @ColorInt
    fun getTextColor(@CloudViewTextColorType type: Int): Int {
        return messageResourcesHolder.getTextColor(type)
    }

    /**
     * Помещает view в пул
     */
    fun addView(view: View) {
        when (view) {
            is TextView -> textViewPool.recycle(view)
            is AttachmentCollageView -> attachmentViewPool.recycle(
                view.apply {
                    setAttachmentClickListener(null)
                    recycle()
                }
            )
            is CertificateView -> certificateViewPool.recycle(view)
            is ContainerView -> containerViewList.add(
                view.apply {
                    setLinkClickListener(null)
                }
            )
            is SigningButtonsView -> signingButtonViewList.add(
                view.apply {
                    setButtonClickListener(null)
                }
            )
            is GrantAccessButtonsView -> grantAccessButtonsViewList.add(
                view.apply {
                    setButtonClickListener(null)
                }
            )
            is AudioMessageView -> audioMessageViewList.add(
                view.apply {
                    recycle()
                }
            )
            is CloudThreadView -> cloudThreadViewList.add(view)
            else -> Timber.w(
                IllegalArgumentException(
                    "Unsupported view type ${view::class.java}. It ignored by the pool"
                )
            )
        }
    }

    /**
     * Заполнить view пулы контента [MessageBlockView].
     */
    @WorkerThread
    fun prefetch(
        textViewsCapacity: Int = MESSAGE_BLOCK_TEXT_VIEW_POOL_CAPACITY,
        containerViewsCapacity: Int = MESSAGE_BLOCK_CONTAINER_VIEW_POOL_CAPACITY,
        attachmentCollageViewsCapacity: Int = MESSAGE_BLOCK_ATTACHMENT_COLLAGE_VIEW_POOL_CAPACITY,
        attachmentCardViewsCapacity: Int = MESSAGE_BLOCK_ATTACHMENT_CARD_VIEW_POOL_CAPACITY,
        certificateViewsCapacity: Int = MESSAGE_BLOCK_CERTIFICATE_VIEW_POOL_CAPACITY,
        audioViewsCapacity: Int = MESSAGE_BLOCK_AUDIO_VIEW_POOL_CAPACITY
    ) {
        textViewPool.inflate(textViewsCapacity)
        attachmentViewPool.inflate(attachmentCollageViewsCapacity)
        attachmentCollageCardViewPool.inflate(attachmentCardViewsCapacity)
        certificateViewPool.inflate(certificateViewsCapacity)
        repeat(containerViewsCapacity) {
            addView(createContainerView())
        }
        repeat(audioViewsCapacity) {
            audioMessageViewList.add(createAudioMessageView())
        }
    }

    /**
     * Извлекает текстовый view из пула, либо создаёт новую.
     */
    internal fun getTextView(text: String) = textViewPool.get(text)

    /**
     * Извлекает view вложения из пула, либо создаёт новую.
     */
    internal fun getAttachmentView(attachments: List<AttachmentModel>) =
        attachmentViewPool.get(getAttachmentsKey(attachments))

    /**
     * Извлекает view подписи документа из пула, либо создаёт новую.
     */
    internal fun getCertificateView(signature: Signature) = certificateViewPool.get(signature.title)

    /**
     * Извлекает view контейнера элементов из пула, либо создаёт новую.
     */
    internal fun getContainerView() = containerViewList.poll() ?: createContainerView()

    /**
     * Извлекает view кнопок принятия и отклонения подписи из пула, либо создаёт новую.
     */
    internal fun getSigningButtonView() = signingButtonViewList.poll() ?: createSigningButtonsView()

    /**
     * Извлекает view аудиосообщения из пула, либо создаёт новую.
     */
    internal fun getAudioMessageView(): AudioMessageView =
        audioMessageViewList.poll() ?: createAudioMessageView()

    /**
     * Извлекает view треда из пула, либо создает новую.
     */
    internal fun getCloudThreadView(): CloudThreadView = cloudThreadViewList.poll() ?: createCloudThreadView()

    /**
     * Извлекает view кнопок разрешения и отклонения доступа к файлу из пула, либо создаёт новую.
     */
    internal fun getGrantAccessButtonView() = grantAccessButtonsViewList.poll() ?: createGrantAccessButtonsView()

    private fun createTextView() = TextView(context, null, RDesign.style.MessagesListItem_RegularText).apply {
        if (Build.VERSION.SDK_INT < 23) {
            setTextAppearance(context, RDesign.style.MessagesListItem_RegularText)
        } else {
            setTextAppearance(RDesign.style.MessagesListItem_RegularText)
        }
        typeface = TypefaceManager.getRobotoRegularFont(context)
        id = R.id.cloud_view_message_block_text_id
        ellipsize = TextUtils.TruncateAt.END
    }

    private fun createAttachmentView() = AttachmentCollageView(context).apply {
        id = R.id.cloud_view_message_block_attachment_preview_id
        viewPool = attachmentCollageCardViewPool
        visibleAttachmentsInteractor?.also(::initAutoRefresh)
    }

    private fun createCertificateView() = CertificateView(context).apply {
        setResourceHolder(messageResourcesHolder)
        id = R.id.cloud_view_message_block_certificate_id
    }

    private fun createContainerView() = ContainerView(context).apply {
        id = R.id.cloud_view_message_block_container_id
    }

    private fun createSigningButtonsView() = SigningButtonsView(context).apply {
        id = R.id.cloud_view_message_block_signing_buttons_id
    }

    private fun createGrantAccessButtonsView() = GrantAccessButtonsView(context).apply {
        id = R.id.cloud_view_message_block_grant_access_buttons_id
    }

    private fun createCloudThreadView() = CloudThreadView(context)

    private fun createAudioMessageView() =
        AudioMessageView(context).apply {
            id = R.id.cloud_view_message_block_audio_message_id
            isCard = false
            setBackgroundColor(getColorFromAttr(R.attr.CloudView_backgroundColor))
        }

    private fun getAttachmentsKey(attachments: List<AttachmentModel>) =
        attachments.map(::getCollageAttachmentPoolKey).hashCode()
}

private const val MESSAGE_BLOCK_TEXT_VIEW_POOL_CAPACITY = 15
private const val MESSAGE_BLOCK_CONTAINER_VIEW_POOL_CAPACITY = 15
private const val MESSAGE_BLOCK_ATTACHMENT_COLLAGE_VIEW_POOL_CAPACITY = 5
private const val MESSAGE_BLOCK_ATTACHMENT_CARD_VIEW_POOL_CAPACITY = 20
private const val MESSAGE_BLOCK_CERTIFICATE_VIEW_POOL_CAPACITY = 10
private const val MESSAGE_BLOCK_AUDIO_VIEW_POOL_CAPACITY = 10