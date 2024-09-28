package ru.tensor.sbis.communication_decl.communicator.ui

/**
 * Перечисление, представляющее различные режимы отображения переписки.
 *
 * @author da.zhukov
 */
enum class ConversationViewMode {
    /**
     * Полнофункциональный режим для отображения обычной переписки.
     */
    FULL,

    /**
     * Режим предпросмотра без возможности перехода в карточку.
     */
    PREVIEW,

    /**
     * Режим предпросмотра с возможностью перехода в карточку.
     */
    EXPANDABLE_PREVIEW;
}