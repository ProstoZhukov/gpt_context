package ru.tensor.sbis.crud.devices.settings.model

/**
 * Модель настроек плиок упрощённой продажи.
 * @property showPrice отображать ли эмблему с ценой
 * @property showPhoto отображать ли фото
 * @property showNameWithPhoto отображать ли название товара в режиме отображения с фото
 */
data class TileSettings(
    var showPrice: Boolean,
    var showPhoto: Boolean,
    var showNameWithPhoto: Boolean,
)