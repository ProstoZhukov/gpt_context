package ru.tensor.sbis.communicator.common.conversation.crud

import ru.tensor.sbis.attachments.generated.AttachmentActions
import ru.tensor.sbis.attachments.generated.AttachmentProperties
import ru.tensor.sbis.attachments.generated.ContentOperationInfo
import ru.tensor.sbis.attachments.generated.FileInfoViewModel
import ru.tensor.sbis.attachments.generated.ImageParams
import ru.tensor.sbis.attachments.generated.OperationError
import ru.tensor.sbis.attachments.generated.OperationErrorCode
import ru.tensor.sbis.attachments.generated.OperationState
import ru.tensor.sbis.attachments.generated.OperationType
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.profiles.generated.Gender
import ru.tensor.sbis.profiles.generated.Person
import ru.tensor.sbis.profiles.generated.PersonDecoration
import ru.tensor.sbis.profiles.generated.PersonName
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


/** Маппер из двоичного представления в объекты классов. **/
class MessageControllerBinaryMapper {

    fun map(listResult: ListResultOfBinaryMapOfStringString): ArrayList<Message> {
        return map(listResult.result)
    }

    fun map(list: List<ByteArray>): ArrayList<Message> {
        return list.map { readMessage(it) }.asArrayList()
    }

    fun map(data: ByteArray): Message {
        return readMessage(data)
    }

    private fun readMessage(data: ByteArray): Message {
        val buffer = ByteBuffer.wrap(data)
        return buffer.readMessage()
    }

    private fun ByteBuffer.readMessage(): Message {
        // версия протокола пока не используется, но прочитать ее в любом случае нужно
        @Suppress("UNUSED_VARIABLE") val protocolVersion = readInt()
        val uuid = readUUID()
        val timestamp = readLong()
        val syncStatus = readEnum<SyncStatus>()
        val timestampSent = readLong()
        val outgoing = readBoolean()
        val forMe = readBoolean()
        val removable = readEnum<MessageRemovableType>()
        val editable = readBoolean()
        val canCreateThread = readBoolean()
        val edited = readBoolean()
        val read = readBoolean()
        val readByMe = readBoolean()
        val readByReceiver = readBoolean()
        val receiverCount = readInt()
        val sender = readPerson()
        val receiverName = optional { readString() }
        val receiverLastName = optional { readString() }
        val content = readList { readMessageContentItem() }
        val rootElements = readList { readInt() }
        val attachmentCount = readInt()
        val isFinishedSignRequest = readBoolean()
        val quotable = readBoolean()
        val pinnable = readBoolean()
        val readTimestamp = optional { readLong() }
        val readTimestampMe = optional { readLong() }
        val textModel = readString()
        val serviceObject = readString()
        val serviceMessageGroup = optional { readServiceMessageGroup() }
        return Message(
            uuid,
            null,
            null,
            timestamp,
            syncStatus,
            timestampSent,
            outgoing,
            forMe,
            removable,
            editable,
            canCreateThread,
            edited,
            read,
            readByMe,
            readByReceiver,
            receiverCount,
            sender,
            receiverName,
            receiverLastName,
            content,
            rootElements,
            attachmentCount,
            isFinishedSignRequest,
            quotable,
            pinnable,
            readTimestamp,
            readTimestampMe,
            textModel,
            serviceObject,
            serviceMessageGroup,
            false,
            false
        )
    }

    private fun ByteBuffer.readInt(): Int {
        order(ByteOrder.LITTLE_ENDIAN)
        return int
    }

    private fun ByteBuffer.readLong(): Long {
        order(ByteOrder.LITTLE_ENDIAN)
        return long
    }

    private fun ByteBuffer.readUUID(): UUID {
        order(ByteOrder.BIG_ENDIAN)
        val hight = this.long
        val low = this.long
        return UUID(hight, low)
    }

    private fun ByteBuffer.readBoolean(): Boolean {
        return get() != 0.toByte()
    }

