package ru.tensor.sbis.widget_player.layout.inline

/**
 * @author am.boldinov
 */
internal sealed interface InlineMeasureStep {
    /**
     * Расстановка элементов рядом друг с другом
     */
    object Inline : InlineMeasureStep

    /**
     * Расстановка элементов рядом друг с другом и выравнивание строк по высоте
     */
    class Baseline(val lineHeight: Int) : InlineMeasureStep
}