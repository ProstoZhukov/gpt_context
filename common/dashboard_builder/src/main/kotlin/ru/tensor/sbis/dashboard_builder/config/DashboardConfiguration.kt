package ru.tensor.sbis.dashboard_builder.config

import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.widget_player.config.ConfigurationBuilder
import ru.tensor.sbis.widget_player.config.WidgetConfiguration
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.id
import ru.tensor.sbis.dashboard_builder.R

/**
 * Конфигурация дашборда и базовых виджетов.
 *
 * @author am.boldinov
 */
class DashboardConfiguration private constructor(
    internal val widgets: WidgetConfiguration
) {

    companion object {

        private val defaultConfiguration by lazy {
            create()
        }

        fun getDefault(): DashboardConfiguration {
            return defaultConfiguration
        }

        /**
         * Создает конфигурацию дашборда и базовых виджетов.
         *
         * @param context темизированный контекст.
         * Необходимо передать в случае если тема конкретной активити может отличаться от темы приложения.
         * @param init билдер для конфигурации виджетов: добавление прикладных виджетов, изменений опций и свойств базовых виждетов.
         */
        fun create(
            context: SbisThemedContext? = null,
            init: (ConfigurationBuilder.() -> Unit) = {}
        ): DashboardConfiguration {
            val builder: (ConfigurationBuilder.() -> Unit) = {
                options {
                    rootLayout {
                        paddingTop = DimenRes.id(R.dimen.dashboard_root_layout_padding_top)
                        paddingLeft = DimenRes.id(R.dimen.dashboard_root_layout_padding_left)
                        paddingBottom = DimenRes.id(R.dimen.dashboard_root_layout_padding_bottom)
                        paddingRight = DimenRes.id(R.dimen.dashboard_root_layout_padding_right)
                        columnGap = DimenRes.id(R.dimen.dashboard_root_layout_column_gap)
                    }
                }
                init.invoke(this)
            }
            return DashboardConfiguration(
                widgets = context?.let {
                    WidgetConfiguration.create(context = it, init = builder)
                } ?: WidgetConfiguration.create(init = builder)
            )
        }
    }
}