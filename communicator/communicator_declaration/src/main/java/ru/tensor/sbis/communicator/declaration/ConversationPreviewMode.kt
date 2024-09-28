package ru.tensor.sbis.communicator.declaration

/**
 * Перечисление, представляющее различные режимы отображения предпросмотра переписки.
 *
 * @author da.zhukov
 */
enum class ConversationPreviewMode {

    /**
     * Режим предпросмотра без возможности перехода в карточку.
     */
    PREVIEW,

    /**
     * Режим предпросмотра с возможностью перехода в карточку.
     */
    EXPANDABLE_PREVIEW;
}