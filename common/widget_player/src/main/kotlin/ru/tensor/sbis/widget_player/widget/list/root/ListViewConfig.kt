package ru.tensor.sbis.widget_player.widget.list.root

import android.graphics.Paint
import ru.tensor.sbis.widget_player.converter.style.FontSize
import ru.tensor.sbis.widget_player.converter.style.FontWeight
import ru.tensor.sbis.widget_player.converter.style.TextColor
import ru.tensor.sbis.widget_player.res.dimen.DimenRes

/**
 * @author am.boldinov
 */

internal sealed interface ListViewConfig {
    val markerSize: DimenRes
}

interface BulletListViewConfig {
    val color: TextColor
    val style: Paint.Style
    val size: DimenRes
}

internal data class CircleListViewConfig(
    override val markerSize: DimenRes,
    override val color: TextColor,
    override val style: Paint.Style,
    override val size: DimenRes
) : ListViewConfig, BulletListViewConfig

internal data class SquareListViewConfig(
    override val markerSize: DimenRes,
    override val color: TextColor,
    override val style: Paint.Style,
    override val size: DimenRes
) : ListViewConfig, BulletListViewConfig

internal data class NumberListViewConfig(
    override val markerSize: DimenRes,
    val numberSize: FontSize,
    val color: TextColor,
    val fontWeight: FontWeight
) : ListViewConfig

internal data class CheckboxListViewConfig(
    override val markerSize: DimenRes
) : ListViewConfig

internal data class LevelListViewConfig(
    override val markerSize: DimenRes,
    val levels: Array<ListViewConfig>
) : ListViewConfig {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LevelListViewConfig

        if (markerSize != other.markerSize) return false
        if (!levels.contentEquals(other.levels)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = markerSize.hashCode()
        result = 31 * result + levels.contentHashCode()
        return result
    }
}