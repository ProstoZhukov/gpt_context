package ru.tensor.sbis.catalog_decl.catalog

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Предоставляет UI компоненты для выбора папок из каталога.
 */
interface ChooseCatalogFoldersViewProvider : Feature {
    fun createChooseCatalogFolderFragment(
        searchQuery: String? = null,
        preSelectedFolders: Set<UUID> = emptySet(),
    ): Fragment
}