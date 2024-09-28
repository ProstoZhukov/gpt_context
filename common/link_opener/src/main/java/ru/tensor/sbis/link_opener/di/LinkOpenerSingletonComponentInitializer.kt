package ru.tensor.sbis.link_opener.di

import android.content.Context
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import ru.tensor.sbis.link_opener.contract.LinkOpenerDependency
import ru.tensor.sbis.link_opener.contract.LinkOpenerFeatureConfiguration

/**
 * Класс для создания и инициализации [LinkOpenerSingletonComponent].
 *
 * @param dependency зависимости модуля "Открытия документов по ссылкам".
 * Без предоставления зависимостей обработка интентов должна быть полностью на стороне клиента,
 * см. [OpenLinkController.processYourself].
 * @param configuration опциональная конфигурация использования компонента.
 */
internal class LinkOpenerSingletonComponentInitializer @JvmOverloads constructor(
    private val context: Context,
    private val dependency: LinkOpenerDependency,
    private val configuration: LinkOpenerFeatureConfiguration = LinkOpenerFeatureConfiguration.DEFAULT
) {
    /** @SelfDocumented */
    fun init() = createComponent()

    private fun createComponent(): LinkOpenerSingletonComponent =
        DaggerLinkOpenerSingletonComponent.factory().create(
            context = context,
            dependency = dependency,
            configuration = configuration
        )
}
