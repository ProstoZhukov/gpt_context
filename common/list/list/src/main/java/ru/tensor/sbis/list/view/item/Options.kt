package ru.tensor.sbis.list.view.item

/**
 * Возможные опции, для настройки поведения и внешнего вида элемента списка.
 *
 * @property clickAction Function0<Unit> событие по нажатию на весь элемент элементы.
 * @property level Int уровень в иерархии, с повышением уровня, увеличивается отступ слева.
 * @property isSticky Boolean должен ли элемент оставаться зафиксированным вверху списка при скроле.
 * @property isMovable Boolean можно ли начать перетаскивать элемент.
 * @property customSidePadding если false, то будет выставлен стандартный отступ слева и справа и вызовутся
 * [getSidePaddingDp] и [getLeftPaddingDp] соответственно, true - не будет. Следует передавать true если не нужен
 * стандартный отступ [sidePaddingByStandardDp] - да в стандарте он фиксирован, но на практике с помощью этого флага
 * можно кастомизировать таким способом.
 * @property customBackground Boolean если флаг не выставлен, то для ячейки будет добавлен фон в соответствии с темой.
 * @property isHighlightable Boolean должен ли элемент по указанной позиции оставаться выделенным после нажатия,
 * когда список отображается в мастер части на планшете.
 * @property isCollapsible Boolean может ли элемент сворачиваться/разворачиваться.
 * @property useCustomListeners Boolean использовать ли листенеры назначенные на прикладной стороне,
 * если [true] то [clickAction] и [longClickAction] игнорируются.
 */
interface ItemOptions {
    val clickAction: () -> Unit
    val longClickAction: () -> Unit
    val level: Int
    val isSticky: Boolean
    val isMovable: Boolean
    val customSidePadding: Boolean //нестандартный отступ слева и справа
    val customBackground: Boolean
    val isHighlightable: Boolean
    val isCollapsible: Boolean
    val useCustomListeners: Boolean
    fun isClickable(): Boolean
    fun getLeftPaddingDp(): Int
    fun getSidePaddingDp(): Int
    fun getTopPaddingDp(): Int
}

/**
 *  @see [ItemOptions]
 */
class Options(
    override var clickAction: () -> Unit = noClickAction,
    override val longClickAction: () -> Unit = noClickAction,
    override val level: Int = 0,
    override val isSticky: Boolean = false,
    override val isMovable: Boolean = false,
    override val customSidePadding: Boolean = false,
    override val customBackground: Boolean = false,
    override val isHighlightable: Boolean = true,
    override val isCollapsible: Boolean = false,
    override val useCustomListeners: Boolean = false,
) : ItemOptions {

    override fun isClickable() = clickAction != noClickAction

    override fun getLeftPaddingDp(): Int {
        return sidePaddingByStandardDp + level * levelPaddingByStandardDp
    }

    override fun getSidePaddingDp() =
        sidePaddingByStandardDp

    override fun getTopPaddingDp() = sidePaddingByStandardDp

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Options

        if (level != other.level) return false
        if (isSticky != other.isSticky) return false
        if (isMovable != other.isMovable) return false
        if (isClickable() != other.isClickable()) return false
        if (isHighlightable != other.isHighlightable) return false
        if (isCollapsible != other.isCollapsible) return false
        if (useCustomListeners != other.useCustomListeners) return false
        if (customSidePadding != other.customSidePadding) return false
        if (customBackground != other.customBackground) return false

        return true
    }

    override fun hashCode(): Int {
        var result = level
        result = 31 * result + isSticky.hashCode()
        result = 31 * result + isMovable.hashCode()
        result = 31 * result + isHighlightable.hashCode()
        result = 31 * result + isCollapsible.hashCode()
        result = 31 * result + useCustomListeners.hashCode()
        result = 31 * result + customSidePadding.hashCode()
        result = 31 * result + customBackground.hashCode()
        return result
    }

    companion object {
        val noClickAction: () -> Unit = {}
    }
}

/**
 * Контент в таблицах и блоках ограничен фиксированными отступами слева и справа 12pt
 * http://axure.tensor.ru/MobileStandart8/#p=%D1%82%D0%B0%D0%B1%D0%BB%D0%B8%D1%87%D0%BD%D0%BE%D0%B5_%D0%BF%D1%80%D0%B5%D0%B4%D1%81%D1%82%D0%B0%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5__%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F_02_&g=1
 */
private const val sidePaddingByStandardDp = 12

/*Отступ для вывода в иерархии*/
private const val levelPaddingByStandardDp = 24