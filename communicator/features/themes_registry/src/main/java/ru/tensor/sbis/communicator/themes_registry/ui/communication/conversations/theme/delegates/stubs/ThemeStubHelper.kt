package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.stubs

import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Хелпер заглушек для реестров диалогов и чатов.
 *
 * @author da.zhukov
 */
internal interface ThemeStubHelper {

    /**
     * Текущая заглушка.
     */
    val currentStub: Stubs?

    /**
     * Создание заглушки.
     */
    fun createStub(metadata: Map<String, String>, isChatTab: Boolean): Stubs?

    /**
     * Проверка наличия заглушки в метаданных. Если заглушки нет вернет null.
     */
    fun stubFromMetadata(metadata: Map<String, String>, isChatTab: Boolean): Stubs?

    /**
     * Закешировать заглушку.
     */
    fun cacheCurrentStub(forChannels: Boolean)

    /**
     * Восстановить заглушку из кеша.
     */
    fun restoreCurrentStub(forChannels: Boolean)
}