package ru.tensor.sbis.design_dialogs.dialogs.container.tablet

/**
 * Тип закрепления якорем
 */
enum class AnchorType {
    /**
     * якорь располагается выше контента
     */
    TOP,
    /**
     * аналогично [AnchorType.TOP], но с наложением контента на якорь
     */
    TOP_WITH_OVERLAY,
    /**
     * якорь располагается ниже контента
     */
    BOTTOM,
    /**
     * расположение якоря относительно контента зависит от его расположения на экране
     */
    AUTO,
    /**
     * аналогично [AnchorType.AUTO], но с наложением контента на якорь
     */
    AUTO_WITH_OVERLAY,
    /**
     * аналогично [AnchorType.AUTO_WITH_OVERLAY], но наложение на якорь происходит только если содержимому не хватает
     * места
     */
    AUTO_WITH_OVERLAY_IF_NOT_ENOUGH_SPACE,

    /**
     * якорь располагается справа от контента
     */
    RIGHT
}