package ru.tensor.sbis.widget_player.widget.list.root

import android.graphics.Paint
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.store.getAsInt

/**
 * @author am.boldinov
 */
internal class ListViewElementFactory(
    private val options: ListViewOptions
) : WidgetElementFactory<ListViewElement> {

    override fun create(tag: String, attributes: WidgetAttributes, environment: WidgetEnvironment): ListViewElement {
        val startIndex = attributes.getAsInt("startIndex")?.let {
            maxOf(it - 1, 0)
        } ?: 0
        val remoteConfig = attributes.get("markerConfigId")?.let {
            parseConfig(it, environment.resources)
        }
        val defaultConfig = remoteConfig ?: getDefaultConfig(environment.resources)
        return ListViewElement(tag, attributes, environment.resources, remoteConfig, defaultConfig, startIndex)
    }

    private fun parseConfig(
        configId: String,
        resources: WidgetResources
    ): ListViewConfig {
        return when (configId) {
            "numbers" -> NumberListViewConfig(
                markerSize = options.markerSize,
                numberSize = resources.globalStyle.fontSize,
                color = resources.globalStyle.textColor,
                fontWeight = resources.globalStyle.fontWeight
            )

            "checkboxes" -> CheckboxListViewConfig(
                markerSize = options.markerSize
            )

            else -> getDefaultConfig(resources)
        }
    }

    private fun getDefaultConfig(resources: WidgetResources): ListViewConfig {
        return LevelListViewConfig(
            markerSize = options.markerSize,
            levels = arrayOf(
                CircleListViewConfig(
                    options.markerSize,
                    color = resources.globalStyle.textColor,
                    style = Paint.Style.FILL,
                    size = options.bulletSize
                ),
                CircleListViewConfig(
                    options.markerSize,
                    color = resources.globalStyle.textColor,
                    style = Paint.Style.STROKE,
                    size = options.bulletSize
                ),
                SquareListViewConfig(
                    markerSize = options.markerSize,
                    color = resources.globalStyle.textColor,
                    style = Paint.Style.FILL,
                    size = options.bulletSize
                )
            )
        )
    }
}