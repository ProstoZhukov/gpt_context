package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.devices.settings.generated.SimplifiedSaleSettings as ControllerSimplifiedSaleSettings

/**
 * Модель настроек упрощённой продажи.
 * @property tileSettings настройки плиток
 * @property tilesForAllLevels настройка отображения плиток на всех уровнях прайс-листа (внутри папок)
 */
data class SimplifiedSaleSettings(
    var tileSettings: TileSettings,
    var tilesForAllLevels: Boolean,
) {
    companion object {
        /** @SelfDocumented */
        val stub = SimplifiedSaleSettings(
            TileSettings(
                showPrice = true,
                showPhoto = false,
                showNameWithPhoto = true,
            ),
            tilesForAllLevels = false,
        )
    }
}

/**
 * Маппер для преобразования из модели контроллера.
 */
fun ControllerSimplifiedSaleSettings.map() = SimplifiedSaleSettings(
    TileSettings(
        showPrice,
        showPhoto,
        showNameWithPhoto,
    ),
    tilesForAllLevels,
)

/**
 * Маппер для преобразования в модель контроллера.
 */
fun SimplifiedSaleSettings.map() = ControllerSimplifiedSaleSettings(
    tileSettings.showPrice,
    tileSettings.showPhoto,
    tileSettings.showNameWithPhoto,
    tilesForAllLevels,
)