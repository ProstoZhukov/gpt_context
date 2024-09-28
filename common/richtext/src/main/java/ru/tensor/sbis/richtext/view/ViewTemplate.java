package ru.tensor.sbis.richtext.view;

/**
 * Шаблон обтекания компонентов в богатом тексте
 *
 * @author am.boldinov
 */
public enum ViewTemplate {
    INLINE, // обтекание внутри строки (между букв)
    INLINE_SIZE, // обтекание внутри строки, не превышающее высоту строки
    LEFT, // обтекание слева
    RIGHT, // обтекание справа
    CENTER // обтекание по центру (разделяет текст на параграфы)
}
