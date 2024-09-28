package ru.tensor.sbis.red_button.ui.settings_item.di

import dagger.Component
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.common.util.di.PerActivity
import ru.tensor.sbis.red_button.di.RedButtonModule
import ru.tensor.sbis.red_button.ui.settings_item.RedButtonItemViewModel

/**
 * Компонент для пункта "Красной Кнопки" в настройках.
 * Компонент создан, чтобы упростить API пункта настроек, т.к. пункт настроек умеет ходить в контроллер,
 * обновляться при события с rxBus, обрабатывать ошибки модуля red_button.
 * Классы ошибок, событий обновлений пункта настроек и т.д. не должны быть снаружи
 *
 * @author ra.stepanov
 */
@PerActivity
@Component(
    dependencies = [CommonSingletonComponent::class],
    modules = [RedButtonModule::class]
)
interface RedButtonItemComponent {

    /** @SelfDocumented */
    val viewModel: RedButtonItemViewModel
}