    private fun ByteBuffer.readPerson(): Person {
        val uuid = readUUID()
        val faceId = fake { optional { readLong() } }
        val personName = readPersonName()
        val photoUrl = optional { readString() }
        val photoDecoration = optional { readPersonDecoration() }
        val gender = readEnum<Gender>()
        val hasAccess = readBoolean()
        return Person(uuid, faceId, personName, photoUrl, photoDecoration, gender, hasAccess)
    }

    private fun ByteBuffer.readPersonName(): PersonName {
        val first = readString()
        val last = readString()
        val patronymic = readString()
        return PersonName(last, first, patronymic, "")
    }

    private fun ByteBuffer.readString(): String {
        val size = readInt()
        val byteArray = ByteArray(size)
        get(byteArray)
        // Размер массива char в два раза меньше чем массив byte (делим размер на 2)
        val charArray = CharArray(size shr 1)
        for (i in charArray.indices) {
            // Восстанавливаем исходный индекс (умножаем на 2)
            val bpos = i shl 1
            // Формируем char из двух byte. Один из байт идёт со смещением на 8 бит влево.
            charArray[i] = ((byteArray[bpos + 1].toUByte().toInt() shl 8) + byteArray[bpos].toUByte().toInt()).toChar()
        }
        return String(charArray)
    }

    private fun ByteBuffer.readPersonDecoration(): PersonDecoration {
        val backgroundColorHex = readString()
        val initials = readString()
        return PersonDecoration(initials, backgroundColorHex)
    }

    private fun ByteBuffer.readMessageContentItem(): MessageContentItem {
        val itemType = readEnum<MessageContentItemType>()
        val quote = optional { readQuote() }
        val linkUrl = optional { readString() }
        val attachment = optional { readAttachmentViewModel() }
        val signature = optional { readSignature() }
        val serviceType = optional { readEnum<ServiceType>() }
        val serviceMessageGroup = optional { readServiceMessageGroup() }
        val serviceMessage = optional { readServiceMessage() }
        val text = readString()
        val children = readList { readInt() }
        return MessageContentItem(itemType, quote, linkUrl, attachment, signature, serviceType, serviceMessageGroup, serviceMessage, text, children)
    }

    private fun ByteBuffer.readServiceMessage(): ServiceMessage {
        val text = readString()
        val activeChatClosedMessage = readBoolean()
        val personList = optional { readPersonList() }
        val type = readEnum<ServiceType>()
        val consultationIconType = readEnum<CrmConsultationIconType>()
        val cursorMessageId = optional { readUUID() }
        return ServiceMessage(type, text, activeChatClosedMessage, personList, consultationIconType, cursorMessageId)
    }

    private fun ByteBuffer.readPersonList(): PersonList {
        val brief = readString()
        val foldedCount = readInt()
        val unfoldedCount = readInt()
        return PersonList(brief, foldedCount, unfoldedCount)
    }

    private fun ByteBuffer.readServiceMessageGroup(): ServiceMessageGroup {
        val firstMessageUuid = readUUID()
        val text = readString()
        val hasMore = readBoolean()
        val folded = readBoolean()
        val messagesCount = readInt()
        val unfoldedMessagesLimit = readInt()
        val unreadCount = readInt()
        return ServiceMessageGroup(text, messagesCount, folded, hasMore, unfoldedMessagesLimit, firstMessageUuid, unreadCount)
    }

    private fun ByteBuffer.readSignature(): Signature {
        val signeeProfileUuid = readUUID()
        val title = readString()
        val certificateHash = readString()
        val isMine = readBoolean()
        val signeeCompany = readString()
        val signeeName = readPersonName()
        return Signature(signeeName, signeeCompany, signeeProfileUuid, isMine, certificateHash, title)
    }

    private fun ByteBuffer.readAttachmentViewModel(): AttachmentViewModel {
        val uuid = readUUID()
        val fileInfoViewModel = readFileInfoViewModel()
        return AttachmentViewModel(uuid, fileInfoViewModel)
    }

