package ru.tensor.sbis.recorder.decl

/**
 * Инструмент для запроса получателей, [RecorderView] отправляет сообщение сразу после завершения записи,
 * поэтому получатель должен быть выбран перед записью.
 * @author ma.kolpakov
 */
interface RecordRecipientMediator {
    /**
     * Вызов блока кода с гарантией наличия получателя
     *
     * @param block блок кода будет вызван, если есть получатель сообщения. Сценарии запроса получателя
     * зависят от реализации
     */
    fun withRecipient(block: () -> Unit)
}