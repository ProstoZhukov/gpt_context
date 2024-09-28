package ru.tensor.sbis.toolbox_decl.linkopener

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinksData

/**
 * Предоставляет данные о доступных ссылках для открытия в приложении.
 *
 * @author us.bessonov
 */
interface SupportedLinksProvider : Feature {

    /** @SelfDocumented */
    suspend fun getAvailableLinks(): List<LinksData>
}
