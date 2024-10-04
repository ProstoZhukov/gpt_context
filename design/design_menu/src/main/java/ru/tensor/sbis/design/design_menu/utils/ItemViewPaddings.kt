package ru.tensor.sbis.design.design_menu.utils

import android.content.Context
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * Модель для хранения значений падингов для компонентов элемента меню в различных компоновках.
 *
 * @author ra.geraskin
 */
internal enum class ItemViewPaddings(private val offset: Offset? = null) {

    /** Внутренние паддинги элемента меню. */
    VIEW_START(offset = Offset.L),
    VIEW_END(offset = Offset.S),

    /** Отступы иконки, если она располагается СЛЕВА от заголовка. */
    LEFT_ICON_START(offset = Offset.X2S),
    LEFT_ICON_END(offset = Offset.X2S),

    /** Отступы маркера, если он располагается СЛЕВА от заголовка. */
    LEFT_MARKER_START,
    LEFT_MARKER_END(offset = Offset.X3S),

    /** Отступы Заголовка. */
    TITLE_START(offset = Offset.X2S),
    TITLE_END(offset = Offset.S),

    /** Отступы маркера, если он располагается СПРАВА от заголовка. */
    RIGHT_MARKER_START(offset = Offset.X3S),
    RIGHT_MARKER_END(offset = Offset.S),

    /** Отступы иконки, если она располагается СПРАВА от заголовка. */
    RIGHT_ICON_START,
    RIGHT_ICON_END,

    /** То же самое, что и выше, но применительно для меню, расположенного в шторке. */
    RIGHT_ICON_END_PANEL(offset = Offset.S),

    /** Отступы иконки стрелки-подменю. */
    ARROW_START(offset = Offset.X3S),
    ARROW_END(offset = Offset.S),

    /** Вертикальный (сверху и снизу) внутренний отступ элемента меню. */
    VIEW_VERTICAL(offset = Offset.ST);

    /** @SelfDocumented */
    fun get(ctx: Context): Int = offset?.getDimenPx(ctx) ?: 0

}

/*
                    Схема отступов компонентов элемента меню

      16                   4 заголовок
      16     4 иконка 4    4 заголовок
      16     0 маркер 2    4 заголовок

                             заголовок 8   2 маркер  8      8
                             заголовок 8   0 иконка  0      8
                             заголовок 8   2 стрелка 8      8
                             заголовок 8                    8

                      Для меню в шторке:
                             заголовок 8   0 иконка  12     8
 */