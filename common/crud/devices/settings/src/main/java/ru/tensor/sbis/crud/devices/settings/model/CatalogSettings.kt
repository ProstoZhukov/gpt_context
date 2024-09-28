package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList
import ru.tensor.devices.settings.generated.CatalogSettings as ControllerCatalogSettings

/**
 * Модель настроек каталога для точки продаж.
 *
 * @property excludedFolderUuids список идентификаторов недоступных разделов каталога
 */
@Parcelize
data class CatalogSettings(
    val excludedFolderUuids: ArrayList<UUID>,
) : Parcelable {
    companion object {
        /** @SelfDocumented */
        fun stub() = CatalogSettings(
            excludedFolderUuids = ArrayList(),
        )
    }
}

/**
 * Маппер для преобразования из модели контроллера.
 */
fun ControllerCatalogSettings.map() = CatalogSettings(
    excludedFolderUuids = denyFolders,
)

/**
 * Маппер для преобразования в модель контроллера.
 */
fun CatalogSettings.map() = ControllerCatalogSettings(
    excludedFolderUuids,
    null
)
