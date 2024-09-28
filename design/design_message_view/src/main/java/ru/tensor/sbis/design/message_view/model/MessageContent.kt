package ru.tensor.sbis.design.message_view.model

import kotlinx.parcelize.RawValue
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import ru.tensor.sbis.communicator.generated.MessageContentItem
import ru.tensor.sbis.design.message_view.utils.equals

/**
 * Контент сообщения.
 *
 * @param content контент сообщения.
 * @param rootElements список элементов верхнего уровня в контенте сообщения.
 *
 * @author dv.baranov
 */
data class MessageContent(
    val content: @RawValue ArrayList<MessageContentItem>,
    val rootElements: ArrayList<Int>
) {
    override fun equals(other: Any?): Boolean = other is MessageContent &&
        rootElements.size == other.rootElements.size &&
        rootElements.containsAll(other.rootElements) &&
        content equals other.content

    private infix fun List<MessageContentItem>?.equals(other: List<MessageContentItem>?): Boolean =
        if (this != null && other != null) {
            var result = true
            if (size == other.size) {
                for (i in indices) {
                    if (other.getOrNull(i) equals this[i]) continue

                    result = false
                    break
                }
            } else {
                result = false
            }
            result
        } else {
            this == other
        }

    private infix fun MessageContentItem?.equals(other: MessageContentItem?): Boolean =
        if (this != null && other != null) {
            serviceMessageGroup equals other.serviceMessageGroup &&
                serviceMessage equals other.serviceMessage &&
                EqualsBuilder()
                    .append(itemType, other.itemType)
                    .append(quote, other.quote)
                    .append(linkUrl, other.linkUrl)
                    .append(attachment, other.attachment)
                    .append(signature, other.signature)
                    .append(serviceType, other.serviceType)
                    .append(text, other.text)
                    .append(children, other.children)
                    .isEquals
        } else {
            false
        }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(content)
            .append(rootElements)
            .toHashCode()
    }

}
