package ru.tensor.sbis.widget_player.layout

/**
 * @author am.boldinov
 */
interface MultiLineView {

    fun getLineCount(): Int

    fun getLineWidth(line: Int): Int

    fun getLineHeight(line: Int): Int

    fun getLineTop(line: Int): Int

    fun getLineBottom(line: Int): Int

    fun getLeftLineIndent(lineNumber: Int): Int

    val beforeMeasureUpdater: BeforeMeasureUpdater

    val afterMeasureUpdater: AfterMeasureUpdater

    interface BeforeMeasureUpdater {
        fun updateLeftLineIndent(lineNumber: Int, indent: Int): Boolean
    }

    interface AfterMeasureUpdater {
        fun updateLineHeight(lineNumber: Int, height: Int): Boolean
    }

}