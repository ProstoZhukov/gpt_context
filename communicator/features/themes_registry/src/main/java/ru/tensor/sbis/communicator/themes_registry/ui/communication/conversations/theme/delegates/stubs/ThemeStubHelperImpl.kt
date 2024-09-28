package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.stubs

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.HAS_MORE_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.LIST_KEY
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Коды заглушек реестра диалогов/чатов.
 *
 * @property stringValue ключ ошибки.
 *
 * @author rv.krohalev
 */
internal enum class StubCodes(val stringValue: String) {

    /**
     * Ошибка бизнес-логики.
     */
    BL_ERROR("bl_error"),

    /**
     * Ошибка сети.
     */
    NETWORK_ERROR("network_error"),

    /**
     * Папка пуста.
     */
    EMPTY_FOLDER("empty_folder"),

    /**
     * Нет результатов по фильтру.
     */
    EMPTY_AT_FILTER("empty_at_filter"),

    /**
     * Путой реестр.
     */
    START_FIRST("start_first"),

    /**
     * Нет поисковых результатов.
     */
    NO_RESULTS("no_results"),

    /**
     * Остальные ошибки.
     */
    OTHER("other");

    /**
     * Маппинг кода заглушки в тип заглушки реестра.
     */
    fun toStub(isChat: Boolean = false, isSharingMode: Boolean): Stubs = when (this) {
        BL_ERROR        -> Stubs.ERROR
        NETWORK_ERROR   -> Stubs.NO_CONNECTION
        EMPTY_FOLDER    -> Stubs.EMPTY_FOLDER
        EMPTY_AT_FILTER -> Stubs.EMPTY_AT_FILTER
        START_FIRST     -> when {
            isChat -> if (isSharingMode) Stubs.SHARING_CHATS_NO_MESSAGES else Stubs.CHATS_NO_MESSAGES
            else   -> if (isSharingMode) Stubs.SHARING_DIALOGS_NO_MESSAGES else Stubs.DIALOGS_NO_MESSAGES
        }
        NO_RESULTS      -> Stubs.NOT_FOUND
        OTHER           -> Stubs.ERROR
    }
}

/**
 * Типы заглушек реестра диалогов/чатов.
 */
internal enum class Stubs {
    /**
     * Ошибка.
     */
    ERROR,

    /**
     * Нет сети.
     */
    NO_CONNECTION,

    /**
     * Нет найденных результатов.
     */
    NOT_FOUND,

    /**
     * Реестр диалогов пуст.
     */
    DIALOGS_NO_MESSAGES,

    /**
     * Реестр чатов пуст.
     */
    CHATS_NO_MESSAGES,

    /**
     * Реестр диалогов при шаринге пуст.
     */
    SHARING_DIALOGS_NO_MESSAGES,

    /**
     * Реестр чатов при шаринге пуст.
     */
    SHARING_CHATS_NO_MESSAGES,

    /**
     * Папка пуста.
     */
    EMPTY_FOLDER,

    /**
     * Нет результатов по фильтру.
     */
    EMPTY_AT_FILTER;

    /**
     * Маппинг типа заглушки в модель контента компонента заглушек.
     */
    fun toStubCaseContent(actions: Map<Int, () -> Unit>): StubViewContent = when (this) {
        ERROR -> StubViewCase.SBIS_ERROR.getContent(actions)
        NO_CONNECTION -> StubViewCase.NO_CONNECTION.getContent(actions)
        NOT_FOUND -> StubViewCase.NO_SEARCH_RESULTS.getContent(actions)
        DIALOGS_NO_MESSAGES,
        CHATS_NO_MESSAGES -> StubViewCase.NO_MESSAGES.getContent(actions)
        SHARING_DIALOGS_NO_MESSAGES -> createSharingContent(false)
        SHARING_CHATS_NO_MESSAGES -> createSharingContent(true)
        EMPTY_FOLDER -> ImageStubContent(
            StubViewImageType.NOT_FOUND,
            null,
            RCommunicatorDesign.string.communicator_stub_view_dialogs_nothing_in_folder
        )
        EMPTY_AT_FILTER -> StubViewCase.NO_FILTER_RESULTS.getContent(actions)
    }

    private fun createSharingContent(isChannels: Boolean): StubViewContent {
        @StringRes val messageRes = if (isChannels) {
            RCommunicatorDesign.string.communicator_stub_view_channels_no_messages
        } else {
            RCommunicatorDesign.string.communicator_stub_view_dialogs_no_messages
        }

        return ImageStubContent(
            imageType = StubViewImageType.NO_MESSAGES,
            messageRes = messageRes,
            details = null
        )
    }
}

/**
 * Реализация хелпера заглушек для реестров диалогов и чатов.
 *
 * @author da.zhukov
 */
internal class ThemeStubHelperImpl(private val isSharingMode: Boolean) : ThemeStubHelper {

    companion object {
        const val DUMMY_CODE = "dummy_code"
    }
    
    private var stub: Stubs? = null
    private val cache = mutableMapOf<Boolean, Stubs?>()

    override val currentStub: Stubs?
        get() = stub

    override fun createStub(metadata: Map<String, String>, isChatTab: Boolean): Stubs? {
        getStubCode(metadata).let {
            val isListCall = metadata[LIST_KEY]?.toBoolean() == true
            val hasMore = metadata[HAS_MORE_KEY]?.toBoolean() == true
            // Не сбрасываем текущую заглушку по пустой заглушке или заглушке с hasMore == true из вызова list
            // Заглушки приходят в рефрешах, в листе может прийти заглушка только если нет сети.
            if (isListCall && (it == null || hasMore)) {
                return stub
            }
            stub = it?.toStub(isChat = isChatTab, isSharingMode = isSharingMode)
            return stub
        }
    }

    override fun stubFromMetadata(metadata: Map<String, String>, isChatTab: Boolean): Stubs? {
        return getStubCode(metadata)?.toStub(isChatTab, isSharingMode)
    }

    override fun cacheCurrentStub(forChannels: Boolean) {
        cache[forChannels] = stub
    }

    override fun restoreCurrentStub(forChannels: Boolean) {
        stub = cache[forChannels]
    }

    private fun getStubCode(metadata: Map<String, String>) =
        StubCodes.values().firstOrNull { metadata[DUMMY_CODE] == it.stringValue }
}