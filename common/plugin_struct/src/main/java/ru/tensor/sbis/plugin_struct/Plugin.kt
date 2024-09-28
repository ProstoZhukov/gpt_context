package ru.tensor.sbis.plugin_struct

import android.app.Application
import android.content.Context
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureRegistry
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * Контрактный интерфейс модуля. Содержит описание публичного API и зависимостей.
 * Регистрация, настройка и инициализация плагинов производится в PluginManager.
 *
 * @author kv.martyshenko
 */
interface Plugin<C> {
    /**
     * Перечень публичного API предоставляемого модулем("фичи" предоставляемые модулем).
     */
    val api: Set<FeatureWrapper<out Feature>>

    /**
     * Зависимости, которые необходимо предоставить модулю для полноценной работы.
     */
    val dependency: Dependency

    /**
     * Дополнительная настройка поведения модуля.
     * Позволяет задавать логику поведения в зависимости от точки подключения(в какое приложение включается).
     *
     * Атрибут является опциональным. Указать Unit, если не требуется.
     */
    val customizationOptions: C

    /**
     * Метод для установки экземпляра приложения и темизированного контекста [BasePlugin.themedAppContext].
     * Будет вызыван самым первым и только один раз.
     */
    fun FeatureRegistry.setApplication(application: Application, themedContext: SbisThemedContext? = null) {}

    /**
     * Инициализация модуля.
     * На этапе инициализации не допускается прямое обращение к зависимостям, только через [FeatureProvider].
     * Доп.настройки, оформление подписок на события выполняйте в [doAfterInitialize].
     */
    fun initialize() {}

    /**
     * Вызов метода осуществляется после инициализации и разрешения зависимотей всех модулей.
     * Предназначен для выполнения доп.настроек модуля, оформления подписок на события и прочие действия,
     * которые должны осуществляться на старте модуля.
     */
    fun doAfterInitialize() {}
}