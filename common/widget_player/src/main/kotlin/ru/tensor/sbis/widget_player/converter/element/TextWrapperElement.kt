package ru.tensor.sbis.widget_player.converter.element

/**
 * Маркерный интерфейс, который сигнализирует о том, что содержимое элемента может содержать текст,
 * а сам элемент является контейнером.
 * Текст будет добавлен в иерархию автоматически.
 * Могут реализовывать только наследники [GroupWidgetElement].
 *
 * Пример: ["DocsEditor/documentHeader:View", {"id": "c-0children-0"}, "Новый SabyDoc"]
 *
 * @author am.boldinov
 */
interface TextWrapperElement