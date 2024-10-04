package ru.tensor.sbis.design_tile_view

import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Модель изображения на плитке
 *
 * @param images набор [TileViewImage] для загрузки изображений коллажа
 * @param alignment расположение изображения на плитке
 * @param shape форма изображения
 * @param placeholder заглушка при отсутствии изображения, по умолчанию иконка [DEFAULT_PLACEHOLDER]
 * @param darkerImage требуется ли явно добавлять градиент поверх изображения для его затемнения (по умолчанию
 * добавляется только при наличии контента у плитки и использовании [SbisTileViewImageAlignment.FILL])
 * @param darkerPlaceholder требуется ли явно добавлять градиент для затемнения заглушки изображения (по умолчанию
 * совпадает с [darkerImage])
 *
 * @author us.bessonov
 */
data class SbisTileViewImageModel(
    internal val images: List<TileViewImage>,
    internal val alignment: SbisTileViewImageAlignment,
    internal val shape: SbisTileViewImageShape,
    internal val placeholder: SbisTileViewPlaceholder? = null,
    internal val darkerImage: Boolean = false,
    internal val darkerPlaceholder: Boolean = darkerImage
) {

    /**
     * Модель одиночного изображения на плитке
     *
     * @param image [TileViewImage] для загрузки изображения плитки.
     * При передаче `null` плитка будет без изображения
     * @param alignment расположение изображения на плитке
     * @param shape форма изображения
     */
    constructor(
        image: TileViewImage?,
        alignment: SbisTileViewImageAlignment,
        shape: SbisTileViewImageShape,
        placeholder: SbisTileViewPlaceholder? = null,
        darkerImage: Boolean = false,
        darkerPlaceholder: Boolean = darkerImage
    ) : this(image?.run(::listOf) ?: emptyList(), alignment, shape, placeholder, darkerImage, darkerPlaceholder)

    companion object {
        /** @SelfDocumented */
        val DEFAULT_PLACEHOLDER = SbisMobileIcon.Icon.smi_cameraBlack

        /** @SelfDocumented */
        val DEFAULT = SbisTileViewImageModel(TileViewImageUrl(""), SbisTileViewImageAlignment.TOP, Rectangle())
    }
}