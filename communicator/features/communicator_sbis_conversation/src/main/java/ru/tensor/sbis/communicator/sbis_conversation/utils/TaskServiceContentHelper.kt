package ru.tensor.sbis.communicator.sbis_conversation.utils

import ru.tensor.sbis.communicator.generated.Message
import ru.tensor.sbis.communicator.generated.MessageContentItemType
import ru.tensor.sbis.communicator.generated.ServiceType

/**
 * Маппер контента сервисного сообщения о создании/прикреплении задачи, нужен чтобы отображать ссылку на задачу.
 * TODO: избавиться от данного объекта после выполнения задачи https://online.sbis.ru/opendoc.html?guid=612be49a-971a-4b65-9ae7-574229caf5f6&client=3.
 *
 * @author dv.baranov
 */
internal object TaskServiceContentHelper {

    fun getTaskServiceTextModel(inputMessage: Message): String? {
        if (inputMessage.content.size == 0) return null
        val content = inputMessage.content
        val serviceType = content[0].serviceType
        if (serviceType != ServiceType.TASK_APPENDED && serviceType != ServiceType.TASK_LINKED) {
            return null
        }
        val links = ArrayList<String>()
        for (i in 1 until content.size) {
            val item = content[i]
            if (item.itemType == MessageContentItemType.LINK && item.text.isNotEmpty()) {
                links.add(item.text)
            }
        }

        return when {
            links.isEmpty() -> null
            links.size > 1 -> {
                val textModel = StringBuilder("[[\"div\", ")
                for (i in links.indices) {
                    val linkBlock = links[i]
                    val link = linkBlock.substring(1, linkBlock.length - 1)
                    textModel.append(link)
                    if (i != links.size - 1) textModel.append("], [\"div\", ")
                }
                textModel.append("]]")
                textModel.toString()
            }
            else -> {
                links[0]
            }
        }
    }
}