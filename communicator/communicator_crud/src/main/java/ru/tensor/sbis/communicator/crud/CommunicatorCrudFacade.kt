package ru.tensor.sbis.communicator.crud

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.crud.contract.CommunicatorCrudFeature
import ru.tensor.sbis.communicator.crud.theme.ThemeRepositoryProviderImpl
import ru.tensor.sbis.communicator.generated.ThemeController

/**
 * Фасад модуля communicator_crud.
 * Предоставляет фичи [CommunicatorCrudFeature].
 *
 * @author da.zhukov
 */
internal object CommunicatorCrudFacade : CommunicatorCrudFeature {

    override fun getThemeRepository(
        themeController: DependencyProvider<ThemeController>
    ): ThemeRepository =
        ThemeRepositoryProviderImpl().getThemeRepository(themeController)
}