    private fun ByteBuffer.readFileInfoViewModel(): FileInfoViewModel {
        return FileInfoViewModel().apply {
            id = readUUID()
            attachId = readLong()
            redId = readLong()
            title = readString()
            fileExtension = optional { readString() }
            imageParams = optional { readImageParams() }
            signState = readEnum()
            signsCount = optional { readInt() }
            flags = readAttachmentProperties()
            actions = readAttachmentActions()
            localPath = optional { readString() }
            previewParams = optional { readString() }
            modifyDate = optional { readDate() }
            size = optional { readLong() }
            sbisDiskId = optional { readString() }
            previewUrl = optional { readString() }
            contentOperation = optional { readContentOperationInfo() }
            representationId = optional { readString() }
        }
    }

    private fun ByteBuffer.readQuote(): Quote {
        val messageUuid = readUUID()
        val dialogUuid = readUUID()
        val sender = readString()
        val timestamp = readLong()
        val senderNameFirst = readString()
        val senderNameLast = readString()
        return Quote(sender, timestamp, messageUuid, dialogUuid, senderNameFirst, senderNameLast)
    }

    private fun ByteBuffer.readContentOperationInfo(): ContentOperationInfo {
        val type: OperationType = readEnum()
        val status: OperationState = readEnum()
        val progress = optional { readInt() }
        val operationError = optional { readOperationError() }
        return ContentOperationInfo(type, status, progress, operationError)
    }

    private fun ByteBuffer.readOperationError(): OperationError {
        val statusCode: OperationErrorCode = readEnum()
        val errorMessage = readString()
        return OperationError(statusCode, errorMessage)
    }

    private fun ByteBuffer.readImageParams(): ImageParams {
        val width = readInt()
        val height = readInt()
        return ImageParams(width, height)
    }

    private fun ByteBuffer.readDate(): Date {
        val year = readShort().toInt()
        val month = readByte().toInt()
        val day = readByte().toInt()
        val hour = readByte().toInt()
        val minute = readByte().toInt()
        val second = readByte().toInt()
        val nanosecond = readInt() // необходимо вычитать чтобы не нарушить десериализацию
        return GregorianCalendar(year, month, day, hour, minute, second).time
    }

    private fun ByteBuffer.readShort(): Short {
        order(ByteOrder.LITTLE_ENDIAN)
        return short
    }

    private fun ByteBuffer.readByte(): Byte {
        order(ByteOrder.LITTLE_ENDIAN)
        return get()
    }

    private inline fun <T> ByteBuffer.optional(action: () -> T): T? =
        if (readBoolean()) {
            action()
        } else {
            null
        }

    /**
     * Имитировать чтение для пропуска неиспользуемых полей.
     */
    private inline fun <T> ByteBuffer.fake(action: () -> T): T? =
        null

    private inline fun <reified T : Enum<T>> ByteBuffer.readEnum(): T {
        val asInt = this.readInt()
        return enumValues<T>()[asInt]
    }

    private fun ByteBuffer.readAttachmentProperties(): AttachmentProperties {
        val asLong = this.readLong()
        return AttachmentProperties(asLong)
    }

    private fun ByteBuffer.readAttachmentActions(): AttachmentActions {
        val asLong = this.readLong()
        return AttachmentActions(asLong)
    }

    private inline fun <T> ByteBuffer.readList(readElementAction: () -> T): ArrayList<T> {
        val size = readInt()
        val result = ArrayList<T>(size)
        repeat(size) {
            val item = readElementAction()
            result.add(item)
        }
        return result
    }

    private inline fun <K, V> ByteBuffer.readMap(readKey: () -> K, readValue: () -> V): HashMap<K, V> {
        val size = readInt()
        val result = HashMap<K, V>(size)
        repeat(size) {
            val key = readKey()
            val value = readValue()
            result[key] = value
        }
        return result
    }
}
