package ru.tensor.sbis.recorder.decl

/**
 * Инструмент для запроса полномочий, которые необходимы для работы RecorderView
 *
 * @author vv.chekurda
 * Создан 8/7/2019
 */
interface RecordPermissionMediator {

    /**
     * Вызов блока кода с гарантией наличия полномочий
     *
     * @param block блок кода не будет вызван, если полномочия не предоставлены. Сценарии автоматического запроса
     * зависят от реализации
     */
    fun withPermission(block: () -> Unit)
}
