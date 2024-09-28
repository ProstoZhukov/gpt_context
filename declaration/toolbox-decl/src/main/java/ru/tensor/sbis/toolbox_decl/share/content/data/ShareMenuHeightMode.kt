package ru.tensor.sbis.toolbox_decl.share.content.data

/**
 * Режим измерения высоты контейнера контента в компоненте меню "поделиться".
 *
 * @author vv.chekurda
 */
sealed interface ShareMenuHeightMode {

    /**
     * Контент занимает всю доступную высоту.
     */
    object Full : ShareMenuHeightMode

    /**
     * Контент оборачивается, минимальный размер искусственно ограничен.
     */
    object Short : ShareMenuHeightMode
}